package CAR_HMS.infrastructure.factories

import CAR_HMS.domain.authentication.*
import CAR_HMS.domain.hikes.*
import CAR_HMS.domain.members.*
import CAR_HMS.domain.trails.TrailId
import org.springframework.context.annotation.Profile
import org.springframework.stereotype.Component
import java.time.LocalDateTime
import java.util.UUID

@Component
@Profile("!contracts")
class DefaultHikeFactory : HikeFactory {

    override fun createHike(
        organizerId: HikerId,
        title: String,
        description: String,
        trailId: TrailId,
        schedule: HikeSchedule,
        capacity: Capacity
    ): Hike = Hike(
        HikeId(UUID.randomUUID().toString()),
        organizerId,
        title,
        description,
        trailId,
        schedule,
        capacity,
        HikeStatus.OPEN
    )
}

@Component
@Profile("!contracts")
class DefaultSignUpFactory : SignUpFactory {

    override fun createSignUp(
        hikerId: HikerId,
        placement: SignUpPlacement,
        at: LocalDateTime
    ): SignUp = SignUp(
        SignUpId(UUID.randomUUID().toString()),
        hikerId,
        at,
        placement.status,
        placement.waitlistPosition
    )
}

@Component
@Profile("!contracts")
class DefaultHikerFactory : HikerFactory {

    override fun createHiker(
        registration: HikerRegistration
    ): Hiker = Hiker(
        HikerId(UUID.randomUUID().toString()),
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
}

@Component
@Profile("!contracts")
class DefaultUserAccountFactory : UserAccountFactory {

    override fun createAccount(
        email: String,
        passwordHash: String,
        role: Role
    ): UserAccount = UserAccount(
        UserAccountId(UUID.randomUUID().toString()),
        email,
        passwordHash,
        role
    )
}