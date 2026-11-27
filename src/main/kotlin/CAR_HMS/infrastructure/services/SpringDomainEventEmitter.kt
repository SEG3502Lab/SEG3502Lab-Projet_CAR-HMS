package CAR_HMS.infrastructure.services

import CAR_HMS.domain.common.DomainEvent
import CAR_HMS.domain.common.DomainEventEmitter
import org.springframework.context.ApplicationEventPublisher
import org.springframework.context.annotation.Profile
import org.springframework.stereotype.Component

@Component
@Profile("!contracts")
class SpringDomainEventEmitter(
    private val publisher: ApplicationEventPublisher
) : DomainEventEmitter {

    override fun emit(event: DomainEvent) {
        publisher.publishEvent(event)
    }
}