package CAR_HMS.domain.common

/** Racine d'agrégat : accumule les événements jusqu'à leur publication par la façade. */
abstract class AggregateRoot {
    private val pendingEvents = mutableListOf<DomainEvent>()

    protected fun record(event: DomainEvent) {
        pendingEvents += event
    }

    fun pullDomainEvents(): List<DomainEvent> {
        val events = pendingEvents.toList()
        pendingEvents.clear()
        return events
    }
}
