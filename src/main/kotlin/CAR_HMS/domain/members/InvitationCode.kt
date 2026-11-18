package CAR_HMS.domain.members

import CAR_HMS.domain.common.AggregateRoot
import CAR_HMS.domain.common.DomainException
import java.time.LocalDateTime

/** Racine de l'agrégat des codes d'invitation, à usage unique. */
class InvitationCode(val code: String, used: Boolean = false) : AggregateRoot() {
    var used = used; private set

    fun requireUnused() {
        if (used) throw DomainException("code d'invitation déjà utilisé")
    }

    fun redeem(at: LocalDateTime) {
        requireUnused()
        used = true
        record(InvitationCodeRedeemed(code, at))
    }
}
