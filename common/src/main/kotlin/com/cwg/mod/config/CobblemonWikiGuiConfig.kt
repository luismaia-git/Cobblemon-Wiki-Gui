package com.cwg.mod.config

import com.google.gson.GsonBuilder

class CobblemonWikiGuiConfig {

    companion object {
        val GSON = GsonBuilder()
        .disableHtmlEscaping()
        .setPrettyPrinting()
        .create()
    }

    var version: String = "1.0.0"

    /**
     * Item id used as filler/border in wiki GUIs (e.g. "minecraft:red_stained_glass_pane").
     * Set to "" or "none" to disable filler entirely (empty slots stay empty).
     * Falls back to red_stained_glass_pane if the id is invalid or unknown.
     */
    var fillerItem: String = "minecraft:red_stained_glass_pane"

    /**
     * Shows a "Report an Issue" button (opens the GitHub issues page) in a corner slot of the
     * Wiki and Pokedex GUIs. Set to false to remove it and keep that slot as plain filler.
     */
    var showGithubIssuesButton: Boolean = true
}

