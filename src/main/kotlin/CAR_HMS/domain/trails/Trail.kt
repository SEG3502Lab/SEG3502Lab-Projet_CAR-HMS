package CAR_HMS.domain.trails

import CAR_HMS.domain.common.AggregateRoot
import CAR_HMS.domain.common.Difficulty
import CAR_HMS.domain.common.DomainException
import java.time.LocalDate
import java.time.MonthDay

data class TrailId(val value: String)

/** Saison d'ouverture annuelle : elle se répète chaque année, d'où l'usage de MonthDay. */
data class OpeningSeason(val opensOn: MonthDay, val closesOn: MonthDay) {
    fun contains(date: LocalDate): Boolean {
        val day = MonthDay.from(date)

        return if (!opensOn.isAfter(closesOn)) {
            !day.isBefore(opensOn) && !day.isAfter(closesOn)
        } else {
            !day.isBefore(opensOn) || !day.isAfter(closesOn)
        }
    }
}

/** Racine de l'agrégat des sentiers, alimenté par les données de la CCN (lecture seule). */
class Trail(
    val id: TrailId,
    val name: String,
    val difficulty: Difficulty,
    val hikingAllowed: Boolean,
    val season: OpeningSeason,
    val trailhead: String
) : AggregateRoot() {

    fun requireHikeableOn(date: LocalDate) {
        if (!hikingAllowed) throw DomainException("randonnée non permise sur ce sentier")
        if (!season.contains(date)) throw DomainException("date hors de la saison d'ouverture")
    }
}
