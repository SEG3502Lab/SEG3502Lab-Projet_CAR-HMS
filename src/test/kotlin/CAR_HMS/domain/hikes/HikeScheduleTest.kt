package CAR_HMS.domain.hikes

import CAR_HMS.domain.common.DomainException
import org.junit.jupiter.api.Test
import java.time.Duration
import java.time.LocalDateTime
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class HikeScheduleTest {

    private val start = LocalDateTime.parse("2027-06-12T09:00")
    private val deadline = start.minusDays(2)

    @Test
    fun `positive duration is accepted`() {
        val schedule = HikeSchedule(
            start,
            Duration.ofHours(4),
            deadline
        )

        assertEquals(Duration.ofHours(4), schedule.expectedDuration)
    }

    @Test
    fun `zero duration is rejected`() {
        assertFailsWith<DomainException> {
            HikeSchedule(start, Duration.ZERO, deadline)
        }
    }

    @Test
    fun `negative duration is rejected`() {
        assertFailsWith<DomainException> {
            HikeSchedule(start, Duration.ofHours(-2), deadline)
        }
    }

    @Test
    fun `deadline exactly fourteen days before is accepted`() {
        val schedule = HikeSchedule(
            start,
            Duration.ofHours(4),
            start.minusDays(14)
        )

        assertEquals(start.minusDays(14), schedule.registrationDeadline)
    }
}