package CAR_HMS.domain.hikes

import CAR_HMS.domain.members.HikerId
import java.time.LocalDateTime

/** Entité interne de l'agrégat Hike : seule la racine modifie son état. */
class SignUp(
    val id: SignUpId,
    val hikerId: HikerId,
    val signedUpAt: LocalDateTime,
    status: SignUpStatus,
    waitlistPosition: Int? = null
) {
    var status = status; private set
    var waitlistPosition = waitlistPosition; private set

    val isActive: Boolean get() = status != SignUpStatus.CANCELLED
    val isConfirmed: Boolean get() = status == SignUpStatus.CONFIRMED
    val isWaitlisted: Boolean get() = status == SignUpStatus.WAITLISTED

    internal fun cancel() {
        status = SignUpStatus.CANCELLED
        waitlistPosition = null
    }

    internal fun confirm() {
        status = SignUpStatus.CONFIRMED
        waitlistPosition = null
    }

    internal fun moveToPosition(position: Int) {
        waitlistPosition = position
    }
}
