package CAR_HMS.infrastructure.persistence.repositories

import CAR_HMS.domain.hikes.*
import CAR_HMS.infrastructure.persistence.mappers.HikeMapper
import org.springframework.context.annotation.Profile
import org.springframework.stereotype.Repository
import org.springframework.transaction.annotation.Transactional

@Repository
@Profile("!contracts")
@Transactional
class HikeRepositoryAdapter(
    private val repository: HikeJpaRepository
) : HikeRepository {

    override fun findById(id: HikeId): Hike? =

        repository.findByIdForUpdate(id.value)
            ?.let(HikeMapper::toDomain)

    override fun findBySignUpId(id: SignUpId): Hike? =

        repository.findBySignUpIdForUpdate(id.value)
            ?.let(HikeMapper::toDomain)

    @Transactional(readOnly = true)
    override fun findByStatus(
        status: HikeStatus
    ): List<Hike> =

        repository.findByStatus(status)
            .map(HikeMapper::toDomain)

    override fun save(hike: Hike) {

        repository.save(
            HikeMapper.toEntity(hike)
        )
    }

    @Transactional(readOnly = true)
    fun findAll(): List<Hike> =

        repository.findAll()
            .map(HikeMapper::toDomain)

    @Transactional(readOnly = true)
    fun findByTrailId(
        trailId: String
    ): List<Hike> =

        repository.findByTrailId(trailId)
            .map(HikeMapper::toDomain)

    @Transactional(readOnly = true)
    fun findByOrganizerId(
        organizerId: String
    ): List<Hike> =

        repository.findByOrganizerId(organizerId)
            .map(HikeMapper::toDomain)
}