package CAR_HMS.domain.notifications

import org.springframework.stereotype.Service
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

@Service
class NotificationsFacade(private val composer: NotificationComposer) {

    fun signUpConfirmation(recipient: String, hikeTitle: String): EmailMessage =
        composer.signUpConfirmation(recipient, hikeTitle)

    fun waitlistPlacement(recipient: String, hikeTitle: String, position: Int): EmailMessage =
        composer.waitlistPlacement(recipient, hikeTitle, position)

    fun hikeCancellation(recipient: String, hikeTitle: String, reason: String): EmailMessage =
        composer.hikeCancellation(recipient, hikeTitle, reason)

    fun waitlistClosed(recipient: String, hikeTitle: String): EmailMessage =
        composer.waitlistClosed(recipient, hikeTitle)

    fun hikeReminder(
        recipient: String,
        hikeTitle: String,
        start: LocalDateTime,
        trailName: String,
        trailhead: String,
        weatherCondition: String?
    ): EmailMessage = composer.hikeReminder(
        recipient,
        ReminderContent(
            hikeTitle,
            start.toLocalDate().toString(),
            start.format(TIME_FORMAT),
            trailName,
            trailhead,
            WeatherWarning.fromCondition(weatherCondition)
        )
    )

    private companion object {
        val TIME_FORMAT: DateTimeFormatter = DateTimeFormatter.ofPattern("HH:mm")
    }
}
