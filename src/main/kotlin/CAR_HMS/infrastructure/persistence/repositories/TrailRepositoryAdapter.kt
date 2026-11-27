package CAR_HMS.infrastructure.persistence.repositories

import CAR_HMS.domain.trails.Trail
import CAR_HMS.domain.trails.TrailId
import CAR_HMS.domain.trails.TrailRepository
import CAR_HMS.infrastructure.persistence.mappers.TrailMapper
import org.springframework.context.annotation.Profile
import org.springframework.stereotype.Repository
import kotlin.jvm.optionals.getOrNull

@Repository
@Profile("!contracts")
class TrailRepositoryAdapter(
    private val repository: TrailJpaRepository
) : TrailRepository {

    override fun findById(id: TrailId): Trail? =
        repository.findById(id.value)
            .getOrNull()
            ?.let(TrailMapper::toDomain)

    override fun findAll(): List<Trail> =
        repository.findAll()
            .map(TrailMapper::toDomain)

    fun save(trail: Trail) {
        repository.save(
            TrailMapper.toEntity(trail)
        )
    }

    fun findHikeableTrails(): List<Trail> =
        repository.findByHikingAllowedTrue()
            .map(TrailMapper::toDomain)

    fun findByName(name: String): List<Trail> =
        repository.findByNameIgnoreCase(name)
            .map(TrailMapper::toDomain)
}