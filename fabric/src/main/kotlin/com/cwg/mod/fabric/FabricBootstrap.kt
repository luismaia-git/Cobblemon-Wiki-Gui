package com.cwg.mod.fabric

import net.fabricmc.api.ModInitializer

class FabricBootstrap : ModInitializer {
    override fun onInitialize() {
        CobblemonWikiGuiFabric.initialize();
    }
}
