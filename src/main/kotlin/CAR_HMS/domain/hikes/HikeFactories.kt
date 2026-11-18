package CAR_HMS.domain.hikes

import CAR_HMS.domain.members.HikerId
import CAR_HMS.domain.trails.TrailId
import java.time.LocalDateTime

interface HikeFactory {
    fun createHike(
        organizerId: HikerId,
        title: String,
        description: String,
        trailId: TrailId,
        schedule: HikeSchedule,
        capacity: Capacity
    ): Hike
}

interface SignUpFactory {
    fun createSignUp(hikerId: HikerId, placement: SignUpPlacement, at: LocalDateTime): SignUp
}
