package CAR_HMS.domain.trails

import org.junit.jupiter.api.Test
import java.time.LocalDate
import java.time.MonthDay
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class OpeningSeasonTest {

    @Test
    fun `summer season accepts summer dates`() {
        val season = OpeningSeason(
            MonthDay.of(5, 1),
            MonthDay.of(10, 31)
        )

        assertTrue(season.contains(LocalDate.of(2027, 7, 15)))
        assertFalse(season.contains(LocalDate.of(2027, 12, 15)))
    }

    @Test
    fun `winter season crosses calendar years`() {
        val season = OpeningSeason(
            MonthDay.of(12, 1),
            MonthDay.of(3, 31)
        )

        assertTrue(season.contains(LocalDate.of(2027, 12, 15)))
        assertTrue(season.contains(LocalDate.of(2028, 1, 15)))
        assertTrue(season.contains(LocalDate.of(2028, 3, 31)))

        assertFalse(season.contains(LocalDate.of(2028, 6, 15)))
    }

    @Test
    fun `year round season accepts all dates`() {
        val season = OpeningSeason(
            MonthDay.of(1, 1),
            MonthDay.of(12, 31)
        )

        assertTrue(season.contains(LocalDate.of(2027, 1, 1)))
        assertTrue(season.contains(LocalDate.of(2027, 7, 1)))
        assertTrue(season.contains(LocalDate.of(2027, 12, 31)))
    }
}