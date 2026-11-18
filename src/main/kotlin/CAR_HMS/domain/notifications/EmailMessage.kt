package CAR_HMS.domain.notifications

enum class NotificationType {
    SIGN_UP_CONFIRMATION,
    WAITLIST_PLACEMENT,
    HIKE_CANCELLATION,
    WAITLIST_CLOSED,
    HIKE_REMINDER
}

enum class WeatherWarning(val label: String) {
    RAIN("pluie"),
    SNOW("neige");

    companion object {
        fun fromCondition(condition: String?): WeatherWarning? = entries.firstOrNull { it.label == condition }
    }
}

/** Contenu d'un rappel envoyé aux participants confirmés. */
data class ReminderContent(
    val hikeTitle: String,
    val date: String,
    val time: String,
    val trailName: String,
    val trailhead: String,
    val weatherWarning: WeatherWarning?
)

/** Objet-valeur décrivant un courriel prêt à être remis au service de courriel. */
data class EmailMessage(
    val recipient: String,
    val type: NotificationType,
    val subject: String,
    val body: String,
    val waitlistPosition: Int? = null,
    val cancellationReason: String? = null,
    val reminder: ReminderContent? = null
)
