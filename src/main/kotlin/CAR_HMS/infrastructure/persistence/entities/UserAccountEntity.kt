package CAR_HMS.infrastructure.persistence.entities

import CAR_HMS.domain.authentication.Role
import jakarta.persistence.*

@Entity
@Table(name = "user_accounts")
class UserAccountEntity(

    @Id
    val id: String,

    @Column(nullable = false, unique = true)
    val email: String,

    @Column(nullable = false)
    val passwordHash: String,

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    val role: Role
)