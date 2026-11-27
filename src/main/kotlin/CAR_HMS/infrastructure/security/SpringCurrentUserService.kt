package CAR_HMS.infrastructure.security

import CAR_HMS.application.services.CurrentUserService
import org.springframework.context.annotation.Profile
import org.springframework.security.authentication.AnonymousAuthenticationToken
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.stereotype.Component

@Component
@Profile("!contracts")
class SpringCurrentUserService : CurrentUserService {

    override fun currentUserEmail(): String? {
        val authentication =
            SecurityContextHolder.getContext().authentication

        return if (
            authentication != null &&
            authentication.isAuthenticated &&
            authentication !is AnonymousAuthenticationToken
        ) {
            authentication.name
        } else {
            null
        }
    }
}