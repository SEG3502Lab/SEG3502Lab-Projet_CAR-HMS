package CAR_HMS.domain.authentication

interface UserAccountRepository {
    fun findByEmail(email: String): UserAccount?
    fun save(account: UserAccount)
}
