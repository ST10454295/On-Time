package com.ontime.app.logic

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertThrows
import org.junit.Test

class ConflictCheckerTest {

    @Test
    fun `no existing activities means no conflict`() {
        val result = ConflictChecker.mostSevereConflict(
            existing = emptyList(),
            newRange = TimeRange(600, 660)
        )
        assertNull(result)
    }

    @Test
    fun `non-overlapping activity does not conflict`() {
        val existing = listOf(TimeRange(540, 600) to Importance.HIGH) // 09:00-10:00
        val newRange = TimeRange(600, 660) // 10:00-11:00, starts exactly when the other ends

        val result = ConflictChecker.mostSevereConflict(existing, newRange)

        assertNull(result)
    }

    @Test
    fun `overlapping activity reports its importance`() {
        // Database Assignment 18:00-20:00, HIGH - straight from the planning doc's example
        val existing = listOf(TimeRange(1080, 1200) to Importance.HIGH)
        val newRange = TimeRange(1080, 1200) // Movie with friends, same slot

        val result = ConflictChecker.mostSevereConflict(existing, newRange)

        assertEquals(Importance.HIGH, result)
    }

    @Test
    fun `partial overlap still counts as a conflict`() {
        val existing = listOf(TimeRange(540, 600) to Importance.MEDIUM) // 09:00-10:00
        val newRange = TimeRange(570, 630) // 09:30-10:30 overlaps the last 30 minutes

        val result = ConflictChecker.mostSevereConflict(existing, newRange)

        assertEquals(Importance.MEDIUM, result)
    }

    @Test
    fun `when multiple activities conflict, the most severe importance wins`() {
        val existing = listOf(
            TimeRange(540, 660) to Importance.LOW,     // 09:00-11:00
            TimeRange(600, 630) to Importance.HIGH,    // 10:00-10:30, nested inside above
            TimeRange(615, 645) to Importance.MEDIUM   // 10:15-10:45
        )
        val newRange = TimeRange(600, 660) // 10:00-11:00, overlaps all three

        val result = ConflictChecker.mostSevereConflict(existing, newRange)

        assertEquals(Importance.HIGH, result)
    }

    @Test
    fun `parseTimeToMinutes converts HH mm correctly`() {
        assertEquals(0, parseTimeToMinutes("00:00"))
        assertEquals(1080, parseTimeToMinutes("18:00"))
        assertEquals(1439, parseTimeToMinutes("23:59"))
    }

    @Test
    fun `parseTimeToMinutes rejects malformed input instead of crashing the app`() {
        assertThrows(IllegalArgumentException::class.java) { parseTimeToMinutes("not a time") }
        assertThrows(IllegalArgumentException::class.java) { parseTimeToMinutes("25:00") }
        assertThrows(IllegalArgumentException::class.java) { parseTimeToMinutes("10:75") }
    }

    @Test
    fun `TimeRange rejects an end time before the start time`() {
        assertThrows(IllegalArgumentException::class.java) { TimeRange(600, 500) }
    }
}
