package CAR_HMS.infrastructure.persistence.repositories

import CAR_HMS.domain.members.InvitationCode
import CAR_HMS.domain.members.InvitationCodeRepository
import CAR_HMS.infrastructure.persistence.mappers.InvitationCodeMapper
import org.springframework.context.annotation.Profile
import org.springframework.stereotype.Repository
import org.springframework.transaction.annotation.Transactional

@Repository
@Profile("!contracts")
class InvitationCodeRepositoryAdapter(
    private val repository: InvitationCodeJpaRepository
) : InvitationCodeRepository {

    @Transactional
    override fun findByCode(code: String): InvitationCode? =

        repository.findByCodeForUpdate(code)
            ?.let(InvitationCodeMapper::toDomain)

    @Transactional
    override fun save(invitationCode: InvitationCode) {

        repository.save(
            InvitationCodeMapper.toEntity(invitationCode)
        )
    }
}