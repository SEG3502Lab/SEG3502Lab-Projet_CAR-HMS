package CAR_HMS.infrastructure.persistence.entities

import CAR_HMS.domain.hikes.HikeStatus
import jakarta.persistence.*
import java.time.LocalDateTime

@Embeddable
class StatusTransitionEntity(

    @Enumerated(EnumType.STRING)
    @Column(name = "previous_status")
    val fromStatus: HikeStatus?,

    @Enumerated(EnumType.STRING)
    @Column(name = "new_status", nullable = false)
    val toStatus: HikeStatus,

    @Column(name = "transition_at", nullable = false)
    val at: LocalDateTime,

    @Column(nullable = false)
    val actor: String
)