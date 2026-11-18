package CAR_HMS.contracts.support

import CAR_HMS.contracts.stubs.Resettable
import CAR_HMS.contracts.stubs.StubHikeRepository
import CAR_HMS.contracts.stubs.StubHikerRepository
import CAR_HMS.contracts.stubs.StubTrailRepository
import CAR_HMS.application.dtos.results.HikeSummary
import CAR_HMS.domain.common.Difficulty
import CAR_HMS.domain.common.DomainException
import CAR_HMS.domain.hikes.Capacity
import CAR_HMS.domain.hikes.Hike
import CAR_HMS.domain.hikes.HikeId
import CAR_HMS.domain.hikes.HikeSchedule
import CAR_HMS.domain.hikes.HikeStatus
import CAR_HMS.domain.hikes.SignUp
import CAR_HMS.domain.hikes.SignUpId
import CAR_HMS.domain.hikes.SignUpStatus
import CAR_HMS.domain.members.Address
import CAR_HMS.domain.members.EmergencyContact
import CAR_HMS.domain.members.Hiker
import CAR_HMS.domain.members.HikerId
import CAR_HMS.domain.members.MembershipStatus
import CAR_HMS.domain.members.PersonName
import CAR_HMS.domain.members.SkillLevel
import CAR_HMS.domain.trails.OpeningSeason
import CAR_HMS.domain.trails.Trail
import CAR_HMS.domain.trails.TrailId
import org.springframework.stereotype.Component
import java.time.Duration
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.MonthDay

/** Inscription décrite par un scénario, avant la construction de l'agrégat. */
data class SignUpFixture(
    val id: String,
    val email: String,
    val status: SignUpStatus,
    val signedUpAt: LocalDateTime? = null,
    val position: Int? = null
)

/** Randonnée décrite par les étapes Étant donné ; l'agrégat est construit juste avant l'action. */
class HikeFixture(val id: String) {
    var title = "Randonnée $id"
    var trailName = "Sentier de test"
    var difficulty = Difficulty.EASY
    var trailhead = "stationnement principal"
    var start: LocalDateTime = LocalDateTime.parse("2027-06-12T07:00")
    var deadline: LocalDateTime? = null
    var capacity: Int? = null
    var remaining: Int? = null
    var status = HikeStatus.OPEN
    var organizerEmail = "organisateur@courriel.ca"
    var extraWaitlisted = 0
    var reminderSent = false
    val signUps = mutableListOf<SignUpFixture>()
}

/** État partagé par les définitions d'étapes pendant un scénario. */
@Component
class ContractWorld(
    private val resettables: List<Resettable>,
    val hikes: StubHikeRepository,
    val hikers: StubHikerRepository,
    val trails: StubTrailRepository
) {
    val fixtures = linkedMapOf<String, HikeFixture>()
    var acted = false
    var refusal: String? = null
    var refusalAsserted = false
    var returnedId: String? = null
    var results: List<HikeSummary> = emptyList()
    var subjectEmail: String? = null

    fun reset() {
        resettables.forEach { it.reset() }
        fixtures.clear()
        acted = false
        refusal = null
        refusalAsserted = false
        returnedId = null
        results = emptyList()
        subjectEmail = null
    }

    fun fixture(id: String): HikeFixture = fixtures.getOrPut(id) { HikeFixture(id) }

    /** Exécute la commande : seul un refus métier est capté, toute autre erreur fait échouer le scénario. */
    fun act(command: () -> Unit) {
        fixtures.values.forEach(::buildHike)
        acted = true
        try {
            command()
        } catch (exception: DomainException) {
            refusal = exception.reason
        }
    }

    fun hike(id: String): Hike = hikes.findById(HikeId(id)) ?: error("Randonnée $id introuvable")

    fun signUp(id: String): SignUp = hikes.findBySignUpId(SignUpId(id))?.signUps?.first { it.id.value == id }
        ?: error("Inscription $id introuvable")

    fun hiker(email: String): Hiker = hikers.findByEmail(email) ?: error("Randonneur $email introuvable")

    fun ensureHiker(
        email: String,
        level: SkillLevel = SkillLevel.INTERMEDIATE,
        membership: MembershipStatus = MembershipStatus.ACTIVE
    ): Hiker = hikers.findByEmail(email) ?: Hiker(
        HikerId("R-$email"), email, PersonName("Prénom", "Nom"), "613-555-0100",
        Address("1, rue Principale", "Ottawa", "ON", "K1A 0A1"), LocalDate.parse("1990-01-01"),
        EmergencyContact("Contact", "613-555-0101"), level, true, null, membership
    ).also(hikers::save)

    fun ensureTrail(
        name: String,
        difficulty: Difficulty = Difficulty.EASY,
        trailhead: String = "stationnement principal"
    ): Trail = trails.findByName(name) ?: Trail(
        TrailId("CCN-$name"), name, difficulty, true,
        OpeningSeason(MonthDay.of(1, 1), MonthDay.of(12, 31)), trailhead
    ).also(trails::save)

    private fun buildHike(fixture: HikeFixture) {
        if (hikes.findById(HikeId(fixture.id)) != null) return
        val organizer = ensureHiker(fixture.organizerEmail)
        val trail = ensureTrail(fixture.trailName, fixture.difficulty, fixture.trailhead)
        val declaredConfirmed = fixture.signUps.count { it.status == SignUpStatus.CONFIRMED }
        val capacity = fixture.capacity ?: if (fixture.status == HikeStatus.FULL) maxOf(declaredConfirmed, 1) else DEFAULT_CAPACITY
        val targetConfirmed = when {
            fixture.status == HikeStatus.FULL -> capacity
            fixture.remaining != null -> capacity - fixture.remaining!!
            else -> declaredConfirmed
        }

        val signUps = fixture.signUps.toSignUps(fixture.start).toMutableList()
        repeat(targetConfirmed - declaredConfirmed) { index ->
            signUps += filler("S-PLEIN-$index", "plein$index@courriel.ca", SignUpStatus.CONFIRMED, null, fixture.start)
        }
        val waitlisted = signUps.count { it.isWaitlisted }
        repeat(fixture.extraWaitlisted) { index ->
            signUps += filler(
                "S-ATTENTE-$index", "attente$index@courriel.ca", SignUpStatus.WAITLISTED, waitlisted + index + 1, fixture.start
            )
        }
        hikes.save(
            Hike(
                HikeId(fixture.id), organizer.id, fixture.title, "Description", trail.id,
                HikeSchedule(fixture.start, Duration.ofHours(4), fixture.deadline ?: fixture.start.minusDays(2)),
                Capacity(capacity), fixture.status, signUps, emptyList(), fixture.reminderSent
            )
        )
    }

    private fun List<SignUpFixture>.toSignUps(start: LocalDateTime): List<SignUp> {
        var nextPosition = 1
        return map { fixture ->
            val position = when (fixture.status) {
                SignUpStatus.WAITLISTED -> fixture.position ?: nextPosition
                else -> null
            }
            if (position != null) nextPosition = position + 1
            SignUp(
                SignUpId(fixture.id), ensureHiker(fixture.email).id,
                fixture.signedUpAt ?: start.minusDays(30), fixture.status, position
            )
        }
    }

    private fun filler(id: String, email: String, status: SignUpStatus, position: Int?, start: LocalDateTime) =
        SignUp(SignUpId(id), ensureHiker(email).id, start.minusDays(30), status, position)

    private companion object {
        const val DEFAULT_CAPACITY = 12
    }
}
