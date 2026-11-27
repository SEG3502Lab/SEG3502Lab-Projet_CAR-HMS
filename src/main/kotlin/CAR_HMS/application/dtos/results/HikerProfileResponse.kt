package CAR_HMS.application.dtos.results

import java.time.LocalDate

data class HikerProfileResponse(

    val id: String,
    val email: String,

    val firstName: String,
    val lastName: String,

    val phoneNumber: String,

    val streetAddress: String,
    val city: String,
    val province: String,
    val postalCode: String,

    val dateOfBirth: LocalDate,

    val emergencyContactName: String,
    val emergencyContactPhone: String,

    val skillLevel: String,
    val membershipStatus: String,

    val organizer: Boolean,

    val certificateNumber: String?,
    val certificateIssueDate: LocalDate?,
    val certificateExpiryDate: LocalDate?,
    val organizerLevel: String?
)