package CAR_HMS.application.usecases.implementation

import CAR_HMS.application.dtos.queries.SignUpForHikeQuery
import CAR_HMS.application.services.EmailService
import CAR_HMS.application.services.TimeService
import CAR_HMS.application.usecases.SignUpForHike
import CAR_HMS.domain.hikes.HikeId
import CAR_HMS.domain.hikes.HikesFacade
import CAR_HMS.domain.hikes.SignUpStatus
import CAR_HMS.domain.members.MembersFacade
import CAR_HMS.domain.notifications.NotificationsFacade
import CAR_HMS.domain.trails.TrailsFacade
import org.springframework.stereotype.Service

@Service
class SignUpForHikeImpl(
    private val access: AccessControl,
    private val members: MembersFacade,
    private val trails: TrailsFacade,
    private val hikes: HikesFacade,
    private val notifications: NotificationsFacade,
    private val email: EmailService,
    private val time: TimeService
) : SignUpForHike {

    override fun signUpForHike(query: SignUpForHikeQuery): String {
        access.requireSelf(query.hikerEmail)
        val hiker = members.requireActiveMember(query.hikerEmail)
        val hikeId = HikeId(query.hikeId)
        val hike = hikes.getHike(hikeId)
        val trail = trails.getTrail(hike.trailId)

        val result = hikes.signUp(
            hikeId, hiker.id, hiker.skillLevel.rank, trail.difficulty, query.hikerEmail, time.now()
        )
        val message = if (result.status == SignUpStatus.CONFIRMED) {
            notifications.signUpConfirmation(query.hikerEmail, hike.title)
        } else {
            notifications.waitlistPlacement(query.hikerEmail, hike.title, result.waitlistPosition!!)
        }
        email.send(message)
        return result.signUpId.value
    }
}
