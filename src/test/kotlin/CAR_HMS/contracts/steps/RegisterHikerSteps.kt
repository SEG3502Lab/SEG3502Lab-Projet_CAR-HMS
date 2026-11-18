package CAR_HMS.contracts.steps

import CAR_HMS.application.dtos.queries.RegisterHikerQuery
import CAR_HMS.application.usecases.RegisterHiker
import CAR_HMS.contracts.stubs.StubInvitationCodeRepository
import CAR_HMS.contracts.stubs.StubUserAccountRepository
import CAR_HMS.contracts.support.ContractWorld
import CAR_HMS.contracts.support.asFields
import CAR_HMS.contracts.support.membershipStatus
import CAR_HMS.domain.members.InvitationCode
import CAR_HMS.domain.members.SkillLevel
import io.cucumber.datatable.DataTable
import io.cucumber.java.fr.Alors
import io.cucumber.java.fr.Etantdonné
import io.cucumber.java.fr.Quand
import java.time.LocalDate
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue

class RegisterHikerSteps(
    private val world: ContractWorld,
    private val registerHiker: RegisterHiker,
    private val invitationCodes: StubInvitationCodeRepository,
    private val accounts: StubUserAccountRepository
) {
    @Etantdonné("le code d'invitation {string} existe et n'est pas utilisé")
    fun unusedCode(code: String) = invitationCodes.save(InvitationCode(code))

    @Etantdonné("le code d'invitation {string} existe et est déjà utilisé")
    fun usedCode(code: String) = invitationCodes.save(InvitationCode(code, used = true))

    @Etantdonné("un randonneur avec le courriel {string} existe déjà")
    fun existingHiker(email: String) {
        world.ensureHiker(email)
    }

    private fun register(
        code: String,
        email: String,
        born: String = "1994-08-21",
        waiver: Boolean = true,
        guardian: String? = null
    ) = submit(
        RegisterHikerQuery(
            code, email, "Sentier!2027", "Prénom", "Nom", "819-555-0147", "45, rue Laurier", "Gatineau", "QC",
            "J8X 3V7", LocalDate.parse(born), "Contact", "819-555-0182", "intermédiaire", waiver, guardian
        )
    )

    private fun submit(query: RegisterHikerQuery) = world.act {
        world.subjectEmail = query.email
        world.returnedId = registerHiker.registerHiker(query)
    }

    @Quand("une personne s'inscrit avec les informations suivantes :")
    fun registerFromTable(table: DataTable) {
        val fields = table.asFields()
        fun field(name: String) = fields.getValue(name)!!
        submit(
            RegisterHikerQuery(
                field("invitationCode"), field("email"), field("password"), field("firstName"), field("lastName"),
                field("phoneNumber"), field("streetAddress"), field("city"), field("province"), field("postalCode"),
                LocalDate.parse(field("dateOfBirth")), field("emergencyContactName"), field("emergencyContactPhone"),
                field("skillLevel"), field("waiverAccepted").toBoolean(), fields["guardianName"]
            )
        )
    }

    @Quand("une personne née le {word} s'inscrit avec le code {string}, le courriel {string}, la décharge acceptée et le tuteur légal {string}")
    fun registerMinor(born: String, code: String, email: String, guardian: String) =
        register(code, email, born, guardian = guardian)

    @Quand("une personne née le {word} s'inscrit avec le code {string}, le courriel {string}, la décharge acceptée et sans tuteur légal")
    fun registerMinorWithoutGuardian(born: String, code: String, email: String) = register(code, email, born)

    @Quand("une personne s'inscrit avec le code {string} et le courriel {string}")
    fun registerWithCode(code: String, email: String) = register(code, email)

    @Quand("une personne s'inscrit avec le code {string}, le courriel {string} et la décharge refusée")
    fun registerWithoutWaiver(code: String, email: String) = register(code, email, waiver = false)

    @Alors("un identifiant de randonneur est retourné")
    fun idReturned() {
        assertNotNull(world.returnedId)
        assertNotNull(accounts.findByEmail(world.subjectEmail!!), "Compte utilisateur non créé")
    }

    @Alors("le randonneur {string} existe avec le niveau {string}")
    fun hikerHasLevel(email: String, level: String) {
        assertEquals(SkillLevel.fromLabel(level), world.hiker(email).skillLevel)
    }

    @Alors("le randonneur {string} existe avec le tuteur légal {string}")
    fun hikerHasGuardian(email: String, guardian: String) {
        assertEquals(guardian, world.hiker(email).guardianName)
    }

    @Alors("son adhésion a le statut {string}")
    fun membershipIs(status: String) {
        assertEquals(membershipStatus(status), world.hiker(world.subjectEmail!!).membershipStatus)
    }

    @Alors("le code d'invitation {string} est marqué comme utilisé")
    fun codeUsed(code: String) {
        assertTrue(invitationCodes.findByCode(code)!!.used)
    }

    @Alors("le code d'invitation {string} n'est pas utilisé")
    fun codeNotUsed(code: String) {
        assertFalse(invitationCodes.findByCode(code)!!.used)
    }

    @Alors("aucun randonneur {string} n'existe")
    fun noHiker(email: String) {
        assertNull(world.hikers.findByEmail(email))
    }
}
