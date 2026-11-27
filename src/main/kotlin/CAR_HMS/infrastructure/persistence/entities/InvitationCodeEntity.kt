package CAR_HMS.infrastructure.persistence.entities

import jakarta.persistence.*

@Entity
@Table(name = "invitation_codes")
class InvitationCodeEntity(

    @Id
    val code: String,

    @Column(nullable = false)
    val used: Boolean = false
)