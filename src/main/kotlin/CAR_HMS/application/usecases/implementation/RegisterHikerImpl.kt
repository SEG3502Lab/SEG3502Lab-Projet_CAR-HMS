package CAR_HMS.application.usecases.implementation

import CAR_HMS.application.dtos.queries.RegisterHikerQuery
import CAR_HMS.application.services.PasswordHasher
import CAR_HMS.application.services.TimeService
import CAR_HMS.application.usecases.RegisterHiker
import CAR_HMS.domain.authentication.AuthenticationFacade
import CAR_HMS.domain.members.Address
import CAR_HMS.domain.members.EmergencyContact
import CAR_HMS.domain.members.HikerRegistration
import CAR_HMS.domain.members.MembersFacade
import CAR_HMS.domain.members.PersonName
import CAR_HMS.domain.members.SkillLevel
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class RegisterHikerImpl(
    private val members: MembersFacade,
    private val authentication: AuthenticationFacade,
    private val passwordHasher: PasswordHasher,
    private val time: TimeService
) : RegisterHiker {

    @Transactional
    override fun registerHiker(
        query: RegisterHikerQuery
    ): String {

        val now = time.now()

        val registration = HikerRegistration(

            query.email,

            PersonName(
                query.firstName,
                query.lastName
            ),

            query.phoneNumber,

            Address(
                query.streetAddress,
                query.city,
                query.province,
                query.postalCode
            ),

            query.dateOfBirth,

            EmergencyContact(
                query.emergencyContactName,
                query.emergencyContactPhone
            ),

            SkillLevel.fromLabel(query.skillLevel),

            query.waiverAccepted,

            query.guardianName
        )

        val hikerId = members.registerHiker(
            query.invitationCode,
            registration,
            now
        )

        authentication.createHikerAccount(
            query.email,
            passwordHasher.hash(query.password),
            now
        )

        return hikerId.value
    }
}