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

    var language: String = "en_us" // Default language code. Change as needed. (zn_cn)
}

