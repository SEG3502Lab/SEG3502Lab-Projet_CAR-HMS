package CAR_HMS.infrastructure.persistence.mappers

import CAR_HMS.domain.members.InvitationCode
import CAR_HMS.infrastructure.persistence.entities.InvitationCodeEntity

object InvitationCodeMapper {

    fun toDomain(entity: InvitationCodeEntity): InvitationCode =
        InvitationCode(
            entity.code,
            entity.used
        )

    fun toEntity(code: InvitationCode): InvitationCodeEntity =
        InvitationCodeEntity(
            code.code,
            code.used
        )
}