package CAR_HMS.contracts.steps

import CAR_HMS.contracts.stubs.StubDomainEventEmitter
import CAR_HMS.contracts.support.ContractWorld
import CAR_HMS.contracts.support.HikeFixture
import CAR_HMS.contracts.support.SignUpFixture
import CAR_HMS.contracts.support.hikeStatus
import CAR_HMS.contracts.support.signUpStatus
import CAR_HMS.domain.common.Difficulty
import CAR_HMS.domain.hikes.HikeStatus
import CAR_HMS.domain.hikes.HikeStatusChanged
import CAR_HMS.domain.hikes.SignUpStatus
import io.cucumber.datatable.DataTable
import io.cucumber.java.fr.Alors
import io.cucumber.java.fr.Etantdonné
import java.time.LocalDateTime
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue

class HikeFixtureSteps(
    private val world: ContractWorld,
    private val events: StubDomainEventEmitter
) {
    private fun describe(id: String, trail: String, start: String): HikeFixture =
        world.fixture(id).apply {
            trailName = trail
            this.start = LocalDateTime.parse(start)
        }

    @Etantdonné("la randonnée {string} sur le {string} le {word}, organisée par {string}, avec le statut {string}")
    fun hikeWithOrganizer(id: String, trail: String, start: String, organizer: String, status: String) {
        describe(id, trail, start).apply {
            organizerEmail = organizer
            this.status = hikeStatus(status)
        }
    }

    @Etantdonné("la randonnée {string} sur le {string} le {word} avec la date limite {word}, de capacité {int}")
    fun hikeWithCapacity(id: String, trail: String, start: String, deadline: String, capacity: Int) {
        describe(id, trail, start).apply {
            this.deadline = LocalDateTime.parse(deadline)
            this.capacity = capacity
        }
    }

    @Etantdonné("la randonnée {string} sur le {string} le {word} avec la date limite {word} et le statut {string}")
    fun hikeWithDeadline(id: String, trail: String, start: String, deadline: String, status: String) {
        describe(id, trail, start).apply {
            this.deadline = LocalDateTime.parse(deadline)
            this.status = hikeStatus(status)
        }
    }

    @Etantdonné("la randonnée {string} sur le {string} le {word}, point de départ {string}, avec le statut {string}")
    fun hikeWithTrailhead(id: String, trail: String, start: String, trailhead: String, status: String) {
        describe(id, trail, start).apply {
            this.trailhead = trailhead
            this.status = hikeStatus(status)
        }
    }

    @Etantdonné("la randonnée {string} sur le {string} \\({}) le {word}, de capacité {int}, avec le statut {string} et {int} places restantes")
    fun hikeWithRemainingSpots(
        id: String, trail: String, difficulty: String, start: String, capacity: Int, status: String, remaining: Int
    ) {
        describe(id, trail, start).apply {
            this.difficulty = Difficulty.fromLabel(difficulty)
            this.capacity = capacity
            this.status = hikeStatus(status)
            this.remaining = remaining
        }
    }

    @Etantdonné("les inscriptions suivantes à {string} :")
    fun signUps(id: String, table: DataTable) {
        table.asMaps().forEach { row ->
            world.fixture(id).signUps += SignUpFixture(
                row.getValue("signUpId"),
                row.getValue("randonneur"),
                signUpStatus(row.getValue("statut")),
                row["inscrit le"]?.let(LocalDateTime::parse)
            )
        }
    }

    /** La même phrase décrit le contexte avant l'action et le résultat attendu après celle-ci. */
    @Etantdonné("la randonnée {string} a le statut {string}")
    fun hikeHasStatus(id: String, status: String) {
        if (world.acted) assertEquals(hikeStatus(status), world.hike(id).status)
        else world.fixture(id).status = hikeStatus(status)
    }

    @Etantdonné("la randonnée {string} a le statut {string} et {int} randonneurs en liste d'attente")
    fun hikeFullWithWaitlist(id: String, status: String, waitlisted: Int) {
        world.fixture(id).apply {
            this.status = hikeStatus(status)
            extraWaitlisted = waitlisted
        }
    }

    @Etantdonné("la randonnée {string} a {int} place restante")
    fun hikeHasRemaining(id: String, remaining: Int) {
        world.fixture(id).remaining = remaining
    }

    @Etantdonné("{string} a l'inscription {string} au statut {string}")
    fun hasSignUp(email: String, signUpId: String, status: String) {
        singleFixture().signUps += SignUpFixture(signUpId, email, signUpStatus(status))
    }

    @Etantdonné("{string} a l'inscription {string} en liste d'attente à la position {int}")
    fun hasWaitlistedSignUp(email: String, signUpId: String, position: Int) {
        singleFixture().signUps += SignUpFixture(signUpId, email, SignUpStatus.WAITLISTED, position = position)
    }

    @Etantdonné("la liste d'attente de {string} est vide")
    fun waitlistEmpty(id: String) {
        assertTrue(world.fixture(id).signUps.none { it.status == SignUpStatus.WAITLISTED })
    }

    @Etantdonné("{string} a déjà une inscription {string} à {string}")
    fun alreadySignedUp(email: String, status: String, id: String) {
        world.fixture(id).signUps += SignUpFixture("S-EXISTANTE", email, signUpStatus(status))
    }

    @Alors("la randonnée {string} a toujours le statut {string}")
    fun hikeStillHasStatus(id: String, status: String) {
        assertEquals(hikeStatus(status), world.hike(id).status)
    }

    @Alors("le nombre de places restantes de {string} est {int}")
    fun remainingSpots(id: String, remaining: Int) {
        assertEquals(remaining, world.hike(id).remainingSpots)
    }

    @Alors("la transition vers {string} est enregistrée avec un horodatage et l'acteur {string}")
    fun transitionRecorded(status: String, actor: String) {
        val expected: HikeStatus = hikeStatus(status)
        val hike = world.hikes.all().single { candidate -> candidate.transitions.any { it.to == expected } }
        val transition = hike.transitions.last { it.to == expected }
        assertEquals(actor, transition.actor)
        assertNotNull(transition.at)
        assertTrue(
            events.emitted.filterIsInstance<HikeStatusChanged>().any { it.to == expected && it.actor == actor },
            "Événement HikeStatusChanged non émis"
        )
    }

    @Alors("l'inscription {word} a le statut {string}")
    fun signUpHasStatus(id: String, status: String) {
        assertEquals(signUpStatus(status), world.signUp(id).status)
    }

    @Alors("l'inscription {word} a toujours le statut {string}")
    fun signUpStillHasStatus(id: String, status: String) = signUpHasStatus(id, status)

    @Alors("l'inscription {word} reste {string}")
    fun signUpRemains(id: String, status: String) = signUpHasStatus(id, status)

    @Alors("^les inscriptions (S-.+) ont le statut \"([^\"]*)\"$")
    fun signUpsHaveStatus(ids: String, status: String) {
        Regex("S-\\d+").findAll(ids).map { it.value }.toList().also { assertTrue(it.isNotEmpty()) }
            .forEach { signUpHasStatus(it, status) }
    }

    @Alors("toutes les inscriptions actives de {string} ont le statut {string}")
    fun allSignUpsHaveStatus(id: String, status: String) {
        assertTrue(world.hike(id).signUps.all { it.status == signUpStatus(status) })
    }

    @Alors("l'inscription {word} est en liste d'attente à la position {int}")
    fun signUpAtPosition(id: String, position: Int) {
        val signUp = world.signUp(id)
        assertEquals(SignUpStatus.WAITLISTED, signUp.status)
        assertEquals(position, signUp.waitlistPosition)
        assertNull(world.refusal)
    }

    private fun singleFixture(): HikeFixture = world.fixtures.values.single()
}
