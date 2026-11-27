package CAR_HMS.infrastructure.configuration

import CAR_HMS.domain.common.Difficulty
import CAR_HMS.domain.trails.OpeningSeason
import CAR_HMS.domain.trails.Trail
import CAR_HMS.domain.trails.TrailId
import CAR_HMS.infrastructure.persistence.repositories.TrailRepositoryAdapter
import org.springframework.boot.ApplicationArguments
import org.springframework.boot.ApplicationRunner
import org.springframework.context.annotation.Profile
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional
import java.time.MonthDay

@Component
@Profile("demo & local-dev & !contracts")
class DemoTrailInitializer(
    private val trails: TrailRepositoryAdapter
) : ApplicationRunner {

    @Transactional
    override fun run(args: ApplicationArguments) {

        val demoTrails = listOf(

            Trail(
                id = TrailId("DEMO-TRAIL-001"),
                name = "Sentier découverte (démo)",
                difficulty = Difficulty.EASY,
                hikingAllowed = true,
                season = OpeningSeason(
                    MonthDay.of(1, 1),
                    MonthDay.of(12, 31)
                ),
                trailhead = "Point de départ A"
            ),

            Trail(
                id = TrailId("DEMO-TRAIL-002"),
                name = "Sentier forestier (démo)",
                difficulty = Difficulty.MODERATE,
                hikingAllowed = true,
                season = OpeningSeason(
                    MonthDay.of(1, 1),
                    MonthDay.of(12, 31)
                ),
                trailhead = "Point de départ B"
            ),

            Trail(
                id = TrailId("DEMO-TRAIL-003"),
                name = "Sentier du sommet (démo)",
                difficulty = Difficulty.HARD,
                hikingAllowed = true,
                season = OpeningSeason(
                    MonthDay.of(1, 1),
                    MonthDay.of(12, 31)
                ),
                trailhead = "Point de départ C"
            ),

            Trail(
                id = TrailId("DEMO-TRAIL-004"),
                name = "Sentier estival (démo)",
                difficulty = Difficulty.EASY,
                hikingAllowed = true,
                season = OpeningSeason(
                    MonthDay.of(5, 1),
                    MonthDay.of(9, 30)
                ),
                trailhead = "Point de départ D"
            )
        )

        demoTrails.forEach { trail ->

            if (trails.findById(trail.id) == null) {
                trails.save(trail)
            }
        }
    }
}