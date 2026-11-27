package CAR_HMS.infrastructure.persistence.entities

import CAR_HMS.domain.hikes.SignUpStatus
import jakarta.persistence.*
import java.time.LocalDateTime

@Entity
@Table(name = "hike_signups")
class SignUpEntity(

    @Id
    val id: String,

    @Column(nullable = false)
    val hikerId: String,

    @Column(nullable = false)
    val signedUpAt: LocalDateTime,

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    val status: SignUpStatus,

    val waitlistPosition: Int? = null
)