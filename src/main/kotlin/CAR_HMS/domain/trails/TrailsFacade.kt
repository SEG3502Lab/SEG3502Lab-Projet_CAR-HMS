package CAR_HMS.domain.trails

import CAR_HMS.domain.common.Difficulty
import CAR_HMS.domain.common.DomainException
import org.springframework.stereotype.Service
import java.time.LocalDate

/** Vue en lecture d'un sentier, exposée aux autres sous-domaines. */
data class TrailDetails(
    val id: TrailId,
    val name: String,
    val difficulty: Difficulty,
    val trailhead: String
)

@Service
class TrailsFacade(private val trails: TrailRepository) {

    fun getTrail(id: TrailId): TrailDetails = load(id).toDetails()

    fun requireHikeableTrail(id: TrailId, date: LocalDate): TrailDetails {
        val trail = load(id)
        trail.requireHikeableOn(date)
        return trail.toDetails()
    }

    private fun load(id: TrailId): Trail =
        trails.findById(id) ?: throw DomainException("sentier introuvable")

    private fun Trail.toDetails() = TrailDetails(id, name, difficulty, trailhead)
}
