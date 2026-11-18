package CAR_HMS.application.usecases.implementation

import CAR_HMS.application.dtos.queries.CloseHikeQuery
import CAR_HMS.application.services.EmailService
import CAR_HMS.application.services.TimeService
import CAR_HMS.application.usecases.CloseHike
import CAR_HMS.domain.hikes.HikeId
import CAR_HMS.domain.hikes.HikesFacade
import CAR_HMS.domain.members.MembersFacade
import CAR_HMS.domain.notifications.NotificationsFacade
import org.springframework.stereotype.Service

@Service
class CloseHikeImpl(
    private val members: MembersFacade,
    private val hikes: HikesFacade,
    private val notifications: NotificationsFacade,
    private val email: EmailService,
    private val time: TimeService
) : CloseHike {

    override fun closeHike(query: CloseHikeQuery) {
        val hikeId = HikeId(query.hikeId)
        val removed = hikes.closeHike(hikeId, SYSTEM_ACTOR, time.now())
        val title = hikes.getHike(hikeId).title
        removed.forEach { hikerId ->
            email.send(notifications.waitlistClosed(members.emailOf(hikerId), title))
        }
    }

    private companion object {
        const val SYSTEM_ACTOR = "système"
    }
}
