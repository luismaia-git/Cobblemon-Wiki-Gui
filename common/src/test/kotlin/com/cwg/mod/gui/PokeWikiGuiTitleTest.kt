package com.cwg.mod.gui

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.CsvSource

class PokeWikiGuiTitleTest {

    @Test
    fun `buildWikiTitle returns base title when form is standard`() {
        assertEquals(
            "Cobblemon Wiki",
            WikiGuiTitle.build("zorua", "zorua", "Zorua")
        )
    }

    @Test
    fun `buildWikiTitle returns title with form when form is not standard`() {
        assertEquals(
            "Cobblemon Wiki - Zorua (Hisui)",
            WikiGuiTitle.build("hisui", "zorua", "Zorua")
        )
    }

    @ParameterizedTest
    @CsvSource(
        "hisui,zorua,Zorua,Cobblemon Wiki - Zorua (Hisui)",
        "alola,meowth,Meowth,Cobblemon Wiki - Meowth (Alola)",
        "galar,weezing,Weezing,Cobblemon Wiki - Weezing (Galar)",
        "default,default,Pikachu,Cobblemon Wiki"
    )
    fun `buildWikiTitle parametrized`(formName: String, standardFormName: String, speciesDisplayName: String, expected: String) {
        assertEquals(expected, WikiGuiTitle.build(formName, standardFormName, speciesDisplayName))
    }
}
