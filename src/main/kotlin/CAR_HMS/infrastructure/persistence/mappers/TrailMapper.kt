package CAR_HMS.infrastructure.persistence.mappers

import CAR_HMS.domain.trails.OpeningSeason
import CAR_HMS.domain.trails.Trail
import CAR_HMS.domain.trails.TrailId
import CAR_HMS.infrastructure.persistence.entities.TrailEntity
import java.time.MonthDay

object TrailMapper {

    fun toDomain(entity: TrailEntity): Trail =
        Trail(
            id = TrailId(entity.id),
            name = entity.name,
            difficulty = entity.difficulty,
            hikingAllowed = entity.hikingAllowed,
            season = OpeningSeason(
                MonthDay.of(
                    entity.openingMonth,
                    entity.openingDay
                ),
                MonthDay.of(
                    entity.closingMonth,
                    entity.closingDay
                )
            ),
            trailhead = entity.trailhead
        )

    fun toEntity(trail: Trail): TrailEntity =
        TrailEntity(
            id = trail.id.value,
            name = trail.name,
            difficulty = trail.difficulty,
            hikingAllowed = trail.hikingAllowed,
            openingMonth = trail.season.opensOn.monthValue,
            openingDay = trail.season.opensOn.dayOfMonth,
            closingMonth = trail.season.closesOn.monthValue,
            closingDay = trail.season.closesOn.dayOfMonth,
            trailhead = trail.trailhead
        )
}