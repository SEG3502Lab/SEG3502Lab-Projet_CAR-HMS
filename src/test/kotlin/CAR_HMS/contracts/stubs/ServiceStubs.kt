package CAR_HMS.contracts.stubs

import CAR_HMS.application.services.CurrentUserService
import CAR_HMS.application.services.EmailService
import CAR_HMS.application.services.PasswordHasher
import CAR_HMS.application.services.TimeService
import CAR_HMS.application.services.WeatherService
import CAR_HMS.application.services.WeatherServiceUnavailableException
import CAR_HMS.domain.common.DomainEvent
import CAR_HMS.domain.common.DomainEventEmitter
import CAR_HMS.domain.notifications.EmailMessage
import CAR_HMS.domain.notifications.NotificationType
import org.springframework.stereotype.Component
import java.time.LocalDate
import java.time.LocalDateTime

@Component
class StubDomainEventEmitter : DomainEventEmitter, Resettable {
    val emitted = mutableListOf<DomainEvent>()

    override fun emit(event: DomainEvent) {
        emitted += event
    }

    override fun reset() = emitted.clear()
}

@Component
class StubEmailService : EmailService, Resettable {
    val sent = mutableListOf<EmailMessage>()

    override fun send(message: EmailMessage) {
        sent += message
    }

    fun sentTo(recipient: String): List<EmailMessage> = sent.filter { it.recipient == recipient }
    fun sentTo(recipient: String, type: NotificationType): List<EmailMessage> =
        sentTo(recipient).filter { it.type == type }
    fun ofType(type: NotificationType): List<EmailMessage> = sent.filter { it.type == type }

    override fun reset() = sent.clear()
}

@Component
class StubWeatherService : WeatherService, Resettable {
    var condition = "ensoleillé"
    var available = true
    var lastRequestedLocation: String? = null

    override fun forecast(location: String, date: LocalDate): String {
        lastRequestedLocation = location

        if (!available) {
            throw WeatherServiceUnavailableException()
        }

        return condition
    }

    override fun reset() {
        condition = "ensoleillé"
        available = true
        lastRequestedLocation = null
    }
}

@Component
class StubCurrentUserService : CurrentUserService, Resettable {
    var email: String? = null

    override fun currentUserEmail(): String? = email

    override fun reset() {
        email = null
    }
}

@Component
class StubPasswordHasher : PasswordHasher {
    override fun hash(rawPassword: String): String = "hash:$rawPassword"
}

@Component
class StubTimeService : TimeService, Resettable {
    var current: LocalDateTime = DEFAULT_NOW

    override fun now(): LocalDateTime = current

    override fun reset() {
        current = DEFAULT_NOW
    }

    private companion object {
        val DEFAULT_NOW: LocalDateTime = LocalDateTime.parse("2027-01-01T00:00")
    }
}
