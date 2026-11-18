package CAR_HMS.domain.authentication

import CAR_HMS.domain.common.AggregateRoot
import java.time.LocalDateTime

data class UserAccountId(val value: String)

enum class Role { HIKER, ADMINISTRATOR }

/** Racine de l'agrégat des comptes : identifiants de connexion et rôle. */
class UserAccount(
    val id: UserAccountId,
    val email: String,
    val passwordHash: String,
    val role: Role
) : AggregateRoot() {

    val isAdministrator: Boolean get() = role == Role.ADMINISTRATOR

    fun markCreated(at: LocalDateTime) {
        record(UserAccountCreated(id, email, role, at))
    }
}
