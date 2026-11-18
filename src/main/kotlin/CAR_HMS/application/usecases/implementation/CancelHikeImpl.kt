package CAR_HMS.application.usecases.implementation

import CAR_HMS.application.dtos.queries.CancelHikeQuery
import CAR_HMS.application.services.EmailService
import CAR_HMS.application.services.TimeService
import CAR_HMS.application.usecases.CancelHike
import CAR_HMS.domain.authentication.AuthenticationFacade
import CAR_HMS.domain.hikes.HikeId
import CAR_HMS.domain.hikes.HikesFacade
import CAR_HMS.domain.members.MembersFacade
import CAR_HMS.domain.notifications.NotificationsFacade
import org.springframework.stereotype.Service

@Service
class CancelHikeImpl(
    private val access: AccessControl,
    private val authentication: AuthenticationFacade,
    private val members: MembersFacade,
    private val hikes: HikesFacade,
    private val notifications: NotificationsFacade,
    private val email: EmailService,
    private val time: TimeService
) : CancelHike {

    override fun cancelHike(query: CancelHikeQuery) {
        access.requireSelf(query.requesterEmail)
        val hikeId = HikeId(query.hikeId)
        val affected = hikes.cancelHike(
            hikeId,
            members.findHikerId(query.requesterEmail),
            authentication.isAdministrator(query.requesterEmail),
            query.reason,
            query.requesterEmail,
            time.now()
        )
        val title = hikes.getHike(hikeId).title
        affected.forEach { hikerId ->
            email.send(notifications.hikeCancellation(members.emailOf(hikerId), title, query.reason))
        }
    }
}
