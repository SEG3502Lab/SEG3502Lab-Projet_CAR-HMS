package CAR_HMS.infrastructure.persistence.repositories

import CAR_HMS.infrastructure.persistence.entities.InvitationCodeEntity
import jakarta.persistence.LockModeType
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Lock
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param

interface InvitationCodeJpaRepository :
    JpaRepository<InvitationCodeEntity, String> {

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query(
        "SELECT c FROM InvitationCodeEntity c WHERE c.code = :code"
    )
    fun findByCodeForUpdate(
        @Param("code") code: String
    ): InvitationCodeEntity?
}