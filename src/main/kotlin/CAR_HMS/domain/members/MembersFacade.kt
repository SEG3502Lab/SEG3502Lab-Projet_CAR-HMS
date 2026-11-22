package CAR_HMS.domain.members

import CAR_HMS.domain.common.AggregateRoot
import CAR_HMS.domain.common.Difficulty
import CAR_HMS.domain.common.DomainEventEmitter
import CAR_HMS.domain.common.DomainException
import org.springframework.stereotype.Service
import java.time.LocalDateTime

/** Vue en lecture d'un randonneur, exposée à la couche application. */
data class HikerProfile(
    val id: HikerId,
    val email: String,
    val skillLevel: SkillLevel,
    val certification: OrganizerCertification?
)

@Service
class MembersFacade(
    private val hikers: HikerRepository,
    private val invitationCodes: InvitationCodeRepository,
    private val hikerFactory: HikerFactory,
    private val registrationPolicy: HikerRegistrationPolicy,
    private val events: DomainEventEmitter
) {
    fun registerHiker(invitationCode: String, registration: HikerRegistration, at: LocalDateTime): HikerId {
        val code = invitationCodes.findByCode(invitationCode)
            ?: throw DomainException("code d'invitation invalide")
        code.requireUnused()
        if (hikers.findByEmail(registration.email) != null) throw DomainException("courriel déjà utilisé")
        registrationPolicy.validate(registration, at.toLocalDate())

        val hiker = hikerFactory.createHiker(registration)
        hiker.markRegistered(at)
        code.redeem(at)
        hikers.save(hiker)
        invitationCodes.save(code)
        publish(hiker, code)
        return hiker.id
    }

    fun updateHikerInformation(email: String, information: HikerInformation, at: LocalDateTime) {
        val hiker = requireHiker(email)
        hiker.updateInformation(information, at)
        hikers.save(hiker)
        publish(hiker)
    }

    fun changeMembershipStatus(email: String, statusCode: String, at: LocalDateTime) {
        val hiker = requireHiker(email)
        hiker.changeMembershipStatus(MembershipStatus.fromCode(statusCode), at)
        hikers.save(hiker)
        publish(hiker)
    }

    fun recordCertification(
        email: String,
        certificateNumber: String,
        period: CertificationPeriod,
        organizerLevel: Difficulty,
        at: LocalDateTime
    ) {
        val hiker = requireHiker(email)
        if (hikers.existsByCertificateNumber(certificateNumber)) {
            throw DomainException("numéro de certificat déjà utilisé")
        }
        hiker.recordCertification(OrganizerCertification(certificateNumber, period, organizerLevel), at)
        hikers.save(hiker)
        publish(hiker)
    }

    fun getProfile(email: String): HikerProfile = requireHiker(email).toProfile()

    fun requireActiveMember(email: String): HikerProfile {
        val hiker = requireHiker(email)
        if (!hiker.hasActiveMembership) throw DomainException("adhésion non active")
        return hiker.toProfile()
    }

    fun requireCertifiedOrganizer(email: String): HikerProfile {
        val profile = hikers.findByEmail(email)?.toProfile()
        if (profile?.certification == null) throw DomainException("organisateur non certifié")
        return profile
    }

    fun findHikerId(email: String): HikerId? = hikers.findByEmail(email)?.id

    fun emailOf(id: HikerId): String =
        hikers.findById(id)?.email ?: throw DomainException("randonneur introuvable")

    /** Nom public de l'organisateur pour l'affichage des randonnées. */
    fun organizerNameOf(id: HikerId): String =
        hikers.findById(id)?.name?.fullName ?: throw DomainException("organisateur introuvable")

    private fun requireHiker(email: String): Hiker =
        hikers.findByEmail(email) ?: throw DomainException("randonneur introuvable")

    private fun Hiker.toProfile() = HikerProfile(id, email, skillLevel, certification)

    private fun publish(vararg aggregates: AggregateRoot) =
        aggregates.forEach { it.pullDomainEvents().forEach(events::emit) }
}
