package CAR_HMS.domain.members

import CAR_HMS.domain.common.Difficulty
import CAR_HMS.domain.common.DomainException
import java.time.LocalDate

data class HikerId(val value: String)

data class PersonName(val firstName: String, val lastName: String) {
    val fullName: String get() = "$firstName $lastName"
}

data class Address(
    val street: String,
    val city: String,
    val province: String,
    val postalCode: String
) {
    override fun toString(): String = "$street, $city, $province, $postalCode"
}

data class EmergencyContact(val name: String, val phoneNumber: String)

enum class SkillLevel(val label: String, val rank: Int) {
    BEGINNER("débutant", 1),
    INTERMEDIATE("intermédiaire", 2),
    ADVANCED("avancé", 3),
    EXPERT("expert", 4);

    companion object {
        fun fromLabel(label: String): SkillLevel =
            entries.firstOrNull { it.label == label } ?: throw DomainException("niveau d'habileté invalide")
    }
}

enum class MembershipStatus(val code: String, val label: String) {
    ACTIVE("active", "active"),
    EXPIRED("expired", "expirée"),
    SUSPENDED("suspended", "suspendue");

    companion object {
        fun fromCode(code: String): MembershipStatus =
            entries.firstOrNull { it.code == code } ?: throw DomainException("statut d'adhésion invalide")
    }
}

/** Période de validité d'un certificat ; la date d'expiration suit la date de délivrance. */
data class CertificationPeriod(val issueDate: LocalDate, val expiryDate: LocalDate) {
    init {
        if (!expiryDate.isAfter(issueDate)) throw DomainException("dates de certification invalides")
    }

    fun covers(date: LocalDate): Boolean = !date.isBefore(issueDate) && !date.isAfter(expiryDate)
}

/** Entité de l'agrégat Hiker, identifiée par son numéro de certificat unique. */
class OrganizerCertification(
    val certificateNumber: String,
    val period: CertificationPeriod,
    val organizerLevel: Difficulty
)

/** Données validées nécessaires à la création d'un randonneur. */
data class HikerRegistration(
    val email: String,
    val name: PersonName,
    val phoneNumber: String,
    val address: Address,
    val dateOfBirth: LocalDate,
    val emergencyContact: EmergencyContact,
    val skillLevel: SkillLevel,
    val waiverAccepted: Boolean,
    val guardianName: String?
)

/** Coordonnées modifiables d'un profil ; le courriel en est exclu. */
data class HikerInformation(
    val name: PersonName,
    val phoneNumber: String,
    val address: Address,
    val emergencyContact: EmergencyContact,
    val skillLevel: SkillLevel
)
