package CAR_HMS.domain.reports

import CAR_HMS.domain.trails.TrailId
import org.springframework.stereotype.Service

/** Service de domaine : agrège l'utilisation d'un sentier sur une période. */
@Service
class TrailUsageReportGenerator {
    fun generate(trailId: TrailId, period: ReportPeriod, records: List<HikeUsageRecord>): TrailUsageReport {
        val inPeriod = records.filter { period.contains(it.date) }
        val held = inPeriod.filterNot { it.cancelled }
        return TrailUsageReport(
            trailId, period, held.size, held.sumOf { it.participants }, inPeriod.count { it.cancelled }
        )
    }
}
