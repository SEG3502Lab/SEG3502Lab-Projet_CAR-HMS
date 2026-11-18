package CAR_HMS.contracts.steps

import CAR_HMS.application.dtos.queries.CancelHikeQuery
import CAR_HMS.application.dtos.queries.CloseHikeQuery
import CAR_HMS.application.dtos.queries.SendHikeReminderQuery
import CAR_HMS.application.usecases.CancelHike
import CAR_HMS.application.usecases.CloseHike
import CAR_HMS.application.usecases.SendHikeReminder
import CAR_HMS.contracts.stubs.StubCurrentUserService
import CAR_HMS.contracts.stubs.StubEmailService
import CAR_HMS.contracts.stubs.StubUserAccountFactory
import CAR_HMS.contracts.stubs.StubUserAccountRepository
import CAR_HMS.contracts.stubs.StubWeatherService
import CAR_HMS.contracts.support.ContractWorld
import CAR_HMS.domain.authentication.Role
import CAR_HMS.domain.notifications.NotificationType
import CAR_HMS.domain.notifications.WeatherWarning
import io.cucumber.java.fr.Alors
import io.cucumber.java.fr.Etantdonné
import io.cucumber.java.fr.Quand
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue

class HikeLifecycleSteps(
    private val world: ContractWorld,
    private val cancelHike: CancelHike,
    private val closeHike: CloseHike,
    private val sendHikeReminder: SendHikeReminder,
    private val currentUser: StubCurrentUserService,
    private val emails: StubEmailService,
    private val weather: StubWeatherService,
    private val accounts: StubUserAccountRepository,
    private val accountFactory: StubUserAccountFactory
) {
    @Etantdonné("l'administratrice {string} existe")
    fun administratorExists(email: String) {
        accounts.save(accountFactory.createAccount(email, "hash", Role.ADMINISTRATOR))
    }

    @Quand("{string} annule {string} avec le motif {string}")
    fun cancel(email: String, hikeId: String, reason: String) = world.act {
        currentUser.email = email
        cancelHike.cancelHike(CancelHikeQuery(hikeId, email, reason))
    }

    @Alors("{string}, {string} et {string} reçoivent un courriel d'annulation contenant le motif {string}")
    fun cancellationEmails(first: String, second: String, third: String, reason: String) {
        listOf(first, second, third).forEach { email ->
            val message = emails.sentTo(email, NotificationType.HIKE_CANCELLATION).single()
            assertEquals(reason, message.cancellationReason)
        }
    }

    @Quand("le système ferme la randonnée {string}")
    fun close(hikeId: String) = world.act {
        closeHike.closeHike(CloseHikeQuery(hikeId))
    }

    @Alors("{string} et {string} reçoivent un courriel les avisant que la liste d'attente est fermée")
    fun waitlistClosedEmails(first: String, second: String) {
        listOf(first, second).forEach { email ->
            assertEquals(1, emails.sentTo(email, NotificationType.WAITLIST_CLOSED).size)
        }
    }

    @Etantdonné("aucun rappel n'a été envoyé pour {string}")
    fun noReminderYet(hikeId: String) {
        world.fixture(hikeId).reminderSent = false
    }

    @Etantdonné("un rappel a déjà été envoyé pour {string}")
    fun reminderAlreadySent(hikeId: String) {
        world.fixture(hikeId).reminderSent = true
    }

    @Etantdonné("la météo prévue pour le {word} au Lac-Pink est {string}")
    fun forecast(date: String, condition: String) {
        weather.condition = condition
    }

    @Etantdonné("le service météo est indisponible")
    fun weatherUnavailable() {
        weather.available = false
    }

    @Quand("le système envoie le rappel de {string}")
    fun sendReminder(hikeId: String) = world.act {
        sendHikeReminder.sendHikeReminder(SendHikeReminderQuery(hikeId))
    }

    private fun reminderOf(email: String) =
        assertNotNull(emails.sentTo(email, NotificationType.HIKE_REMINDER).single().reminder)

    @Alors("{string} et {string} reçoivent un rappel indiquant la date {string}, l'heure {string}, le sentier {string} et le point de départ {string}")
    fun reminderDetails(first: String, second: String, date: String, hour: String, trail: String, trailhead: String) {
        listOf(first, second).forEach { email ->
            val reminder = reminderOf(email)
            assertEquals(date, reminder.date)
            assertEquals(hour, reminder.time)
            assertEquals(trail, reminder.trailName)
            assertEquals(trailhead, reminder.trailhead)
        }
    }

    @Alors("le rappel ne contient aucun avertissement météo")
    fun noWarningInAnyReminder() {
        val reminders = emails.ofType(NotificationType.HIKE_REMINDER)
        assertTrue(reminders.isNotEmpty())
        reminders.forEach { assertNull(it.reminder?.weatherWarning) }
    }

    @Alors("{string} ne reçoit aucun rappel")
    fun noReminderFor(email: String) {
        assertTrue(emails.sentTo(email, NotificationType.HIKE_REMINDER).isEmpty())
    }

    @Alors("le rappel de {string} est marqué comme envoyé")
    fun reminderMarked(hikeId: String) {
        assertTrue(world.hike(hikeId).reminderSent)
    }

    @Alors("{string} et {string} reçoivent un rappel contenant un avertissement de {word}")
    fun reminderWithWarning(first: String, second: String, warning: String) {
        val expected = WeatherWarning.entries.first { it.label == warning }
        listOf(first, second).forEach { assertEquals(expected, reminderOf(it).weatherWarning) }
    }

    @Alors("{string} et {string} reçoivent un rappel sans avertissement météo")
    fun reminderWithoutWarning(first: String, second: String) {
        listOf(first, second).forEach { assertNull(reminderOf(it).weatherWarning) }
    }

    @Alors("aucun rappel n'est envoyé")
    fun noReminderSent() {
        assertTrue(emails.ofType(NotificationType.HIKE_REMINDER).isEmpty())
    }

    @Alors("aucun nouveau rappel n'est envoyé")
    fun noNewReminderSent() {
        noReminderSent()
        assertFalse(world.refusal == null)
    }
}
