package com.cwg.mod.util

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

/**
 * Regression coverage for [TypeChart.combinedMultiplierByName]. Before this, weak/resist and
 * immune were computed independently for dual-type Pokémon and could both "win" for the same
 * attacking type (e.g. Electric/Flying showed Ground as both weak against and immune against,
 * since Flying's immunity to Ground was lost when summed with Electric's weakness to Ground).
 * The multiplier must be a single combined value per attacking type, with 0x always dominating.
 *
 * Uses raw type name strings (like [com.cwg.mod.gui.WikiGuiTitle]) instead of Cobblemon's
 * `ElementalType`, since that class isn't loadable in a plain JVM unit test.
 */
class TypeChartTest {

    @Test
    fun `immunity from one type dominates a weakness from the other type`() {
        // Electric-Flying: Electric is weak to Ground (2x), Flying is immune to Ground (0x).
        // Combined must be 0x (immune), not 2x (weak).
        val multiplier = TypeChart.combinedMultiplierByName("Ground", listOf("Electric", "Flying"))
        assertEquals(0.0, multiplier)
    }

    @Test
    fun `immunity from one type dominates a resistance from the other type`() {
        // Ghost-Dark: Dark resists Ghost (0.5x), Ghost is immune to... use Normal attacking Ghost-Normal instead.
        // Ghost-Normal vs Normal: Ghost is immune to Normal (0x), Normal takes neutral from Normal (1x).
        val multiplier = TypeChart.combinedMultiplierByName("Normal", listOf("Ghost", "Normal"))
        assertEquals(0.0, multiplier)
    }

    @Test
    fun `weaknesses from both types stack multiplicatively`() {
        // Bug-Grass vs Fire: Bug takes 2x from Fire, Grass takes 2x from Fire -> 4x combined.
        val multiplier = TypeChart.combinedMultiplierByName("Fire", listOf("Bug", "Grass"))
        assertEquals(4.0, multiplier)
    }

    @Test
    fun `weakness and resistance cancel out to neutral`() {
        // Electric-Water vs Electric: Electric resists Electric (0.5x), Water is weak to Electric (2x) -> 1x.
        val multiplier = TypeChart.combinedMultiplierByName("Electric", listOf("Electric", "Water"))
        assertEquals(1.0, multiplier)
    }

    @Test
    fun `single type neutral matchup is 1x`() {
        val multiplier = TypeChart.combinedMultiplierByName("Normal", listOf("Fire"))
        assertEquals(1.0, multiplier)
    }

    @Test
    fun `type names are case-insensitive on the leading letter`() {
        val multiplier = TypeChart.combinedMultiplierByName("ground", listOf("electric", "flying"))
        assertEquals(0.0, multiplier)
    }
}
