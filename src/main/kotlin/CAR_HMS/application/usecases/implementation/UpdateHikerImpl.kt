package CAR_HMS.application.usecases.implementation

import CAR_HMS.application.dtos.queries.RecordOrganizerCertificationQuery
import CAR_HMS.application.dtos.queries.UpdateHikerInformationQuery
import CAR_HMS.application.dtos.queries.UpdateMembershipStatusQuery
import CAR_HMS.application.services.TimeService
import CAR_HMS.application.usecases.UpdateHiker
import CAR_HMS.domain.common.Difficulty
import CAR_HMS.domain.members.Address
import CAR_HMS.domain.members.CertificationPeriod
import CAR_HMS.domain.members.EmergencyContact
import CAR_HMS.domain.members.HikerInformation
import CAR_HMS.domain.members.MembersFacade
import CAR_HMS.domain.members.PersonName
import CAR_HMS.domain.members.SkillLevel
import org.springframework.stereotype.Service

@Service
class UpdateHikerImpl(
    private val access: AccessControl,
    private val members: MembersFacade,
    private val time: TimeService
) : UpdateHiker {

    override fun updateHikerInformation(query: UpdateHikerInformationQuery) {
        access.requireSelf(query.hikerEmail)
        val information = HikerInformation(
            PersonName(query.firstName, query.lastName),
            query.phoneNumber,
            Address(query.streetAddress, query.city, query.province, query.postalCode),
            EmergencyContact(query.emergencyContactName, query.emergencyContactPhone),
            SkillLevel.fromLabel(query.skillLevel)
        )
        members.updateHikerInformation(query.hikerEmail, information, time.now())
    }

    override fun updateMembershipStatus(query: UpdateMembershipStatusQuery) {
        access.requireAdministrator(query.adminEmail)
        members.changeMembershipStatus(query.hikerEmail, query.newStatus, time.now())
    }

    override fun recordOrganizerCertification(query: RecordOrganizerCertificationQuery) {
        access.requireAdministrator(query.adminEmail)
        members.recordCertification(
            query.hikerEmail,
            query.certificateNumber,
            CertificationPeriod(query.issueDate, query.expiryDate),
            Difficulty.fromLabel(query.organizerLevel),
            time.now()
        )
    }
}
