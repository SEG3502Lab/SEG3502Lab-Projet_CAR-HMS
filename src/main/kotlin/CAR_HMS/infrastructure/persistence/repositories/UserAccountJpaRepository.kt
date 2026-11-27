package CAR_HMS.infrastructure.persistence.repositories

import CAR_HMS.infrastructure.persistence.entities.UserAccountEntity
import org.springframework.data.jpa.repository.JpaRepository

interface UserAccountJpaRepository :
    JpaRepository<UserAccountEntity, String> {

    fun findByEmail(email: String): UserAccountEntity?
}