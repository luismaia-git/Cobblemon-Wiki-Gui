package com.cwg.mod.gui

/**
 * Pure helper for building the wiki GUI title string.
 * No mod dependencies so it can be unit tested without loading PokeWikiGui.
 */
object WikiGuiTitle {

    /**
     * Builds the wiki GUI title string. When the form is not the standard form,
     * includes species and form name (e.g. "Cobblemon Wiki - Zorua (Hisui)").
     *
     * [baseTitle] and [formTitleTemplate] are configurable (see `CobblemonWikiGuiLang`);
     * [formTitleTemplate] supports the `%species%` and `%form%` placeholders.
     */
    fun build(
        formName: String,
        standardFormName: String,
        speciesDisplayName: String,
        baseTitle: String = "Cobblemon Wiki",
        formTitleTemplate: String = "Cobblemon Wiki - %species% (%form%)"
    ): String {
        return if (formName != standardFormName) {
            val formDisplayName = formName.replaceFirstChar { it.uppercase() }
            formTitleTemplate
                .replace("%species%", speciesDisplayName)
                .replace("%form%", formDisplayName)
        } else {
            baseTitle
        }
    }
}
