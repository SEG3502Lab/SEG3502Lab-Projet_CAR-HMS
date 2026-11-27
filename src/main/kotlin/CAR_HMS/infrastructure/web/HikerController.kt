package CAR_HMS.infrastructure.web

import CAR_HMS.application.dtos.queries.RegisterHikerQuery
import CAR_HMS.application.dtos.queries.UpdateHikerInformationQuery
import CAR_HMS.application.usecases.RegisterHiker
import CAR_HMS.application.usecases.UpdateHiker
import org.springframework.context.annotation.Profile
import org.springframework.http.HttpStatus
import org.springframework.security.core.Authentication
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/hikers")
@Profile("!contracts")
class HikerController(
    private val registerHiker: RegisterHiker,
    private val updateHiker: UpdateHiker
) {

    @PostMapping("/register")
    @ResponseStatus(HttpStatus.CREATED)
    @Transactional
    fun register(
        @RequestBody query: RegisterHikerQuery
    ): Map<String, String> {

        val hikerId =
            registerHiker.registerHiker(query)

        return mapOf("hikerId" to hikerId)
    }

    @PutMapping("/me")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Transactional
    fun updateInformation(
        @RequestBody query: UpdateHikerInformationQuery,
        authentication: Authentication
    ) {
        updateHiker.updateHikerInformation(
            query.copy(
                hikerEmail = authentication.name
            )
        )
    }
}