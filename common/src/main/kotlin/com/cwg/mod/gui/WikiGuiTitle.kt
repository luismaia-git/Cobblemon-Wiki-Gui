package com.cwg.mod.gui

/**
 * Pure helper for building the wiki GUI title string.
 * No mod dependencies so it can be unit tested without loading PokeWikiGui.
 */
object WikiGuiTitle {

    /**
     * Builds the wiki GUI title string. When the form is not the standard form,
     * includes species and form name (e.g. "Cobblemon Wiki - Zorua (Hisui)").
     */
    fun build(formName: String, standardFormName: String, speciesDisplayName: String): String {
        return if (formName != standardFormName) {
            val formDisplayName = formName.replaceFirstChar { it.uppercase() }
            "Cobblemon Wiki - $speciesDisplayName ($formDisplayName)"
        } else {
            "Cobblemon Wiki"
        }
    }
}
