package CAR_HMS.application.usecases.implementation

import CAR_HMS.application.dtos.queries.BrowseOpenHikesQuery
import CAR_HMS.application.dtos.queries.ListMatchingHikesQuery
import CAR_HMS.application.dtos.results.HikeSummary
import CAR_HMS.application.usecases.BrowseHikes
import CAR_HMS.domain.hikes.HikeDetails
import CAR_HMS.domain.hikes.HikesFacade
import CAR_HMS.domain.members.MembersFacade
import CAR_HMS.domain.trails.TrailDetails
import CAR_HMS.domain.trails.TrailsFacade
import org.springframework.stereotype.Service

@Service
class BrowseHikesImpl(
    private val access: AccessControl,
    private val hikes: HikesFacade,
    private val trails: TrailsFacade,
    private val members: MembersFacade
) : BrowseHikes {

    override fun browseOpenHikes(query: BrowseOpenHikesQuery): List<HikeSummary> =
        openHikesWithTrails()
            .filter { (hike, trail) ->
                (query.trailName == null || trail.name == query.trailName) &&
                    (query.date == null || hike.startDateTime.toLocalDate() == query.date) &&
                    (query.difficulty == null || trail.difficulty.label == query.difficulty)
            }
            .map { (hike, trail) -> summarize(hike, trail) }

    override fun listMatchingHikes(query: ListMatchingHikesQuery): List<HikeSummary> {
        access.requireSelf(query.hikerEmail)
        val skillRank = members.getProfile(query.hikerEmail).skillLevel.rank
        return openHikesWithTrails()
            .filter { (_, trail) -> hikes.matchesSkill(skillRank, trail.difficulty) }
            .map { (hike, trail) -> summarize(hike, trail) }
    }

    private fun openHikesWithTrails(): List<Pair<HikeDetails, TrailDetails>> =
        hikes.findOpenHikes().map { it to trails.getTrail(it.trailId) }

    private fun summarize(hike: HikeDetails, trail: TrailDetails) = HikeSummary(
        hike.id.value, hike.title, trail.name, trail.difficulty.label,
        hike.startDateTime, hike.status.label, hike.remainingSpots
    )
}
