package CAR_HMS.infrastructure.web

import CAR_HMS.application.usecases.implementation.ConsultationService
import org.springframework.context.annotation.Profile
import org.springframework.security.core.Authentication
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@Profile("!contracts")
class AuthController(
    private val consultation: ConsultationService
) {

    @GetMapping("/api/auth/me")
    fun currentUser(
        authentication: Authentication
    ): AuthenticatedUserResponse =

        AuthenticatedUserResponse(
            email = authentication.name,

            roles = authentication.authorities
                .mapNotNull { it.authority },

            organizer =
                consultation.isCurrentUserOrganizer()
        )
}

data class AuthenticatedUserResponse(
    val email: String,
    val roles: List<String>,
    val organizer: Boolean
)