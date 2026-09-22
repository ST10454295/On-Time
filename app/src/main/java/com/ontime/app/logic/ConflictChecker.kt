package com.ontime.app.logic

enum class Importance { HIGH, MEDIUM, LOW }

/** A time span in minutes since midnight, e.g. 18:00 -> 20:00 is TimeRange(1080, 1200). */
data class TimeRange(val startMinutes: Int, val endMinutes: Int) {
    init {
        require(startMinutes in 0..1440 && endMinutes in 0..1440) { "Time must be within a single day" }
        require(startMinutes < endMinutes) { "Start time must be before end time" }
    }
}

/**
 * Converts "HH:mm" (e.g. "18:30") into minutes since midnight.
 * Throws IllegalArgumentException on anything malformed, so callers can
 * show a friendly "invalid time" message instead of crashing.
 */
fun parseTimeToMinutes(hhmm: String): Int {
    val parts = hhmm.trim().split(":")
    require(parts.size == 2) { "Expected HH:mm, got \"$hhmm\"" }
    val hours = parts[0].toIntOrNull() ?: throw IllegalArgumentException("Invalid hour in \"$hhmm\"")
    val minutes = parts[1].toIntOrNull() ?: throw IllegalArgumentException("Invalid minute in \"$hhmm\"")
    require(hours in 0..23 && minutes in 0..59) { "Time out of range in \"$hhmm\"" }
    return hours * 60 + minutes
}

/**
 * The importance system described in the plan: when a new activity is
 * added, compare it against existing activities on the same day and
 * report the most severe conflict, if any.
 */
object ConflictChecker {

    private fun overlaps(a: TimeRange, b: TimeRange): Boolean =
        a.startMinutes < b.endMinutes && b.startMinutes < a.endMinutes

    private fun severity(importance: Importance): Int = when (importance) {
        Importance.HIGH -> 3
        Importance.MEDIUM -> 2
        Importance.LOW -> 1
    }

    /**
     * Returns the importance level of the most severe existing activity
     * that overlaps [newRange], or null if there's no conflict.
     */
    fun mostSevereConflict(
        existing: List<Pair<TimeRange, Importance>>,
        newRange: TimeRange
    ): Importance? {
        return existing
            .filter { (range, _) -> overlaps(range, newRange) }
            .maxByOrNull { (_, importance) -> severity(importance) }
            ?.second
    }
}
