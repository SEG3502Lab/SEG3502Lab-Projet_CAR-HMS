package CAR_HMS.infrastructure.persistence.repositories

import CAR_HMS.domain.hikes.HikeStatus
import CAR_HMS.infrastructure.persistence.entities.HikeEntity
import jakarta.persistence.LockModeType
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Lock
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param

interface HikeJpaRepository :
    JpaRepository<HikeEntity, String> {

    fun findByStatus(
        status: HikeStatus
    ): List<HikeEntity>

    fun findBySignUps_Id(
        signUpId: String
    ): HikeEntity?

    fun findByTrailId(
        trailId: String
    ): List<HikeEntity>

    fun findByOrganizerId(
        organizerId: String
    ): List<HikeEntity>

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query(
        "SELECT h FROM HikeEntity h WHERE h.id = :id"
    )
    fun findByIdForUpdate(
        @Param("id") id: String
    ): HikeEntity?

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query(
        """
        SELECT h FROM HikeEntity h
        WHERE EXISTS (
            SELECT s.id FROM HikeEntity h2
            JOIN h2.signUps s
            WHERE h2.id = h.id
            AND s.id = :signUpId
        )
        """
    )
    fun findBySignUpIdForUpdate(
        @Param("signUpId") signUpId: String
    ): HikeEntity?
}