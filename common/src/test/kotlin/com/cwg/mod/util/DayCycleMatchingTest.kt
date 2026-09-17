package com.cwg.mod.util

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

/**
 * Regression coverage for the spawn-time-cycle matching logic and its backing [dayCycleMap].
 * A duplicate map key here previously made one cycle definition dead code silently - a plain
 * size/uniqueness check plus a couple of matching cases keeps that from creeping back in.
 */
class DayCycleMatchingTest {

    @Test
    fun `dayCycleMap has no duplicate or collapsed keys`() {
        assertEquals(10, dayCycleMap.size)
        assertEquals(setOf(
            "Any time", "Day", "Night", "Noon", "Midnight",
            "Dawn", "Dusk", "Twilight", "Morning", "Afternoon"
        ), dayCycleMap.keys)
    }

    @Test
    fun `matches Noon range exactly`() {
        val matches = matchingDayCycleKeys(listOf(5000..6999), dayCycleMap)
        assertEquals(listOf("Noon"), matches)
    }

    @Test
    fun `matches Dusk and Twilight for their shared range`() {
        // Twilight is defined as the union of Dusk's range and Dawn's; only the "..' side
        // (Dusk) can ever structurally equal a real condition's IntRange - see Dawn/Day/
        // Twilight's other half, which are defined with `downTo` and can never match a
        // vanilla `..` IntRange by equality. Documented here rather than silently assumed.
        val matches = matchingDayCycleKeys(listOf(11834..13701), dayCycleMap)
        assertTrue(matches.contains("Dusk"))
        assertTrue(matches.contains("Twilight"))
    }

    @Test
    fun `no match when time range is not one of the predefined cycles`() {
        val matches = matchingDayCycleKeys(listOf(1..2), dayCycleMap)
        assertEquals(emptyList<String>(), matches)
    }

    @Test
    fun `no time ranges yields no matches`() {
        val matches = matchingDayCycleKeys(emptyList(), dayCycleMap)
        assertEquals(emptyList<String>(), matches)
    }
}
