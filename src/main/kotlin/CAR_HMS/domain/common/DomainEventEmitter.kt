package CAR_HMS.domain.common

/** Port de publication des événements de domaine. */
interface DomainEventEmitter {
    fun emit(event: DomainEvent)
}
