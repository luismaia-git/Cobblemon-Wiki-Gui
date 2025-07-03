package com.cwg.mod.fabric.client

import com.cwg.mod.CobblemonWikiGuiClientImplementation
import com.cwg.mod.client.CobblemonWikiGuiClient
import com.cwg.mod.fabric.CobblemonWikiGuiFabric
import net.fabricmc.api.ClientModInitializer

class CobblemonWikiGuiFabricClient: ClientModInitializer, CobblemonWikiGuiClientImplementation {
    override fun onInitializeClient() {
        CobblemonWikiGuiClient.initialize(this)
        CobblemonWikiGuiFabric.networkManager.registerClientHandlers()
    }
}