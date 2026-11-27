package CAR_HMS.infrastructure.persistence.entities

import CAR_HMS.domain.hikes.HikeStatus
import jakarta.persistence.*
import java.time.LocalDateTime

@Entity
@Table(name = "hikes")
class HikeEntity(

    @Id
    val id: String,

    @Column(nullable = false)
    val organizerId: String,

    @Column(nullable = false)
    val title: String,

    @Column(nullable = false, length = 5000)
    val description: String,

    @Column(nullable = false)
    val trailId: String,

    @Column(nullable = false)
    val startDateTime: LocalDateTime,

    @Column(nullable = false)
    val expectedDurationSeconds: Long,

    @Column(nullable = false)
    val registrationDeadline: LocalDateTime,

    @Column(nullable = false)
    val capacity: Int,

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    val status: HikeStatus,

    @OneToMany(
        cascade = [CascadeType.ALL],
        orphanRemoval = true
    )
    @JoinColumn(name = "hike_id")
    val signUps: MutableList<SignUpEntity> =
        mutableListOf(),

    @ElementCollection
    @CollectionTable(
        name = "hike_transitions",
        joinColumns = [JoinColumn(name = "hike_id")]
    )
    @OrderColumn(name = "transition_index")
    val transitions: MutableList<StatusTransitionEntity> =
        mutableListOf(),

    @Column(nullable = false)
    val reminderSent: Boolean = false
)