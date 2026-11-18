package CAR_HMS.contracts.steps

import CAR_HMS.contracts.stubs.StubCurrentUserService
import CAR_HMS.contracts.stubs.StubEmailService
import CAR_HMS.contracts.stubs.StubTimeService
import CAR_HMS.contracts.support.ContractWorld
import io.cucumber.java.After
import io.cucumber.java.Before
import io.cucumber.java.fr.Alors
import io.cucumber.java.fr.Etantdonné
import java.time.LocalDateTime
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class CommonSteps(
    private val world: ContractWorld,
    private val time: StubTimeService,
    private val currentUser: StubCurrentUserService,
    private val emails: StubEmailService
) {
    @Before
    fun resetWorld() = world.reset()

    /** Un refus que le scénario n'a pas prévu fait échouer le contrat. */
    @After
    fun failOnUnexpectedRefusal() {
        if (world.refusal != null) {
            assertTrue(world.refusalAsserted, "Refus inattendu : ${world.refusal}")
        }
    }

    @Etantdonné("nous sommes le {word}")
    fun nowIs(dateTime: String) {
        time.current = LocalDateTime.parse(dateTime)
    }

    @Etantdonné("aucun utilisateur n'est connecté")
    fun nobodyLoggedIn() {
        currentUser.email = null
    }

    @Etantdonné("{string} est connecté(e)")
    fun loggedIn(email: String) {
        world.ensureHiker(email)
        currentUser.email = email
    }

    @Alors("^(?:l'annulation|la fermeture|la création|la demande|l'enregistrement|l'inscription|la modification|l'envoi) est refusée? avec le motif \"([^\"]*)\"$")
    fun refused(reason: String) {
        assertEquals(reason, world.refusal)
        world.refusalAsserted = true
    }

    @Alors("{string} ne reçoit aucun courriel")
    fun noEmail(email: String) {
        assertTrue(emails.sentTo(email).isEmpty(), "Courriel inattendu pour $email")
    }
}
