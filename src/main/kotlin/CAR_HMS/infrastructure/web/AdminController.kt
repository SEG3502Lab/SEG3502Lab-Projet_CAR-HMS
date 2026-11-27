package CAR_HMS.infrastructure.web

import CAR_HMS.application.dtos.queries.RecordOrganizerCertificationQuery
import CAR_HMS.application.dtos.queries.UpdateMembershipStatusQuery
import CAR_HMS.application.usecases.UpdateHiker
import org.springframework.context.annotation.Profile
import org.springframework.http.HttpStatus
import org.springframework.security.core.Authentication
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/admin/hikers")
@Profile("!contracts")
class AdminController(
    private val updateHiker: UpdateHiker
) {

    @PutMapping("/membership")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Transactional
    fun updateMembership(
        @RequestBody query: UpdateMembershipStatusQuery,
        authentication: Authentication
    ) {
        updateHiker.updateMembershipStatus(
            query.copy(
                adminEmail = authentication.name
            )
        )
    }

    @PutMapping("/certification")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Transactional
    fun recordCertification(
        @RequestBody query: RecordOrganizerCertificationQuery,
        authentication: Authentication
    ) {
        updateHiker.recordOrganizerCertification(
            query.copy(
                adminEmail = authentication.name
            )
        )
    }
}