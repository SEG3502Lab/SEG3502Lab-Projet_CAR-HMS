package CAR_HMS.domain.hikes

import CAR_HMS.domain.common.DomainException
import CAR_HMS.domain.hikes.HikeStatusChanged
import CAR_HMS.domain.members.HikerId
import CAR_HMS.domain.trails.TrailId
import org.junit.jupiter.api.Test
import java.time.Duration
import java.time.LocalDateTime
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertTrue

class HikeCompletionTest {
    private val start = LocalDateTime.parse("2027-06-12T09:00")
    private val end = start.plusHours(4)

    private fun hike(status: HikeStatus = HikeStatus.CLOSED): Hike = Hike(
        HikeId("H-TEST"), HikerId("R-TEST"), "Randonnée test", "Description",
        TrailId("CCN-TEST"),
        HikeSchedule(start, Duration.ofHours(4), start.minusDays(2)),
        Capacity(12), status
    )

    @Test
    fun `completed hike records system actor and domain event`() {
        val hike = hike()
        hike.complete("système", end)

        assertEquals(HikeStatus.COMPLETED, hike.status)
        assertEquals("système", hike.transitions.single().actor)
        assertEquals(end, hike.transitions.single().at)
        assertTrue(hike.pullDomainEvents().filterIsInstance<HikeStatusChanged>()
            .any { it.to == HikeStatus.COMPLETED && it.actor == "système" })
    }

    @Test
    fun `hike cannot complete before expected end`() {
        val hike = hike()
        assertFailsWith<DomainException> { hike.complete("système", end.minusMinutes(1)) }
        assertEquals(HikeStatus.CLOSED, hike.status)
    }

    @Test
    fun `cancelled hike cannot complete`() {
        val hike = hike(HikeStatus.CANCELLED)
        assertFailsWith<DomainException> { hike.complete("système", end) }
        assertEquals(HikeStatus.CANCELLED, hike.status)
    }

    @Test
    fun `completed hike cannot complete twice`() {
        val hike = hike()
        hike.complete("système", end)
        assertFailsWith<DomainException> { hike.complete("système", end.plusHours(1)) }
        assertEquals(1, hike.transitions.size)
    }
}
