package CAR_HMS.contracts.steps

import CAR_HMS.application.dtos.queries.RecordOrganizerCertificationQuery
import CAR_HMS.application.dtos.queries.UpdateHikerInformationQuery
import CAR_HMS.application.dtos.queries.UpdateMembershipStatusQuery
import CAR_HMS.application.usecases.UpdateHiker
import CAR_HMS.contracts.stubs.StubCurrentUserService
import CAR_HMS.contracts.stubs.StubUserAccountFactory
import CAR_HMS.contracts.stubs.StubUserAccountRepository
import CAR_HMS.contracts.support.ContractWorld
import CAR_HMS.contracts.support.asFields
import CAR_HMS.contracts.support.membershipStatus
import CAR_HMS.domain.authentication.Role
import CAR_HMS.domain.common.Difficulty
import CAR_HMS.domain.members.Address
import CAR_HMS.domain.members.CertificationPeriod
import CAR_HMS.domain.members.Hiker
import CAR_HMS.domain.members.HikerInformation
import CAR_HMS.domain.members.OrganizerCertification
import CAR_HMS.domain.members.SkillLevel
import io.cucumber.datatable.DataTable
import io.cucumber.java.fr.Alors
import io.cucumber.java.fr.Etantdonné
import io.cucumber.java.fr.Quand
import java.time.LocalDate
import java.time.LocalDateTime
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class UpdateHikerSteps(
    private val world: ContractWorld,
    private val updateHiker: UpdateHiker,
    private val currentUser: StubCurrentUserService,
    private val accounts: StubUserAccountRepository,
    private val accountFactory: StubUserAccountFactory
) {
    private val fixtureTime = LocalDateTime.parse("2027-01-01T00:00")

    @Etantdonné("le randonneur {string} existe avec le téléphone {string}, l'adresse {string} et le niveau {string}")
    fun hikerWithProfile(email: String, phone: String, address: String, level: String) {
        val hiker = world.ensureHiker(email)
        val (street, number, city, province, postalCode) = address.split(", ")
        hiker.updateInformation(
            HikerInformation(
                hiker.name, phone, Address("$street, $number", city, province, postalCode),
                hiker.emergencyContact, SkillLevel.fromLabel(level)
            ),
            fixtureTime
        )
        hiker.pullDomainEvents()
    }

    @Etantdonné("le randonneur {string} existe")
    fun hikerExists(email: String) {
        world.ensureHiker(email)
    }

    @Etantdonné("le randonneur {string} existe sans certification")
    fun hikerWithoutCertification(email: String) {
        world.ensureHiker(email)
    }

    @Etantdonné("le randonneur {string} existe avec l'adhésion {string}")
    fun hikerWithMembership(email: String, status: String) {
        world.ensureHiker(email, membership = membershipStatus(status))
    }

    @Etantdonné("l'administratrice {string} est connectée")
    fun administratorLoggedIn(email: String) {
        accounts.save(accountFactory.createAccount(email, "hash", Role.ADMINISTRATOR))
        currentUser.email = email
    }

    @Etantdonné("le certificat {string} est déjà enregistré pour {string}")
    fun certificateAlreadyRecorded(number: String, email: String) {
        val hiker = world.ensureHiker(email)
        hiker.recordCertification(
            OrganizerCertification(
                number,
                CertificationPeriod(LocalDate.parse("2027-03-01"), LocalDate.parse("2028-02-29")),
                Difficulty.HARD
            ),
            fixtureTime
        )
        hiker.pullDomainEvents()
    }

    /** La même phrase décrit le contexte avant l'action et le résultat attendu après celle-ci. */
    @Etantdonné("l'adhésion de {string} a le statut {string}")
    fun membershipHasStatus(email: String, status: String) {
        val hiker = world.hiker(email)
        if (world.acted) {
            assertEquals(membershipStatus(status), hiker.membershipStatus)
        } else {
            hiker.changeMembershipStatus(membershipStatus(status), fixtureTime)
            hiker.pullDomainEvents()
        }
    }

    private fun queryFrom(email: String, hiker: Hiker, phone: String) = UpdateHikerInformationQuery(
        email, hiker.name.firstName, hiker.name.lastName, phone, hiker.address.street, hiker.address.city,
        hiker.address.province, hiker.address.postalCode, hiker.emergencyContact.name,
        hiker.emergencyContact.phoneNumber, hiker.skillLevel.label
    )

    @Quand("{string} modifie son profil avec :")
    fun updateOwnProfile(email: String, table: DataTable) {
        val fields = table.asFields()
        fun field(name: String) = fields.getValue(name)!!
        world.act {
            world.subjectEmail = email
            updateHiker.updateHikerInformation(
                UpdateHikerInformationQuery(
                    email, field("firstName"), field("lastName"), field("phoneNumber"), field("streetAddress"),
                    field("city"), field("province"), field("postalCode"), field("emergencyContactName"),
                    field("emergencyContactPhone"), field("skillLevel")
                )
            )
        }
    }

    @Quand("{string} modifie le profil de {string} avec le téléphone {string}")
    fun updateOtherProfile(requester: String, email: String, phone: String) = world.act {
        currentUser.email = requester
        updateHiker.updateHikerInformation(queryFrom(email, world.hiker(email), phone))
    }

    @Quand("un visiteur non connecté modifie le profil de {string} avec le téléphone {string}")
    fun visitorUpdatesProfile(email: String, phone: String) = world.act {
        currentUser.email = null
        updateHiker.updateHikerInformation(queryFrom(email, world.hiker(email), phone))
    }

    @Quand("{string} change l'adhésion de {string} à {string}")
    fun changeMembership(admin: String, email: String, status: String) = world.act {
        currentUser.email = admin
        updateHiker.updateMembershipStatus(UpdateMembershipStatusQuery(admin, email, status))
    }

    private fun record(admin: String, email: String, number: String, from: String, to: String, level: String) =
        world.act {
            currentUser.email = admin
            updateHiker.recordOrganizerCertification(
                RecordOrganizerCertificationQuery(
                    admin, email, number, LocalDate.parse(from), LocalDate.parse(to), level
                )
            )
        }

    @Quand("{string} enregistre pour {string} la certification suivante :")
    fun recordFromTable(admin: String, email: String, table: DataTable) {
        val fields = table.asFields()
        record(
            admin, email, fields.getValue("certificateNumber")!!, fields.getValue("issueDate")!!,
            fields.getValue("expiryDate")!!, fields.getValue("organizerLevel")!!
        )
    }

    @Quand("{string} enregistre pour {string} la certification {string} du {word} au {word} de niveau {string}")
    fun recordInline(admin: String, email: String, number: String, from: String, to: String, level: String) =
        record(admin, email, number, from, to, level)

    @Alors("le profil de {string} a le téléphone {string}")
    fun profilePhone(email: String, phone: String) {
        assertEquals(phone, world.hiker(email).phoneNumber)
    }

    @Alors("le profil de {string} a toujours le téléphone {string}")
    fun profileStillPhone(email: String, phone: String) = profilePhone(email, phone)

    @Alors("le profil de {string} a l'adresse {string}")
    fun profileAddress(email: String, address: String) {
        assertEquals(address, world.hiker(email).address.toString())
    }

    @Alors("le profil de {string} a le contact d'urgence {string} au {string}")
    fun profileEmergencyContact(email: String, name: String, phone: String) {
        val contact = world.hiker(email).emergencyContact
        assertEquals(name, contact.name)
        assertEquals(phone, contact.phoneNumber)
    }

    @Alors("le profil de {string} a le niveau {string}")
    fun profileLevel(email: String, level: String) {
        assertEquals(SkillLevel.fromLabel(level), world.hiker(email).skillLevel)
    }

    @Alors("le courriel du randonneur est toujours {string}")
    fun emailUnchanged(email: String) {
        assertEquals(email, world.hiker(world.subjectEmail!!).email)
    }

    @Alors("l'adhésion de {string} a toujours le statut {string}")
    fun membershipStillHasStatus(email: String, status: String) {
        assertEquals(membershipStatus(status), world.hiker(email).membershipStatus)
    }

    @Alors("{string} détient la certification {string} valide du {word} au {word}")
    fun holdsCertification(email: String, number: String, from: String, to: String) {
        val certification = world.hiker(email).certification!!
        assertEquals(number, certification.certificateNumber)
        assertEquals(LocalDate.parse(from), certification.period.issueDate)
        assertEquals(LocalDate.parse(to), certification.period.expiryDate)
    }

    @Alors("le niveau d'organisateur de {string} est {string}")
    fun organizerLevel(email: String, level: String) {
        assertEquals(Difficulty.fromLabel(level), world.hiker(email).certification!!.organizerLevel)
    }

    @Alors("{string} est un organisateur")
    fun isOrganizer(email: String) {
        assertTrue(world.hiker(email).isOrganizer)
    }

    @Alors("{string} n'est pas un organisateur")
    fun isNotOrganizer(email: String) {
        assertFalse(world.hiker(email).isOrganizer)
    }
}
