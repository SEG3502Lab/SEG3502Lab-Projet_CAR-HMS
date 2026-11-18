package CAR_HMS.domain.members

import CAR_HMS.domain.common.AggregateRoot
import java.time.LocalDate
import java.time.LocalDateTime

/** Racine de l'agrégat des randonneurs. */
class Hiker(
    val id: HikerId,
    val email: String,
    name: PersonName,
    phoneNumber: String,
    address: Address,
    val dateOfBirth: LocalDate,
    emergencyContact: EmergencyContact,
    skillLevel: SkillLevel,
    val waiverAccepted: Boolean,
    val guardianName: String?,
    membershipStatus: MembershipStatus = MembershipStatus.ACTIVE,
    certification: OrganizerCertification? = null
) : AggregateRoot() {

    var name = name; private set
    var phoneNumber = phoneNumber; private set
    var address = address; private set
    var emergencyContact = emergencyContact; private set
    var skillLevel = skillLevel; private set
    var membershipStatus = membershipStatus; private set
    var certification = certification; private set

    val isOrganizer: Boolean get() = certification != null
    val hasActiveMembership: Boolean get() = membershipStatus == MembershipStatus.ACTIVE

    fun markRegistered(at: LocalDateTime) {
        record(HikerRegistered(id, email, at))
    }

    fun updateInformation(information: HikerInformation, at: LocalDateTime) {
        name = information.name
        phoneNumber = information.phoneNumber
        address = information.address
        emergencyContact = information.emergencyContact
        skillLevel = information.skillLevel
        record(HikerInformationUpdated(id, at))
    }

    fun changeMembershipStatus(newStatus: MembershipStatus, at: LocalDateTime) {
        val previous = membershipStatus
        membershipStatus = newStatus
        record(MembershipStatusChanged(id, previous, newStatus, at))
    }

    fun recordCertification(newCertification: OrganizerCertification, at: LocalDateTime) {
        certification = newCertification
        record(OrganizerCertificationRecorded(id, newCertification.certificateNumber, at))
    }
}
