package CAR_HMS.infrastructure.web

import CAR_HMS.application.dtos.results.HikerProfileResponse
import CAR_HMS.application.dtos.results.TrailResponse
import CAR_HMS.application.usecases.implementation.ConsultationService
import org.springframework.context.annotation.Profile
import org.springframework.security.core.Authentication
import org.springframework.web.bind.annotation.*

@RestController
@Profile("!contracts")
class ConsultationController(
    private val consultation: ConsultationService
) {

    @GetMapping("/api/hikers/me")
    fun myProfile(): HikerProfileResponse =
        consultation.getMyProfile()

    @GetMapping("/api/admin/hikers/profile")
    fun hikerProfile(
        @RequestParam email: String,
        authentication: Authentication
    ): HikerProfileResponse =

        consultation.getHikerProfile(
            authentication.name,
            email
        )

    @GetMapping("/api/trails")
    fun listTrails(): List<TrailResponse> =
        consultation.listTrails()
}