package CAR_HMS.infrastructure.persistence.repositories

import CAR_HMS.infrastructure.persistence.entities.TrailEntity
import org.springframework.data.jpa.repository.JpaRepository

interface TrailJpaRepository :
    JpaRepository<TrailEntity, String> {

    fun findByNameIgnoreCase(name: String): List<TrailEntity>

    fun findByHikingAllowedTrue(): List<TrailEntity>
}