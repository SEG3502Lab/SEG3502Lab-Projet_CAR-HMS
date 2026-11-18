package CAR_HMS.application.usecases.implementation

import CAR_HMS.application.dtos.queries.SendHikeReminderQuery
import CAR_HMS.application.services.EmailService
import CAR_HMS.application.services.TimeService
import CAR_HMS.application.services.WeatherService
import CAR_HMS.application.services.WeatherServiceUnavailableException
import CAR_HMS.application.usecases.SendHikeReminder
import CAR_HMS.domain.hikes.HikeId
import CAR_HMS.domain.hikes.HikesFacade
import CAR_HMS.domain.members.MembersFacade
import CAR_HMS.domain.notifications.NotificationsFacade
import CAR_HMS.domain.trails.TrailsFacade
import org.springframework.stereotype.Service
import java.time.LocalDate

@Service
class SendHikeReminderImpl(
    private val hikes: HikesFacade,
    private val trails: TrailsFacade,
    private val members: MembersFacade,
    private val notifications: NotificationsFacade,
    private val weather: WeatherService,
    private val email: EmailService,
    private val time: TimeService
) : SendHikeReminder {

    override fun sendHikeReminder(query: SendHikeReminderQuery) {
        val now = time.now()
        val hikeId = HikeId(query.hikeId)
        val hike = hikes.prepareReminder(hikeId, now)
        val trail = trails.getTrail(hike.trailId)
        val condition = forecastOrNull(trail.name, hike.startDateTime.toLocalDate())

        hike.confirmedHikerIds.forEach { hikerId ->
            email.send(
                notifications.hikeReminder(
                    members.emailOf(hikerId), hike.title, hike.startDateTime,
                    trail.name, trail.trailhead, condition
                )
            )
        }
        hikes.markReminderSent(hikeId, now)
    }

    /** Une panne du service météo ne bloque pas le rappel : il part sans avertissement. */
    private fun forecastOrNull(location: String, date: LocalDate): String? =
        try {
            weather.forecast(location, date)
        } catch (_: WeatherServiceUnavailableException) {
            null
        }
}
