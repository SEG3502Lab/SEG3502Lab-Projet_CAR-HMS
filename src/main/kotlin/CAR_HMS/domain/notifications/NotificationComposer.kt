package CAR_HMS.domain.notifications

import org.springframework.stereotype.Service

/** Service de domaine : rédige les courriels à partir des faits métier. */
@Service
class NotificationComposer {
    fun signUpConfirmation(recipient: String, hikeTitle: String) = EmailMessage(
        recipient, NotificationType.SIGN_UP_CONFIRMATION,
        "Inscription confirmée : $hikeTitle",
        "Votre place est confirmée pour la randonnée « $hikeTitle »."
    )

    fun waitlistPlacement(recipient: String, hikeTitle: String, position: Int) = EmailMessage(
        recipient, NotificationType.WAITLIST_PLACEMENT,
        "Liste d'attente : $hikeTitle",
        "Vous êtes à la position $position de la liste d'attente de « $hikeTitle ».",
        waitlistPosition = position
    )

    fun hikeCancellation(recipient: String, hikeTitle: String, reason: String) = EmailMessage(
        recipient, NotificationType.HIKE_CANCELLATION,
        "Randonnée annulée : $hikeTitle",
        "La randonnée « $hikeTitle » est annulée. Motif : $reason.",
        cancellationReason = reason
    )

    fun waitlistClosed(recipient: String, hikeTitle: String) = EmailMessage(
        recipient, NotificationType.WAITLIST_CLOSED,
        "Liste d'attente fermée : $hikeTitle",
        "La liste d'attente de la randonnée « $hikeTitle » est fermée."
    )

    fun hikeReminder(recipient: String, content: ReminderContent): EmailMessage {
        val warning = content.weatherWarning?.let { " Attention : ${it.label} prévue." }.orEmpty()
        return EmailMessage(
            recipient, NotificationType.HIKE_REMINDER,
            "Rappel : ${content.hikeTitle}",
            "Départ le ${content.date} à ${content.time}, ${content.trailName}, " +
                "point de départ : ${content.trailhead}.$warning",
            reminder = content
        )
    }
}
