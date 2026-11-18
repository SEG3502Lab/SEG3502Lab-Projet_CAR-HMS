package CAR_HMS.domain.members

import CAR_HMS.domain.common.DomainEvent
import java.time.LocalDateTime

data class HikerRegistered(
    val hikerId: HikerId,
    val email: String,
    override val occurredAt: LocalDateTime
) : DomainEvent

data class InvitationCodeRedeemed(
    val code: String,
    override val occurredAt: LocalDateTime
) : DomainEvent

data class HikerInformationUpdated(
    val hikerId: HikerId,
    override val occurredAt: LocalDateTime
) : DomainEvent

data class MembershipStatusChanged(
    val hikerId: HikerId,
    val previousStatus: MembershipStatus,
    val newStatus: MembershipStatus,
    override val occurredAt: LocalDateTime
) : DomainEvent

data class OrganizerCertificationRecorded(
    val hikerId: HikerId,
    val certificateNumber: String,
    override val occurredAt: LocalDateTime
) : DomainEvent
