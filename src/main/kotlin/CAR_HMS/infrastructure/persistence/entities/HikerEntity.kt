package CAR_HMS.infrastructure.persistence.entities

import CAR_HMS.domain.common.Difficulty
import CAR_HMS.domain.members.MembershipStatus
import CAR_HMS.domain.members.SkillLevel
import jakarta.persistence.*
import java.time.LocalDate

@Entity
@Table(name = "hikers")
class HikerEntity(

    @Id
    val id: String,

    @Column(nullable = false, unique = true)
    val email: String,

    @Column(nullable = false)
    val firstName: String,

    @Column(nullable = false)
    val lastName: String,

    @Column(nullable = false)
    val phoneNumber: String,

    @Column(nullable = false)
    val street: String,

    @Column(nullable = false)
    val city: String,

    @Column(nullable = false)
    val province: String,

    @Column(nullable = false)
    val postalCode: String,

    @Column(nullable = false)
    val dateOfBirth: LocalDate,

    @Column(nullable = false)
    val emergencyContactName: String,

    @Column(nullable = false)
    val emergencyContactPhone: String,

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    val skillLevel: SkillLevel,

    @Column(nullable = false)
    val waiverAccepted: Boolean,

    val guardianName: String?,

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    val membershipStatus: MembershipStatus,

    @Column(unique = true)
    val certificateNumber: String? = null,

    val certificateIssueDate: LocalDate? = null,

    val certificateExpiryDate: LocalDate? = null,

    @Enumerated(EnumType.STRING)
    val organizerLevel: Difficulty? = null
)