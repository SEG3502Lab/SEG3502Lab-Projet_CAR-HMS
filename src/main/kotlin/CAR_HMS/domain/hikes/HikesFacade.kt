package CAR_HMS.domain.hikes

import CAR_HMS.domain.common.Difficulty
import CAR_HMS.domain.common.DomainEventEmitter
import CAR_HMS.domain.common.DomainException
import CAR_HMS.domain.members.HikerId
import CAR_HMS.domain.trails.TrailId
import org.springframework.stereotype.Service
import java.time.Duration
import java.time.LocalDateTime

/** Vue en lecture d'une randonnée, exposée à la couche application. */
data class HikeDetails(
    val id: HikeId,
    val title: String,
    val trailId: TrailId,
    val startDateTime: LocalDateTime,
    val status: HikeStatus,
    val remainingSpots: Int,
    val confirmedHikerIds: List<HikerId>,
    val organizerId: HikerId
)

data class NewHike(
    val organizerId: HikerId,
    val title: String,
    val description: String,
    val trailId: TrailId,
    val startDateTime: LocalDateTime,
    val expectedDuration: Duration,
    val registrationDeadline: LocalDateTime,
    val capacity: Int
)

data class SignUpResult(val signUpId: SignUpId, val status: SignUpStatus, val waitlistPosition: Int?)

@Service
class HikesFacade(
    private val hikes: HikeRepository,
    private val hikeFactory: HikeFactory,
    private val signUpFactory: SignUpFactory,
    private val planningPolicy: HikePlanningPolicy,
    private val skillMatching: SkillMatchingPolicy,
    private val events: DomainEventEmitter
) {
    fun createHike(
        newHike: NewHike,
        qualification: OrganizerQualification,
        trailDifficulty: Difficulty,
        actor: String,
        at: LocalDateTime
    ): HikeId {
        if (newHike.title.isBlank() || newHike.description.isBlank()) {
            throw DomainException("informations de randonnée incomplètes")
        }

        if (!newHike.startDateTime.isAfter(at)) {
            throw DomainException("date de randonnée invalide")
        }

        if (!newHike.registrationDeadline.isAfter(at)) {
            throw DomainException("date limite déjà dépassée")
        }

        val schedule = HikeSchedule(
            newHike.startDateTime,
            newHike.expectedDuration,
            newHike.registrationDeadline
        )
        val capacity = Capacity(newHike.capacity)
        planningPolicy.validate(qualification, newHike.startDateTime.toLocalDate(), trailDifficulty)

        val hike = hikeFactory.createHike(
            newHike.organizerId, newHike.title, newHike.description, newHike.trailId, schedule, capacity
        )
        hike.open(actor, at)
        store(hike)
        return hike.id
    }

    fun signUp(
        hikeId: HikeId,
        hikerId: HikerId,
        skillRank: Int,
        trailDifficulty: Difficulty,
        actor: String,
        at: LocalDateTime
    ): SignUpResult {
        val hike = requireHike(hikeId)
        hike.requireAcceptingSignUps(at)
        if (!skillMatching.matches(skillRank, trailDifficulty)) {
            throw DomainException("niveau d'habileté insuffisant")
        }
        hike.requireNoActiveSignUp(hikerId)

        val signUp = signUpFactory.createSignUp(hikerId, hike.nextPlacement(), at)
        hike.addSignUp(signUp, actor, at)
        store(hike)
        return SignUpResult(signUp.id, signUp.status, signUp.waitlistPosition)
    }

    /** Retourne le randonneur promu de la liste d'attente, s'il y en a un. */
    fun cancelSignUp(signUpId: SignUpId, hikerId: HikerId, actor: String, at: LocalDateTime): HikerId? {
        val hike = hikes.findBySignUpId(signUpId) ?: throw DomainException("inscription introuvable")
        val promoted = hike.cancelSignUp(signUpId, hikerId, actor, at)
        store(hike)
        return promoted
    }

    fun cancelHike(
        hikeId: HikeId,
        requesterId: HikerId?,
        requesterIsAdministrator: Boolean,
        reason: String,
        actor: String,
        at: LocalDateTime
    ): List<HikerId> {
        val hike = requireHike(hikeId)
        val authorized = requesterIsAdministrator || hike.organizerId == requesterId
        val affected = hike.cancel(authorized, reason, actor, at)
        store(hike)
        return affected
    }

    fun closeHike(hikeId: HikeId, actor: String, at: LocalDateTime): List<HikerId> {
        val hike = requireHike(hikeId)
        val removed = hike.close(actor, at)
        store(hike)
        return removed
    }

    /** À invoquer par le planificateur système lorsque la randonnée est terminée. */
    fun completeHike(hikeId: HikeId, at: LocalDateTime) {
        val hike = requireHike(hikeId)
        hike.complete("système", at)
        store(hike)
    }

    fun prepareReminder(hikeId: HikeId, at: LocalDateTime): HikeDetails {
        val hike = requireHike(hikeId)
        hike.requireReminderDue(at)
        return hike.toDetails()
    }

    fun markReminderSent(hikeId: HikeId, at: LocalDateTime) {
        val hike = requireHike(hikeId)
        hike.markReminderSent(at)
        store(hike)
    }

    fun getHike(hikeId: HikeId): HikeDetails = requireHike(hikeId).toDetails()

    fun getHikeOfSignUp(signUpId: SignUpId): HikeDetails =
        (hikes.findBySignUpId(signUpId) ?: throw DomainException("inscription introuvable")).toDetails()

    fun findOpenHikes(): List<HikeDetails> =
        hikes.findByStatus(HikeStatus.OPEN)
            .sortedBy { it.schedule.startDateTime }
            .map { it.toDetails() }

    fun matchesSkill(skillRank: Int, difficulty: Difficulty): Boolean =
        skillMatching.matches(skillRank, difficulty)

    private fun requireHike(id: HikeId): Hike =
        hikes.findById(id) ?: throw DomainException("randonnée introuvable")

    private fun store(hike: Hike) {
        hikes.save(hike)
        hike.pullDomainEvents().forEach(events::emit)
    }

    private fun Hike.toDetails() = HikeDetails(
        id, title, trailId, schedule.startDateTime, status, remainingSpots, confirmedHikerIds, organizerId
    )
}
