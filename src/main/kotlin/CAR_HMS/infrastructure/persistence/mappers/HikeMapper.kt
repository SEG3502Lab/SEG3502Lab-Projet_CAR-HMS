package CAR_HMS.infrastructure.persistence.mappers

import CAR_HMS.domain.hikes.*
import CAR_HMS.domain.members.HikerId
import CAR_HMS.domain.trails.TrailId
import CAR_HMS.infrastructure.persistence.entities.*
import java.time.Duration

object HikeMapper {

    fun toDomain(entity: HikeEntity): Hike =
        Hike(
            id = HikeId(entity.id),
            organizerId = HikerId(entity.organizerId),
            title = entity.title,
            description = entity.description,
            trailId = TrailId(entity.trailId),

            schedule = HikeSchedule(
                entity.startDateTime,
                Duration.ofSeconds(
                    entity.expectedDurationSeconds
                ),
                entity.registrationDeadline
            ),

            capacity = Capacity(entity.capacity),

            status = entity.status,

            signUps = entity.signUps.map {
                SignUp(
                    SignUpId(it.id),
                    HikerId(it.hikerId),
                    it.signedUpAt,
                    it.status,
                    it.waitlistPosition
                )
            },

            transitions = entity.transitions.map {
                StatusTransition(
                    it.fromStatus,
                    it.toStatus,
                    it.at,
                    it.actor
                )
            },

            reminderSent = entity.reminderSent
        )

    fun toEntity(hike: Hike): HikeEntity =
        HikeEntity(
            id = hike.id.value,
            organizerId = hike.organizerId.value,
            title = hike.title,
            description = hike.description,
            trailId = hike.trailId.value,

            startDateTime =
                hike.schedule.startDateTime,

            expectedDurationSeconds =
                hike.schedule.expectedDuration.seconds,

            registrationDeadline =
                hike.schedule.registrationDeadline,

            capacity = hike.capacity.value,

            status = hike.status,

            signUps = hike.signUps.map {
                SignUpEntity(
                    id = it.id.value,
                    hikerId = it.hikerId.value,
                    signedUpAt = it.signedUpAt,
                    status = it.status,
                    waitlistPosition = it.waitlistPosition
                )
            }.toMutableList(),

            transitions = hike.transitions.map {
                StatusTransitionEntity(
                    fromStatus = it.from,
                    toStatus = it.to,
                    at = it.at,
                    actor = it.actor
                )
            }.toMutableList(),

            reminderSent = hike.reminderSent
        )
}