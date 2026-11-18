package CAR_HMS.domain.reports

import CAR_HMS.domain.common.DomainException
import CAR_HMS.domain.trails.TrailId
import java.time.LocalDate

data class ReportPeriod(val from: LocalDate, val to: LocalDate) {
    init {
        if (to.isBefore(from)) throw DomainException("période de rapport invalide")
    }

    fun contains(date: LocalDate): Boolean = !date.isBefore(from) && !date.isAfter(to)
}

/** Donnée d'entrée du rapport : utilisation d'un sentier par une randonnée. */
data class HikeUsageRecord(val date: LocalDate, val participants: Int, val cancelled: Boolean)

/** Objet-valeur : statistiques calculées pour un sentier sur une période. */
data class TrailUsageReport(
    val trailId: TrailId,
    val period: ReportPeriod,
    val hikeCount: Int,
    val participantCount: Int,
    val cancelledHikeCount: Int
)
