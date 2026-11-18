package CAR_HMS.domain.members

import CAR_HMS.domain.common.DomainException
import org.springframework.stereotype.Service
import java.time.LocalDate
import java.time.Period

/** Service de domaine : conditions d'admissibilité à l'inscription d'un randonneur. */
@Service
class HikerRegistrationPolicy {
    fun validate(registration: HikerRegistration, today: LocalDate) {
        if (!registration.waiverAccepted) throw DomainException("décharge non acceptée")
        val age = Period.between(registration.dateOfBirth, today).years
        if (age < LEGAL_AGE && registration.guardianName.isNullOrBlank()) {
            throw DomainException("tuteur légal obligatoire")
        }
    }

    private companion object {
        const val LEGAL_AGE = 18
    }
}
