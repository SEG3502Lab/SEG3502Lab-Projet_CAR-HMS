package CAR_HMS.infrastructure.persistence.repositories

import CAR_HMS.domain.authentication.UserAccount
import CAR_HMS.domain.authentication.UserAccountRepository
import CAR_HMS.infrastructure.persistence.mappers.AuthenticationMapper
import org.springframework.context.annotation.Profile
import org.springframework.stereotype.Repository

@Repository
@Profile("!contracts")
class UserAccountRepositoryAdapter(
    private val repository: UserAccountJpaRepository
) : UserAccountRepository {

    override fun findByEmail(email: String): UserAccount? =
        repository.findByEmail(email)
            ?.let(AuthenticationMapper::toDomain)

    override fun save(account: UserAccount) {
        repository.save(
            AuthenticationMapper.toEntity(account)
        )
    }
}