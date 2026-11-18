package CAR_HMS.application.usecases.implementation

import CAR_HMS.application.dtos.queries.CancelSignUpQuery
import CAR_HMS.application.services.EmailService
import CAR_HMS.application.services.TimeService
import CAR_HMS.application.usecases.CancelSignUp
import CAR_HMS.domain.hikes.HikesFacade
import CAR_HMS.domain.hikes.SignUpId
import CAR_HMS.domain.members.MembersFacade
import CAR_HMS.domain.notifications.NotificationsFacade
import org.springframework.stereotype.Service

@Service
class CancelSignUpImpl(
    private val access: AccessControl,
    private val members: MembersFacade,
    private val hikes: HikesFacade,
    private val notifications: NotificationsFacade,
    private val email: EmailService,
    private val time: TimeService
) : CancelSignUp {

    override fun cancelSignUp(query: CancelSignUpQuery) {
        access.requireSelf(query.hikerEmail)
        val hiker = members.getProfile(query.hikerEmail)
        val signUpId = SignUpId(query.signUpId)
        val promoted = hikes.cancelSignUp(signUpId, hiker.id, query.hikerEmail, time.now()) ?: return
        val title = hikes.getHikeOfSignUp(signUpId).title
        email.send(notifications.signUpConfirmation(members.emailOf(promoted), title))
    }
}
