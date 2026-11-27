package CAR_HMS.infrastructure.persistence.entities

import CAR_HMS.domain.common.Difficulty
import jakarta.persistence.*

@Entity
@Table(name = "trails")
class TrailEntity(

    @Id
    val id: String,

    @Column(nullable = false)
    val name: String,

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    val difficulty: Difficulty,

    @Column(nullable = false)
    val hikingAllowed: Boolean,

    @Column(nullable = false)
    val openingMonth: Int,

    @Column(nullable = false)
    val openingDay: Int,

    @Column(nullable = false)
    val closingMonth: Int,

    @Column(nullable = false)
    val closingDay: Int,

    @Column(nullable = false)
    val trailhead: String
)