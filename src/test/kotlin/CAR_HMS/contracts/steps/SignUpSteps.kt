package CAR_HMS.contracts.steps

import CAR_HMS.application.dtos.queries.CancelSignUpQuery
import CAR_HMS.application.dtos.queries.SignUpForHikeQuery
import CAR_HMS.application.usecases.CancelSignUp
import CAR_HMS.application.usecases.SignUpForHike
import CAR_HMS.contracts.stubs.StubCurrentUserService
import CAR_HMS.contracts.stubs.StubEmailService
import CAR_HMS.contracts.support.ContractWorld
import CAR_HMS.contracts.support.signUpStatus
import CAR_HMS.domain.notifications.NotificationType
import io.cucumber.java.fr.Alors
import io.cucumber.java.fr.Quand
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

class SignUpSteps(
    private val world: ContractWorld,
    private val signUpForHike: SignUpForHike,
    private val cancelSignUp: CancelSignUp,
    private val currentUser: StubCurrentUserService,
    private val emails: StubEmailService
) {
    @Quand("{string} s'inscrit à {string}")
    fun signUp(email: String, hikeId: String) = world.act {
        world.returnedId = signUpForHike.signUpForHike(SignUpForHikeQuery(hikeId, email))
    }

    @Quand("un visiteur non connecté s'inscrit à {string}")
    fun visitorSignsUp(hikeId: String) {
        currentUser.email = null
        signUp("visiteur@courriel.ca", hikeId)
    }

    @Quand("{string} annule l'inscription {string}")
    fun cancel(email: String, signUpId: String) = world.act {
        world.ensureHiker(email)
        currentUser.email = email
        cancelSignUp.cancelSignUp(CancelSignUpQuery(signUpId, email))
    }

    @Alors("un identifiant d'inscription est retourné")
    fun idReturned() {
        assertNotNull(world.returnedId)
    }

    @Alors("l'inscription a le statut {string}")
    fun newSignUpHasStatus(status: String) {
        assertEquals(signUpStatus(status), world.signUp(world.returnedId!!).status)
    }

    @Alors("l'inscription a le statut {string} à la position {int}")
    fun newSignUpAtPosition(status: String, position: Int) {
        val signUp = world.signUp(world.returnedId!!)
        assertEquals(signUpStatus(status), signUp.status)
        assertEquals(position, signUp.waitlistPosition)
    }

    @Alors("{string} reçoit un courriel de confirmation")
    fun confirmationEmail(email: String) {
        assertEquals(1, emails.sentTo(email, NotificationType.SIGN_UP_CONFIRMATION).size)
    }

    @Alors("{string} reçoit un courriel indiquant sa position {int} en liste d'attente")
    fun waitlistEmail(email: String, position: Int) {
        assertEquals(position, emails.sentTo(email, NotificationType.WAITLIST_PLACEMENT).single().waitlistPosition)
    }

    @Alors("{string} a une seule inscription active à {string}")
    fun singleActiveSignUp(email: String, hikeId: String) {
        val hikerId = world.hiker(email).id
        assertEquals(1, world.hike(hikeId).signUps.count { it.hikerId == hikerId && it.isActive })
    }
}
