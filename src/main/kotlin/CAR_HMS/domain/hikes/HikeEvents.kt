package CAR_HMS.domain.hikes

import CAR_HMS.domain.common.DomainEvent
import CAR_HMS.domain.members.HikerId
import java.time.LocalDateTime

data class HikeCreated(
    val hikeId: HikeId,
    val organizerId: HikerId,
    override val occurredAt: LocalDateTime
) : DomainEvent

data class HikeStatusChanged(
    val hikeId: HikeId,
    val from: HikeStatus?,
    val to: HikeStatus,
    val actor: String,
    override val occurredAt: LocalDateTime
) : DomainEvent

data class HikeCancelled(
    val hikeId: HikeId,
    val reason: String,
    override val occurredAt: LocalDateTime
) : DomainEvent

data class HikeClosed(
    val hikeId: HikeId,
    override val occurredAt: LocalDateTime
) : DomainEvent

data class SignUpConfirmed(
    val hikeId: HikeId,
    val signUpId: SignUpId,
    val hikerId: HikerId,
    override val occurredAt: LocalDateTime
) : DomainEvent

data class SignUpWaitlisted(
    val hikeId: HikeId,
    val signUpId: SignUpId,
    val hikerId: HikerId,
    val position: Int,
    override val occurredAt: LocalDateTime
) : DomainEvent

data class SignUpCancelled(
    val hikeId: HikeId,
    val signUpId: SignUpId,
    val hikerId: HikerId,
    override val occurredAt: LocalDateTime
) : DomainEvent

data class SignUpPromoted(
    val hikeId: HikeId,
    val signUpId: SignUpId,
    val hikerId: HikerId,
    override val occurredAt: LocalDateTime
) : DomainEvent

data class WaitlistPositionChanged(
    val hikeId: HikeId,
    val signUpId: SignUpId,
    val position: Int,
    override val occurredAt: LocalDateTime
) : DomainEvent

data class HikeReminderSent(
    val hikeId: HikeId,
    override val occurredAt: LocalDateTime
) : DomainEvent
