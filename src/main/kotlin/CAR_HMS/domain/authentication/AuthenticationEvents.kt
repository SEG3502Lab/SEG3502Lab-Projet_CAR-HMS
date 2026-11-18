package CAR_HMS.domain.authentication

import CAR_HMS.domain.common.DomainEvent
import java.time.LocalDateTime

data class UserAccountCreated(
    val accountId: UserAccountId,
    val email: String,
    val role: Role,
    override val occurredAt: LocalDateTime
) : DomainEvent
