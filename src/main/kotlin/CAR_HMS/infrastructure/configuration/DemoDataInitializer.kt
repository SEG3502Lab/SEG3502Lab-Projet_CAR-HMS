package CAR_HMS.infrastructure.configuration

import CAR_HMS.domain.authentication.*
import CAR_HMS.domain.common.Difficulty
import CAR_HMS.domain.members.*
import org.springframework.boot.ApplicationArguments
import org.springframework.boot.ApplicationRunner
import org.springframework.context.annotation.Profile
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDate

@Component
@Profile("demo & !contracts")
class DemoDataInitializer(
    private val accounts: UserAccountRepository,
    private val hikers: HikerRepository,
    private val invitations: InvitationCodeRepository,
    private val accountFactory: UserAccountFactory,
    private val hikerFactory: HikerFactory,
    private val passwordEncoder: PasswordEncoder
) : ApplicationRunner {

    @Transactional
    override fun run(args: ApplicationArguments) {

        createAccount(
            "admin@carhms.ca",
            "Admin123!",
            Role.ADMINISTRATOR
        )

        createHiker(
            email = "organizer@carhms.ca",
            password = "Organizer123!",
            firstName = "Alex",
            lastName = "Organizer",
            organizer = true
        )

        createHiker(
            email = "hiker@carhms.ca",
            password = "Hiker123!",
            firstName = "Charlie",
            lastName = "Hiker",
            organizer = false
        )

        listOf(
            "CAIRN-001",
            "CAIRN-002",
            "CAIRN-003",
            "CAIRN-004",
            "CAIRN-005"
        ).forEach { code ->

            if (invitations.findByCode(code) == null) {
                invitations.save(
                    InvitationCode(code)
                )
            }
        }
    }

    private fun createAccount(
        email: String,
        password: String,
        role: Role
    ) {
        if (accounts.findByEmail(email) != null) {
            return
        }

        val account = accountFactory.createAccount(
            email,
            requireNotNull(
                passwordEncoder.encode(password)
            ),
            role
        )

        accounts.save(account)
    }

    private fun createHiker(
        email: String,
        password: String,
        firstName: String,
        lastName: String,
        organizer: Boolean
    ) {
        if (hikers.findByEmail(email) == null) {

            val hiker = hikerFactory.createHiker(
                HikerRegistration(
                    email = email,

                    name = PersonName(
                        firstName,
                        lastName
                    ),

                    phoneNumber = "613-555-0100",

                    address = Address(
                        "1 Test Street",
                        "Ottawa",
                        "ON",
                        "K1A 0A1"
                    ),

                    dateOfBirth =
                        LocalDate.of(1995, 1, 1),

                    emergencyContact =
                        EmergencyContact(
                            "Test Contact",
                            "613-555-0101"
                        ),

                    skillLevel = if (organizer) {
                        SkillLevel.EXPERT
                    } else {
                        SkillLevel.INTERMEDIATE
                    },

                    waiverAccepted = true,
                    guardianName = null
                )
            )

            if (organizer) {
                hiker.recordCertification(
                    OrganizerCertification(
                        "CERT-DEMO-001",
                        CertificationPeriod(
                            LocalDate.of(2025, 1, 1),
                            LocalDate.of(2030, 12, 31)
                        ),
                        Difficulty.VERY_HARD
                    ),
                    LocalDate.now().atStartOfDay()
                )
            }

            hikers.save(hiker)
        }

        createAccount(
            email,
            password,
            Role.HIKER
        )
    }
}