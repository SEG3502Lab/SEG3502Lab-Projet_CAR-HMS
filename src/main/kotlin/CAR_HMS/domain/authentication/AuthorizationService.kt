package CAR_HMS.domain.authentication

import CAR_HMS.domain.common.DomainException
import org.springframework.stereotype.Service

/** Service de domaine : règles d'autorisation fondées sur le rôle du compte. */
@Service
class AuthorizationService {
    fun isAdministrator(account: UserAccount?): Boolean = account?.isAdministrator == true

    fun requireAdministrator(account: UserAccount?) {
        if (!isAdministrator(account)) throw DomainException("accès refusé")
    }
}
