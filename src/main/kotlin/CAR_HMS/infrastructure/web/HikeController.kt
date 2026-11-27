package CAR_HMS.infrastructure.web

import CAR_HMS.application.dtos.queries.BrowseOpenHikesQuery
import CAR_HMS.application.dtos.queries.CreateHikeQuery
import CAR_HMS.application.dtos.queries.ListMatchingHikesQuery
import CAR_HMS.application.dtos.results.HikeSummary
import CAR_HMS.application.usecases.BrowseHikes
import CAR_HMS.application.usecases.CreateHike
import org.springframework.context.annotation.Profile
import org.springframework.http.HttpStatus
import org.springframework.security.core.Authentication
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.bind.annotation.*
import java.time.LocalDate

@RestController
@RequestMapping("/api/hikes")
@Profile("!contracts")
class HikeController(
    private val browseHikes: BrowseHikes,
    private val createHike: CreateHike
) {

    @GetMapping
    fun browse(
        @RequestParam(required = false)
        trailName: String?,

        @RequestParam(required = false)
        date: LocalDate?,

        @RequestParam(required = false)
        difficulty: String?
    ): List<HikeSummary> =
        browseHikes.browseOpenHikes(
            BrowseOpenHikesQuery(
                trailName,
                date,
                difficulty
            )
        )

    @GetMapping("/matching")
    fun matching(
        authentication: Authentication
    ): List<HikeSummary> =
        browseHikes.listMatchingHikes(
            ListMatchingHikesQuery(
                authentication.name
            )
        )

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Transactional
    fun create(
        @RequestBody query: CreateHikeQuery,
        authentication: Authentication
    ): Map<String, String> {

        val hikeId = createHike.createHike(
            query.copy(
                organizerEmail = authentication.name
            )
        )

        return mapOf("hikeId" to hikeId)
    }
}