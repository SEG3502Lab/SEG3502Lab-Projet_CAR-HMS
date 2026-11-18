package CAR_HMS.application.services

interface CurrentUserService {
    /** Courriel de l'utilisateur connecté, ou null pour un visiteur. */
    fun currentUserEmail(): String?
}
