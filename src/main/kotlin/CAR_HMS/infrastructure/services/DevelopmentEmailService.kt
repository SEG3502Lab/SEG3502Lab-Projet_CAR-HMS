package CAR_HMS.infrastructure.services

import CAR_HMS.application.services.EmailService
import CAR_HMS.domain.notifications.EmailMessage
import org.slf4j.LoggerFactory
import org.springframework.context.annotation.Profile
import org.springframework.stereotype.Component

@Component
@Profile("local-dev & !contracts")
class DevelopmentEmailService : EmailService {

    private val logger =
        LoggerFactory.getLogger(
            DevelopmentEmailService::class.java
        )

    override fun send(message: EmailMessage) {
        logger.info(
            "Development mode: email delivery simulated"
        )
    }
}