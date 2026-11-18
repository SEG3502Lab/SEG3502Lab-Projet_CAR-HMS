package CAR_HMS.domain.members

interface HikerRepository {
    fun findById(id: HikerId): Hiker?
    fun findByEmail(email: String): Hiker?
    fun existsByCertificateNumber(certificateNumber: String): Boolean
    fun save(hiker: Hiker)
}

interface InvitationCodeRepository {
    fun findByCode(code: String): InvitationCode?
    fun save(invitationCode: InvitationCode)
}
