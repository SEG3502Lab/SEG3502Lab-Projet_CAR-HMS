package CAR_HMS.domain.hikes

import CAR_HMS.domain.common.AggregateRoot
import CAR_HMS.domain.common.DomainException
import CAR_HMS.domain.members.HikerId
import CAR_HMS.domain.trails.TrailId
import java.time.Duration
import java.time.LocalDateTime

/** Racine de l'agrégat : garantit la capacité, la liste d'attente et le cycle de vie. */
class Hike(
    val id: HikeId,
    val organizerId: HikerId,
    val title: String,
    val description: String,
    val trailId: TrailId,
    val schedule: HikeSchedule,
    val capacity: Capacity,
    status: HikeStatus,
    signUps: List<SignUp> = emptyList(),
    transitions: List<StatusTransition> = emptyList(),
    reminderSent: Boolean = false
) : AggregateRoot() {

    private val signUpList = signUps.toMutableList()
    private val transitionList = transitions.toMutableList()

    var status = status; private set
    var reminderSent = reminderSent; private set

    val signUps: List<SignUp> get() = signUpList.toList()
    val transitions: List<StatusTransition> get() = transitionList.toList()
    val remainingSpots: Int get() = (capacity.value - signUpList.count { it.isConfirmed }).coerceAtLeast(0)
    val confirmedHikerIds: List<HikerId> get() = signUpList.filter { it.isConfirmed }.map { it.hikerId }

    private val waitlist: List<SignUp>
        get() = signUpList.filter { it.isWaitlisted }.sortedBy { it.waitlistPosition }

    /** Publie une randonnée nouvellement créée : première transition de son cycle de vie. */
    fun open(actor: String, at: LocalDateTime) {
        status = HikeStatus.OPEN
        transitionList += StatusTransition(null, HikeStatus.OPEN, at, actor)
        record(HikeCreated(id, organizerId, at))
        record(HikeStatusChanged(id, null, HikeStatus.OPEN, actor, at))
    }

    fun requireAcceptingSignUps(at: LocalDateTime) {
        if (status != HikeStatus.OPEN && status != HikeStatus.FULL) {
            throw DomainException("randonnée non disponible")
        }

        if (!at.isBefore(schedule.registrationDeadline)) {
            throw DomainException("date limite dépassée")
        }
    }

    fun requireNoActiveSignUp(hikerId: HikerId) {
        if (signUpList.any { it.hikerId == hikerId && it.isActive }) {
            throw DomainException("inscription déjà active")
        }
    }

    fun nextPlacement(): SignUpPlacement =
        if (remainingSpots > 0) SignUpPlacement(SignUpStatus.CONFIRMED, null)
        else SignUpPlacement(SignUpStatus.WAITLISTED, waitlist.size + 1)

    fun addSignUp(signUp: SignUp, actor: String, at: LocalDateTime) {
        signUpList += signUp
        if (signUp.isConfirmed) {
            record(SignUpConfirmed(id, signUp.id, signUp.hikerId, at))
            if (remainingSpots == 0) transitionTo(HikeStatus.FULL, actor, at)
        } else {
            record(SignUpWaitlisted(id, signUp.id, signUp.hikerId, signUp.waitlistPosition!!, at))
        }
    }

    /** Annule une inscription et retourne le randonneur promu de la liste d'attente, s'il y en a un. */
    fun cancelSignUp(signUpId: SignUpId, hikerId: HikerId, actor: String, at: LocalDateTime): HikerId? {
        val signUp = signUpList.firstOrNull { it.id == signUpId }
            ?: throw DomainException("inscription introuvable")

        if (signUp.hikerId != hikerId) {
            throw DomainException("accès refusé")
        }

        if (!signUp.isActive) {
            throw DomainException("inscription non active")
        }

        if (status != HikeStatus.OPEN && status != HikeStatus.FULL) {
            throw DomainException("randonnée non disponible")
        }

        if (!at.isBefore(schedule.registrationDeadline)) {
            throw DomainException("date limite dépassée")
        }

        val freedConfirmedSpot = signUp.isConfirmed
        signUp.cancel()
        record(SignUpCancelled(id, signUp.id, signUp.hikerId, at))

        val promoted = if (freedConfirmedSpot) promoteFirstWaitlisted(at) else null
        renumberWaitlist(at)
        if (freedConfirmedSpot && promoted == null && status == HikeStatus.FULL) {
            transitionTo(HikeStatus.OPEN, actor, at)
        }
        return promoted?.hikerId
    }

    /** Annule la randonnée et retourne les randonneurs dont l'inscription était active. */
    fun cancel(authorized: Boolean, reason: String, actor: String, at: LocalDateTime): List<HikerId> {
        if (!authorized) throw DomainException("accès refusé")
        if (status == HikeStatus.CANCELLED) throw DomainException("randonnée déjà annulée")
        if (status == HikeStatus.COMPLETED) throw DomainException("randonnée terminée")
        if (!at.isBefore(schedule.startDateTime)) throw DomainException("randonnée déjà commencée")
        if (reason.isBlank()) throw DomainException("motif obligatoire")

        val affected = cancelSignUps(signUpList.filter { it.isActive }, at)
        transitionTo(HikeStatus.CANCELLED, actor, at)
        record(HikeCancelled(id, reason, at))
        return affected
    }

    /** Ferme les inscriptions et retourne les randonneurs retirés de la liste d'attente. */
    fun close(actor: String, at: LocalDateTime): List<HikerId> {
        if (status != HikeStatus.OPEN && status != HikeStatus.FULL) {
            throw DomainException("randonnée ni ouverte ni complète")
        }
        if (at.isBefore(schedule.registrationDeadline)) throw DomainException("date limite non atteinte")

        val removed = cancelSignUps(waitlist, at)
        transitionTo(HikeStatus.CLOSED, actor, at)
        record(HikeClosed(id, at))
        return removed
    }

    /** Transition système quand la durée prévue est écoulée et que la sortie n'est pas annulée. */
    fun complete(actor: String, at: LocalDateTime) {
        if (status == HikeStatus.CANCELLED) throw DomainException("randonnée annulée")
        if (status == HikeStatus.COMPLETED) throw DomainException("randonnée déjà terminée")
        if (at.isBefore(schedule.startDateTime.plus(schedule.expectedDuration))) {
            throw DomainException("randonnée non terminée")
        }

        transitionTo(HikeStatus.COMPLETED, actor, at)
    }

    fun requireReminderDue(at: LocalDateTime) {
        if (status == HikeStatus.CANCELLED) throw DomainException("randonnée annulée")
        if (reminderSent) throw DomainException("rappel déjà envoyé")
        val untilStart = Duration.between(at, schedule.startDateTime)
        if (untilStart.isNegative || untilStart.isZero || untilStart > REMINDER_WINDOW) {
            throw DomainException("départ pas dans 24 heures")
        }
    }

    fun markReminderSent(at: LocalDateTime) {
        reminderSent = true
        record(HikeReminderSent(id, at))
    }

    private fun cancelSignUps(targets: List<SignUp>, at: LocalDateTime): List<HikerId> =
        targets.map { signUp ->
            signUp.cancel()
            record(SignUpCancelled(id, signUp.id, signUp.hikerId, at))
            signUp.hikerId
        }

    private fun promoteFirstWaitlisted(at: LocalDateTime): SignUp? =
        waitlist.firstOrNull()?.also { first ->
            first.confirm()
            record(SignUpPromoted(id, first.id, first.hikerId, at))
        }

    private fun renumberWaitlist(at: LocalDateTime) {
        waitlist.forEachIndexed { index, signUp ->
            val position = index + 1
            if (signUp.waitlistPosition != position) {
                signUp.moveToPosition(position)
                record(WaitlistPositionChanged(id, signUp.id, position, at))
            }
        }
    }

    private fun transitionTo(newStatus: HikeStatus, actor: String, at: LocalDateTime) {
        val previous = status
        status = newStatus
        transitionList += StatusTransition(previous, newStatus, at, actor)
        record(HikeStatusChanged(id, previous, newStatus, actor, at))
    }

    private companion object {
        val REMINDER_WINDOW: Duration = Duration.ofHours(24)
    }
}
