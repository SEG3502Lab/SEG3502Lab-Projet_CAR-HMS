package CAR_HMS.contracts.steps

import CAR_HMS.application.dtos.queries.BrowseOpenHikesQuery
import CAR_HMS.application.dtos.queries.ListMatchingHikesQuery
import CAR_HMS.application.usecases.BrowseHikes
import CAR_HMS.contracts.stubs.StubCurrentUserService
import CAR_HMS.contracts.support.ContractWorld
import CAR_HMS.contracts.support.hikeStatus
import CAR_HMS.contracts.support.membershipStatus
import CAR_HMS.domain.common.Difficulty
import CAR_HMS.domain.members.MembershipStatus
import CAR_HMS.domain.members.SkillLevel
import io.cucumber.datatable.DataTable
import io.cucumber.java.fr.Alors
import io.cucumber.java.fr.Etantdonné
import io.cucumber.java.fr.Quand
import java.time.LocalDate
import java.time.LocalDateTime
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class BrowseHikesSteps(
    private val world: ContractWorld,
    private val browseHikes: BrowseHikes,
    private val currentUser: StubCurrentUserService
) {
    @Etantdonné("les randonnées suivantes :")
    fun hikes(table: DataTable) = table.asMaps().forEach { row -> describeHike(row, row.getValue("statut")) }

    @Etantdonné("les randonnées ouvertes suivantes :")
    fun openHikes(table: DataTable) = table.asMaps().forEach { row -> describeHike(row, "ouverte") }

    private fun describeHike(row: Map<String, String?>, status: String) {
        world.fixture(row.getValue("hikeId")!!).apply {
            title = row["titre"] ?: "Randonnée sur ${row["sentier"]}"
            trailName = row.getValue("sentier")!!
            difficulty = Difficulty.fromLabel(row.getValue("difficulté")!!)
            start = LocalDateTime.parse(row.getValue("départ"))
            this.status = hikeStatus(status)
        }
    }

    @Etantdonné("les randonneurs suivants :")
    fun hikers(table: DataTable) = table.asMaps().forEach { row ->
        world.ensureHiker(
            row.getValue("courriel"),
            SkillLevel.fromLabel(row.getValue("niveau")),
            row["adhésion"]?.let { membershipStatus(it) } ?: MembershipStatus.ACTIVE
        )
    }

    private fun browse(trail: String? = null, date: String? = null, difficulty: String? = null) = world.act {
        world.results = browseHikes.browseOpenHikes(
            BrowseOpenHikesQuery(trail, date?.let(LocalDate::parse), difficulty)
        )
    }

    @Quand("on consulte les randonnées ouvertes sans filtre")
    fun browseAll() = browse()

    @Quand("on consulte les randonnées ouvertes pour le sentier {string}")
    fun browseByTrail(trail: String) = browse(trail = trail)

    @Quand("on consulte les randonnées ouvertes pour la date {word}")
    fun browseByDate(date: String) = browse(date = date)

    @Quand("on consulte les randonnées ouvertes de difficulté {string}")
    fun browseByDifficulty(difficulty: String) = browse(difficulty = difficulty)

    @Quand("on consulte les randonnées ouvertes pour le sentier {string} et la date {word}")
    fun browseByTrailAndDate(trail: String, date: String) = browse(trail = trail, date = date)

    @Quand("{string} demande les randonnées qui lui correspondent")
    fun listMatching(email: String) = world.act {
        world.results = browseHikes.listMatchingHikes(ListMatchingHikesQuery(email))
    }

    @Quand("un visiteur demande les randonnées correspondant à {string}")
    fun visitorListsMatching(email: String) {
        currentUser.email = null
        listMatching(email)
    }

    @Alors("les randonnées retournées sont, dans l'ordre : {}")
    fun returnedInOrder(ids: String) {
        assertEquals(ids.split(",").map { it.trim() }, world.results.map { it.hikeId })
    }

    @Alors("aucune randonnée n'est retournée")
    fun noneReturned() {
        assertTrue(world.acted && world.refusal == null && world.results.isEmpty())
    }

    @Alors("chaque résultat indique le titre, le sentier, la difficulté, le départ et les places restantes")
    fun resultsAreComplete() {
        assertTrue(world.results.isNotEmpty())
        world.results.forEach { summary ->
            val hike = world.hike(summary.hikeId)
            assertEquals(hike.title, summary.title)
            assertEquals(world.fixture(summary.hikeId).trailName, summary.trailName)
            assertEquals(world.fixture(summary.hikeId).difficulty.label, summary.difficulty)
            assertEquals(hike.schedule.startDateTime, summary.startDateTime)
            assertEquals(hike.remainingSpots, summary.remainingSpots)
        }
    }
}
