package com.cwg.mod.util

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.CsvSource

/**
 * Regression coverage for [readableLabel], the string logic behind every "toReadableLabel()"
 * used to display biome/fluid/item/structure tags in GUI tooltips. This is the piece that broke
 * silently before (raw "cobblemon:is_beach" ids shown to players) - keep it pinned down.
 */
class ReadableLabelsTest {

    @ParameterizedTest
    @CsvSource(
        "cobblemon,is_beach,Beach",
        "cobblemon,is_overworld,Overworld",
        "minecraft,is_forest,Forest",
        "minecraft,deep_ocean,Deep Ocean",
        "cobblemon,is_deep_dark,Deep Dark",
    )
    fun `known namespaces are not annotated`(namespace: String, path: String, expected: String) {
        assertEquals(expected, readableLabel(namespace, path))
    }

    @ParameterizedTest
    @CsvSource(
        "aether,highlands,Highlands (Aether)",
        "biomesoplenty,alps,Alps (Biomesoplenty)",
    )
    fun `unknown namespaces are annotated with the mod name`(namespace: String, path: String, expected: String) {
        assertEquals(expected, readableLabel(namespace, path))
    }

    @ParameterizedTest
    @CsvSource(
        "minecraft,plains,Plains",
        "minecraft,is_hot,Hot",
    )
    fun `is_ prefix is only stripped once from the front`(namespace: String, path: String, expected: String) {
        assertEquals(expected, readableLabel(namespace, path))
    }
}
