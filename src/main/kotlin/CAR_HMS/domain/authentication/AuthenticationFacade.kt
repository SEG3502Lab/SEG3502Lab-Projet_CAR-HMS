package CAR_HMS.domain.authentication

import CAR_HMS.domain.common.DomainEventEmitter
import org.springframework.stereotype.Service
import java.time.LocalDateTime

@Service
class AuthenticationFacade(
    private val accounts: UserAccountRepository,
    private val accountFactory: UserAccountFactory,
    private val authorization: AuthorizationService,
    private val events: DomainEventEmitter
) {
    fun createHikerAccount(email: String, passwordHash: String, at: LocalDateTime) {
        val account = accountFactory.createAccount(email, passwordHash, Role.HIKER)
        account.markCreated(at)
        accounts.save(account)
        account.pullDomainEvents().forEach(events::emit)
    }

    fun isAdministrator(email: String): Boolean =
        authorization.isAdministrator(accounts.findByEmail(email))

    fun requireAdministrator(email: String) =
        authorization.requireAdministrator(accounts.findByEmail(email))
}
