package CAR_HMS.domain.hikes

import CAR_HMS.domain.common.Difficulty
import CAR_HMS.domain.common.DomainException
import org.springframework.stereotype.Service
import java.time.LocalDate

/** Qualification d'un organisateur, transmise par la couche application. */
data class OrganizerQualification(
    val validFrom: LocalDate,
    val validUntil: LocalDate,
    val level: Difficulty
)

/** Service de domaine : un organisateur doit être qualifié pour la date et le sentier. */
@Service
class HikePlanningPolicy {
    fun validate(qualification: OrganizerQualification, hikeDate: LocalDate, trailDifficulty: Difficulty) {
        if (hikeDate.isBefore(qualification.validFrom) || hikeDate.isAfter(qualification.validUntil)) {
            throw DomainException("certification non valide à la date de la randonnée")
        }
        if (qualification.level.rank < trailDifficulty.rank) {
            throw DomainException("niveau d'organisateur insuffisant")
        }
    }
}

/** Service de domaine : rang(difficulté) <= rang(niveau du randonneur) + 1. */
@Service
class SkillMatchingPolicy {
    fun matches(skillRank: Int, difficulty: Difficulty): Boolean = difficulty.rank <= skillRank + 1
}
