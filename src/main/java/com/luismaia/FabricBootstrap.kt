package com.luismaia

import net.fabricmc.api.ModInitializer

class FabricBootstrap : ModInitializer {
    override fun onInitialize() {
        CobblemonWikiGui.initialize()
    }
}
