package CAR_HMS.application.services

import CAR_HMS.domain.notifications.EmailMessage

interface EmailService {
    fun send(message: EmailMessage)
}
