package CAR_HMS.contracts.stubs

import CAR_HMS.domain.authentication.Role
import CAR_HMS.domain.authentication.UserAccount
import CAR_HMS.domain.authentication.UserAccountFactory
import CAR_HMS.domain.authentication.UserAccountId
import CAR_HMS.domain.hikes.Capacity
import CAR_HMS.domain.hikes.Hike
import CAR_HMS.domain.hikes.HikeFactory
import CAR_HMS.domain.hikes.HikeId
import CAR_HMS.domain.hikes.HikeSchedule
import CAR_HMS.domain.hikes.HikeStatus
import CAR_HMS.domain.hikes.SignUp
import CAR_HMS.domain.hikes.SignUpFactory
import CAR_HMS.domain.hikes.SignUpId
import CAR_HMS.domain.hikes.SignUpPlacement
import CAR_HMS.domain.members.Hiker
import CAR_HMS.domain.members.HikerFactory
import CAR_HMS.domain.members.HikerId
import CAR_HMS.domain.members.HikerRegistration
import CAR_HMS.domain.trails.TrailId
import org.springframework.stereotype.Component
import java.time.LocalDateTime

@Component
class StubHikeFactory : HikeFactory, Resettable {
    private var sequence = 0

    override fun createHike(
        organizerId: HikerId,
        title: String,
        description: String,
        trailId: TrailId,
        schedule: HikeSchedule,
        capacity: Capacity
    ): Hike = Hike(
        HikeId("H-STUB-${++sequence}"), organizerId, title, description, trailId, schedule, capacity, HikeStatus.OPEN
    )

    override fun reset() {
        sequence = 0
    }
}

@Component
class StubSignUpFactory : SignUpFactory, Resettable {
    private var sequence = 0

    override fun createSignUp(hikerId: HikerId, placement: SignUpPlacement, at: LocalDateTime): SignUp =
        SignUp(SignUpId("S-STUB-${++sequence}"), hikerId, at, placement.status, placement.waitlistPosition)

    override fun reset() {
        sequence = 0
    }
}

@Component
class StubHikerFactory : HikerFactory, Resettable {
    private var sequence = 0

    override fun createHiker(registration: HikerRegistration): Hiker = Hiker(
        HikerId("R-STUB-${++sequence}"),
        registration.email,
        registration.name,
        registration.phoneNumber,
        registration.address,
        registration.dateOfBirth,
        registration.emergencyContact,
        registration.skillLevel,
        registration.waiverAccepted,
        registration.guardianName
    )

    override fun reset() {
        sequence = 0
    }
}

@Component
class StubUserAccountFactory : UserAccountFactory, Resettable {
    private var sequence = 0

    override fun createAccount(email: String, passwordHash: String, role: Role): UserAccount =
        UserAccount(UserAccountId("U-STUB-${++sequence}"), email, passwordHash, role)

    override fun reset() {
        sequence = 0
    }
}
