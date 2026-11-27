package CAR_HMS.infrastructure.security

import CAR_HMS.application.services.PasswordHasher
import org.springframework.context.annotation.Profile
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Component

@Component
@Profile("!contracts")
class BCryptPasswordHasher(
    private val encoder: PasswordEncoder
) : PasswordHasher {

    override fun hash(rawPassword: String): String =
        requireNotNull(encoder.encode(rawPassword))
}