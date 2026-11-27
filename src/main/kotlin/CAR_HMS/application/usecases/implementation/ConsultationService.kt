package CAR_HMS.application.usecases.implementation

import CAR_HMS.application.dtos.results.HikerProfileResponse
import CAR_HMS.application.dtos.results.TrailResponse
import CAR_HMS.domain.common.DomainException
import CAR_HMS.domain.members.Hiker
import CAR_HMS.domain.members.HikerRepository
import CAR_HMS.domain.trails.TrailRepository
import org.springframework.stereotype.Service

@Service
class ConsultationService(
    private val access: AccessControl,
    private val hikers: HikerRepository,
    private val trails: TrailRepository
) {

    fun getMyProfile(): HikerProfileResponse {

        val email = access.requireLoggedIn()

        val hiker = hikers.findByEmail(email)
            ?: throw DomainException(
                "randonneur introuvable"
            )

        return toProfileResponse(hiker)
    }

    fun getHikerProfile(
        adminEmail: String,
        hikerEmail: String
    ): HikerProfileResponse {

        access.requireAdministrator(adminEmail)

        val hiker = hikers.findByEmail(hikerEmail)
            ?: throw DomainException(
                "randonneur introuvable"
            )

        return toProfileResponse(hiker)
    }

    fun isCurrentUserOrganizer(): Boolean {

        val email = access.requireLoggedIn()

        return hikers.findByEmail(email)
            ?.isOrganizer == true
    }

    fun listTrails(): List<TrailResponse> {

        access.requireLoggedIn()

        return trails.findAll()
            .filter { it.hikingAllowed }
            .sortedBy { it.name }
            .map { trail ->

                TrailResponse(
                    id = trail.id.value,
                    name = trail.name,
                    difficulty = trail.difficulty.label,
                    trailhead = trail.trailhead,

                    openingSeasonStart =
                        trail.season.opensOn.toString(),

                    openingSeasonEnd =
                        trail.season.closesOn.toString()
                )
            }
    }

    private fun toProfileResponse(
        hiker: Hiker
    ): HikerProfileResponse =

        HikerProfileResponse(

            id = hiker.id.value,
            email = hiker.email,

            firstName = hiker.name.firstName,
            lastName = hiker.name.lastName,

            phoneNumber = hiker.phoneNumber,

            streetAddress = hiker.address.street,
            city = hiker.address.city,
            province = hiker.address.province,
            postalCode = hiker.address.postalCode,

            dateOfBirth = hiker.dateOfBirth,

            emergencyContactName =
                hiker.emergencyContact.name,

            emergencyContactPhone =
                hiker.emergencyContact.phoneNumber,

            skillLevel = hiker.skillLevel.label,

            membershipStatus =
                hiker.membershipStatus.code,

            organizer = hiker.isOrganizer,

            certificateNumber =
                hiker.certification?.certificateNumber,

            certificateIssueDate =
                hiker.certification?.period?.issueDate,

            certificateExpiryDate =
                hiker.certification?.period?.expiryDate,

            organizerLevel =
                hiker.certification?.organizerLevel?.label
        )
}