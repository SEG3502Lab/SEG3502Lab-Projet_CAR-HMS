package CAR_HMS.infrastructure.persistence.repositories

import CAR_HMS.domain.members.Hiker
import CAR_HMS.domain.members.HikerId
import CAR_HMS.domain.members.HikerRepository
import CAR_HMS.infrastructure.persistence.mappers.HikerMapper
import org.springframework.context.annotation.Profile
import org.springframework.stereotype.Repository
import kotlin.jvm.optionals.getOrNull

@Repository
@Profile("!contracts")
class HikerRepositoryAdapter(
    private val repository: HikerJpaRepository
) : HikerRepository {

    override fun findById(id: HikerId): Hiker? =
        repository.findById(id.value)
            .getOrNull()
            ?.let(HikerMapper::toDomain)

    override fun findByEmail(email: String): Hiker? =
        repository.findByEmail(email)
            ?.let(HikerMapper::toDomain)

    override fun existsByCertificateNumber(
        certificateNumber: String
    ): Boolean =
        repository.existsByCertificateNumber(
            certificateNumber
        )

    override fun save(hiker: Hiker) {
        repository.save(
            HikerMapper.toEntity(hiker)
        )
    }
}