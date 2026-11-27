package CAR_HMS.infrastructure.security

import CAR_HMS.domain.authentication.UserAccountRepository
import org.springframework.context.annotation.Profile
import org.springframework.security.core.userdetails.User
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.stereotype.Service

@Service
@Profile("!contracts")
class DatabaseUserDetailsService(
    private val accounts: UserAccountRepository
) : UserDetailsService {

    override fun loadUserByUsername(
        username: String
    ): UserDetails {

        val account = accounts.findByEmail(username)
            ?: throw UsernameNotFoundException(
                "Utilisateur introuvable"
            )

        return User.withUsername(account.email)
            .password(account.passwordHash)
            .roles(account.role.name)
            .build()
    }
}