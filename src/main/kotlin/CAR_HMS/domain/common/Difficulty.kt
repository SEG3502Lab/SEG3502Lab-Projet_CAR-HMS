package CAR_HMS.domain.common

/** Échelle de difficulté de la CCN, partagée par les sentiers, les organisateurs et les randonnées. */
enum class Difficulty(val label: String, val rank: Int) {
    EASY("facile", 1),
    MODERATE("modéré", 2),
    HARD("difficile", 3),
    VERY_HARD("très difficile", 4);

    companion object {
        fun fromLabel(label: String): Difficulty =
            entries.firstOrNull { it.label == label } ?: throw DomainException("difficulté invalide")
    }
}
