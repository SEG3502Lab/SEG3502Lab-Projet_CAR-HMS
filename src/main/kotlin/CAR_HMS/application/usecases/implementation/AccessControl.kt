package CAR_HMS.application.usecases.implementation

import CAR_HMS.application.services.CurrentUserService
import CAR_HMS.domain.authentication.AuthenticationFacade
import CAR_HMS.domain.common.DomainException
import org.springframework.stereotype.Component

/** Contrôles d'accès communs aux réalisations de cas d'utilisation. */
@Component
class AccessControl(
    private val currentUser: CurrentUserService,
    private val authentication: AuthenticationFacade
) {
    fun requireLoggedIn(): String =
        currentUser.currentUserEmail() ?: throw DomainException("connexion requise")

    fun requireSelf(email: String) {
        if (requireLoggedIn() != email) throw DomainException("accès refusé")
    }

    fun requireAdministrator(email: String) {
        requireSelf(email)
        authentication.requireAdministrator(email)
    }
}
