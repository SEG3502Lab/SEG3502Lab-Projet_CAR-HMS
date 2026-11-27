package CAR_HMS.infrastructure.persistence.mappers

import CAR_HMS.domain.members.*
import CAR_HMS.infrastructure.persistence.entities.HikerEntity

object HikerMapper {

    fun toDomain(entity: HikerEntity): Hiker {

        val certificateNumber = entity.certificateNumber

        val certification =
            if (certificateNumber != null) {
                OrganizerCertification(
                    certificateNumber,
                    CertificationPeriod(
                        requireNotNull(entity.certificateIssueDate),
                        requireNotNull(entity.certificateExpiryDate)
                    ),
                    requireNotNull(entity.organizerLevel)
                )
            } else {
                null
            }

        return Hiker(
            id = HikerId(entity.id),
            email = entity.email,
            name = PersonName(
                entity.firstName,
                entity.lastName
            ),
            phoneNumber = entity.phoneNumber,
            address = Address(
                entity.street,
                entity.city,
                entity.province,
                entity.postalCode
            ),
            dateOfBirth = entity.dateOfBirth,
            emergencyContact = EmergencyContact(
                entity.emergencyContactName,
                entity.emergencyContactPhone
            ),
            skillLevel = entity.skillLevel,
            waiverAccepted = entity.waiverAccepted,
            guardianName = entity.guardianName,
            membershipStatus = entity.membershipStatus,
            certification = certification
        )
    }

    fun toEntity(hiker: Hiker): HikerEntity =
        HikerEntity(
            id = hiker.id.value,
            email = hiker.email,
            firstName = hiker.name.firstName,
            lastName = hiker.name.lastName,
            phoneNumber = hiker.phoneNumber,
            street = hiker.address.street,
            city = hiker.address.city,
            province = hiker.address.province,
            postalCode = hiker.address.postalCode,
            dateOfBirth = hiker.dateOfBirth,
            emergencyContactName =
                hiker.emergencyContact.name,
            emergencyContactPhone =
                hiker.emergencyContact.phoneNumber,
            skillLevel = hiker.skillLevel,
            waiverAccepted = hiker.waiverAccepted,
            guardianName = hiker.guardianName,
            membershipStatus = hiker.membershipStatus,
            certificateNumber =
                hiker.certification?.certificateNumber,
            certificateIssueDate =
                hiker.certification?.period?.issueDate,
            certificateExpiryDate =
                hiker.certification?.period?.expiryDate,
            organizerLevel =
                hiker.certification?.organizerLevel
        )
}