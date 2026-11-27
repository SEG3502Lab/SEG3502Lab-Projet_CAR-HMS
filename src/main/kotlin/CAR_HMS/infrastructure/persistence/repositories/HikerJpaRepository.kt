package CAR_HMS.infrastructure.persistence.repositories

import CAR_HMS.infrastructure.persistence.entities.HikerEntity
import org.springframework.data.jpa.repository.JpaRepository

interface HikerJpaRepository :
    JpaRepository<HikerEntity, String> {

    fun findByEmail(email: String): HikerEntity?

    fun existsByCertificateNumber(
        certificateNumber: String
    ): Boolean
}