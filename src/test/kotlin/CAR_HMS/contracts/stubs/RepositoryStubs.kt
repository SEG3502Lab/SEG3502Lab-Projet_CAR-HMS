package CAR_HMS.contracts.stubs

import CAR_HMS.domain.authentication.UserAccount
import CAR_HMS.domain.authentication.UserAccountRepository
import CAR_HMS.domain.hikes.Hike
import CAR_HMS.domain.hikes.HikeId
import CAR_HMS.domain.hikes.HikeRepository
import CAR_HMS.domain.hikes.HikeStatus
import CAR_HMS.domain.hikes.SignUpId
import CAR_HMS.domain.members.Hiker
import CAR_HMS.domain.members.HikerId
import CAR_HMS.domain.members.HikerRepository
import CAR_HMS.domain.members.InvitationCode
import CAR_HMS.domain.members.InvitationCodeRepository
import CAR_HMS.domain.trails.Trail
import CAR_HMS.domain.trails.TrailId
import CAR_HMS.domain.trails.TrailRepository
import org.springframework.stereotype.Component

@Component
class StubHikeRepository : HikeRepository, Resettable {
    private val hikes = linkedMapOf<HikeId, Hike>()

    override fun findById(id: HikeId): Hike? = hikes[id]
    override fun findBySignUpId(id: SignUpId): Hike? = hikes.values.firstOrNull { hike -> hike.signUps.any { it.id == id } }
    override fun findByStatus(status: HikeStatus): List<Hike> = hikes.values.filter { it.status == status }
    override fun save(hike: Hike) {
        hikes[hike.id] = hike
    }

    fun all(): List<Hike> = hikes.values.toList()
    override fun reset() = hikes.clear()
}

@Component
class StubHikerRepository : HikerRepository, Resettable {
    private val hikers = linkedMapOf<HikerId, Hiker>()

    override fun findById(id: HikerId): Hiker? = hikers[id]
    override fun findByEmail(email: String): Hiker? = hikers.values.firstOrNull { it.email == email }
    override fun existsByCertificateNumber(certificateNumber: String): Boolean =
        hikers.values.any { it.certification?.certificateNumber == certificateNumber }
    override fun save(hiker: Hiker) {
        hikers[hiker.id] = hiker
    }

    override fun reset() = hikers.clear()
}

@Component
class StubInvitationCodeRepository : InvitationCodeRepository, Resettable {
    private val codes = linkedMapOf<String, InvitationCode>()

    override fun findByCode(code: String): InvitationCode? = codes[code]
    override fun save(invitationCode: InvitationCode) {
        codes[invitationCode.code] = invitationCode
    }

    override fun reset() = codes.clear()
}

@Component
class StubTrailRepository : TrailRepository, Resettable {
    private val trails = linkedMapOf<TrailId, Trail>()

    override fun findById(id: TrailId): Trail? = trails[id]
    fun findByName(name: String): Trail? = trails.values.firstOrNull { it.name == name }
    fun save(trail: Trail) {
        trails[trail.id] = trail
    }

    override fun reset() = trails.clear()
}

@Component
class StubUserAccountRepository : UserAccountRepository, Resettable {
    private val accounts = linkedMapOf<String, UserAccount>()

    override fun findByEmail(email: String): UserAccount? = accounts[email]
    override fun save(account: UserAccount) {
        accounts[account.email] = account
    }

    override fun reset() = accounts.clear()
}
