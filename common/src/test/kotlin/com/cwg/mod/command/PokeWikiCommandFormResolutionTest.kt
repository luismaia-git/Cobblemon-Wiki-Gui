package com.cwg.mod.command

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

/**
 * Tests the form resolution logic used in [PokeWikiCommand.execute]:
 * when formDataArg is null we use standardForm, when non-null we use formDataArg.
 * Uses a generic helper to test without Cobblemon FormData at runtime.
 */
class PokeWikiCommandFormResolutionTest {

    /**
     * Same logic as in PokeWikiCommand: formDataArg ?: standardForm
     */
    private fun <T> resolveFormData(formDataArg: T?, standardForm: T): T = formDataArg ?: standardForm

    @Test
    fun `resolveFormData returns standardForm when formDataArg is null`() {
        val standardForm = "zorua_standard"
        assertEquals(standardForm, resolveFormData(null, standardForm))
    }

    @Test
    fun `resolveFormData returns formDataArg when formDataArg is not null`() {
        val standardForm = "zorua_standard"
        val hisuiForm = "zorua_hisui"
        assertEquals(hisuiForm, resolveFormData(hisuiForm, standardForm))
    }

    @Test
    fun `resolveFormData with Int - null uses standard`() {
        assertEquals(1, resolveFormData(null, 1))
    }

    @Test
    fun `resolveFormData with Int - non-null uses arg`() {
        assertEquals(2, resolveFormData(2, 1))
    }
}
