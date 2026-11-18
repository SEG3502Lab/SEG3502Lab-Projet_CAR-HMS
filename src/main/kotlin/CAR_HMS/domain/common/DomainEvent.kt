package CAR_HMS.domain.common

import java.time.LocalDateTime

/** Fait métier survenu dans le domaine, émis pour chaque changement d'état. */
interface DomainEvent {
    val occurredAt: LocalDateTime
}
