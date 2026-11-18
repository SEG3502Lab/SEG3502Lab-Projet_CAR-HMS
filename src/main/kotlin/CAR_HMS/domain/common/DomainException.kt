package CAR_HMS.domain.common

/** Refus d'une commande : le message porte le motif métier prévu par les contrats. */
class DomainException(val reason: String) : RuntimeException(reason)
