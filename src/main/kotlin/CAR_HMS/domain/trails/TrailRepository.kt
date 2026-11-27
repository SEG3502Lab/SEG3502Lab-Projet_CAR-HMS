package CAR_HMS.domain.trails

interface TrailRepository {

    fun findById(id: TrailId): Trail?

    fun findAll(): List<Trail>
}