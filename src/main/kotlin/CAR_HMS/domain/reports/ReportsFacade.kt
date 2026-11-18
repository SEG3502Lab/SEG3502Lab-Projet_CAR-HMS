package CAR_HMS.domain.reports

import CAR_HMS.domain.trails.TrailId
import org.springframework.stereotype.Service

@Service
class ReportsFacade(private val generator: TrailUsageReportGenerator) {
    fun trailUsageReport(trailId: TrailId, period: ReportPeriod, records: List<HikeUsageRecord>): TrailUsageReport =
        generator.generate(trailId, period, records)
}
