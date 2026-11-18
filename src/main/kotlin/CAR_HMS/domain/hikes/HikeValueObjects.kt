package CAR_HMS.domain.hikes

import CAR_HMS.domain.common.DomainException
import java.time.Duration
import java.time.LocalDateTime

data class HikeId(val value: String)

data class SignUpId(val value: String)

enum class HikeStatus(val label: String) {
    OPEN("ouverte"),
    FULL("complète"),
    CLOSED("fermée"),
    CANCELLED("annulée"),
    COMPLETED("terminée")
}

enum class SignUpStatus(val label: String) {
    CONFIRMED("confirmée"),
    WAITLISTED("en liste d'attente"),
    CANCELLED("annulée")
}

/** Horaire d'une randonnée : la date limite précède le départ d'au plus 14 jours. */
data class HikeSchedule(
    val startDateTime: LocalDateTime,
    val expectedDuration: Duration,
    val registrationDeadline: LocalDateTime
) {
    init {
        val tooLate = !registrationDeadline.isBefore(startDateTime)
        val tooEarly = registrationDeadline.isBefore(startDateTime.minusDays(MAX_DEADLINE_LEAD_DAYS))
        if (tooLate || tooEarly) throw DomainException("date limite d'inscription invalide")
    }

    private companion object {
        const val MAX_DEADLINE_LEAD_DAYS = 14L
    }
}

data class Capacity(val value: Int) {
    init {
        if (value <= 0) throw DomainException("capacité invalide")
    }
}

/** Trace d'un changement de statut : horodatage et acteur. */
data class StatusTransition(
    val from: HikeStatus?,
    val to: HikeStatus,
    val at: LocalDateTime,
    val actor: String
)

/** Place attribuée à une nouvelle inscription. */
data class SignUpPlacement(val status: SignUpStatus, val waitlistPosition: Int?)
