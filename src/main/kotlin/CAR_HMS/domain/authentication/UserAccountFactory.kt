package CAR_HMS.domain.authentication

interface UserAccountFactory {
    fun createAccount(email: String, passwordHash: String, role: Role): UserAccount
}
