package CAR_HMS.domain.hikes

interface HikeRepository {
    fun findById(id: HikeId): Hike?
    fun findBySignUpId(id: SignUpId): Hike?
    fun findByStatus(status: HikeStatus): List<Hike>
    fun save(hike: Hike)
}
