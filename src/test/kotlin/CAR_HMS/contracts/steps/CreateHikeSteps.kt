package CAR_HMS.contracts.steps

import CAR_HMS.application.dtos.queries.CreateHikeQuery
import CAR_HMS.application.usecases.CreateHike
import CAR_HMS.contracts.stubs.StubCurrentUserService
import CAR_HMS.contracts.support.ContractWorld
import CAR_HMS.contracts.support.asFields
import CAR_HMS.contracts.support.hikeStatus
import CAR_HMS.domain.common.Difficulty
import CAR_HMS.domain.members.CertificationPeriod
import CAR_HMS.domain.members.OrganizerCertification
import CAR_HMS.domain.trails.OpeningSeason
import CAR_HMS.domain.trails.Trail
import CAR_HMS.domain.trails.TrailId
import io.cucumber.datatable.DataTable
import io.cucumber.java.fr.Alors
import io.cucumber.java.fr.Etantdonné
import io.cucumber.java.fr.Quand
import java.time.Duration
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.MonthDay
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

class CreateHikeSteps(
    private val world: ContractWorld,
    private val createHike: CreateHike,
    private val currentUser: StubCurrentUserService
) {
    private var certifiedHikerEmail: String? = null

    @Etantdonné("les sentiers CCN suivants :")
    fun trails(table: DataTable) = table.asMaps().forEach { row ->
        val (opens, closes) = row.getValue("saison d'ouverture").split(" à ").map(LocalDate::parse)
        world.trails.save(
            Trail(
                TrailId(row.getValue("trailId")),
                row.getValue("nom"),
                Difficulty.fromLabel(row.getValue("difficulté")),
                row.getValue("randonnée permise") == "oui",
                OpeningSeason(MonthDay.from(opens), MonthDay.from(closes)),
                "stationnement principal"
            )
        )
    }

    @Etantdonné("la randonneuse {string} est connectée")
    fun hikerLoggedIn(email: String) {
        world.ensureHiker(email)
        currentUser.email = email
        certifiedHikerEmail = email
    }

    @Etantdonné("elle détient la certification d'organisatrice {string} de niveau {string} valide du {word} au {word}")
    fun holdsCertification(number: String, level: String, from: String, to: String) {
        world.hiker(certifiedHikerEmail!!).recordCertification(
            OrganizerCertification(
                number,
                CertificationPeriod(LocalDate.parse(from), LocalDate.parse(to)),
                Difficulty.fromLabel(level)
            ),
            LocalDateTime.parse(from + "T00:00")
        )
        world.hiker(certifiedHikerEmail!!).pullDomainEvents()
    }

    @Etantdonné("le randonneur {string} est connecté et n'a aucune certification")
    fun uncertifiedHikerLoggedIn(email: String) {
        world.ensureHiker(email)
        currentUser.email = email
    }

    private fun create(
        email: String,
        trailId: String,
        start: String,
        deadline: String? = null,
        capacity: Int = 10,
        title: String = "Randonnée",
        description: String = "Description",
        duration: Duration = Duration.ofHours(4)
    ) = world.act {
        currentUser.email = email
        val startDateTime = LocalDateTime.parse(start)
        world.returnedId = createHike.createHike(
            CreateHikeQuery(
                email, title, description, trailId, startDateTime, duration,
                deadline?.let(LocalDateTime::parse) ?: startDateTime.minusDays(2), capacity
            )
        )
    }

    @Quand("{string} crée la randonnée suivante :")
    fun createFromTable(email: String, table: DataTable) {
        val fields = table.asFields()
        create(
            email,
            fields.getValue("trailId")!!,
            fields.getValue("startDateTime")!!,
            fields.getValue("registrationDeadline"),
            fields.getValue("capacity")!!.toInt(),
            fields.getValue("title")!!,
            fields.getValue("description")!!,
            Duration.parse(fields.getValue("expectedDuration"))
        )
    }

    @Quand("{string} crée une randonnée sur {string} le {word} avec une capacité de {int}")
    fun createWithCapacity(email: String, trailId: String, start: String, capacity: Int) =
        create(email, trailId, start, capacity = capacity)

    @Quand("{string} crée une randonnée sur {string} le {word} avec la date limite {word}")
    fun createWithDeadline(email: String, trailId: String, start: String, deadline: String) =
        create(email, trailId, start, deadline)

    @Alors("un identifiant unique de randonnée est retourné")
    fun idReturned() {
        assertNotNull(world.returnedId)
    }

    @Alors("la randonnée existe avec le statut {string}")
    fun hikeExists(status: String) {
        assertEquals(hikeStatus(status), world.hike(world.returnedId!!).status)
    }

    @Alors("le nombre de places restantes est {int}")
    fun remaining(remaining: Int) {
        assertEquals(remaining, world.hike(world.returnedId!!).remainingSpots)
    }

    @Alors("l'organisatrice de la randonnée est {string}")
    fun organizerIs(email: String) {
        assertEquals(world.hiker(email).id, world.hike(world.returnedId!!).organizerId)
    }

    @Alors("aucune randonnée n'est créée")
    fun noHikeCreated() {
        assertTrue(world.hikes.all().isEmpty())
    }
}
