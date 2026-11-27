package CAR_HMS.infrastructure.persistence.mappers

import CAR_HMS.domain.authentication.UserAccount
import CAR_HMS.domain.authentication.UserAccountId
import CAR_HMS.infrastructure.persistence.entities.UserAccountEntity

object AuthenticationMapper {

    fun toDomain(entity: UserAccountEntity): UserAccount =
        UserAccount(
            UserAccountId(entity.id),
            entity.email,
            entity.passwordHash,
            entity.role
        )

    fun toEntity(account: UserAccount): UserAccountEntity =
        UserAccountEntity(
            account.id.value,
            account.email,
            account.passwordHash,
            account.role
        )
}