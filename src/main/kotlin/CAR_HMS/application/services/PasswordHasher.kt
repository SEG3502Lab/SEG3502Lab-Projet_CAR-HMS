package CAR_HMS.application.services

interface PasswordHasher {
    fun hash(rawPassword: String): String
}
