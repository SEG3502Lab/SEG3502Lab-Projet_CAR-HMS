package CAR_HMS.infrastructure.web

import org.springframework.context.annotation.Profile
import org.springframework.security.web.csrf.CsrfToken
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@Profile("!contracts")
class CsrfController {

    @GetMapping("/api/auth/csrf")
    fun csrf(token: CsrfToken): CsrfTokenResponse =
        CsrfTokenResponse(
            token.headerName,
            token.parameterName,
            token.token
        )
}

data class CsrfTokenResponse(
    val headerName: String,
    val parameterName: String,
    val token: String
)