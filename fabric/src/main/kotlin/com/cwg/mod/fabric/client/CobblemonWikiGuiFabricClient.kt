package com.cwg.mod.fabric.client

import com.cwg.mod.CobblemonWikiGuiClientImplementation
import com.cwg.mod.CobblemonWikiGuiNetwork
import com.cwg.mod.client.CobblemonWikiGuiClient
import com.cwg.mod.fabric.CobblemonWikiGuiFabric
import com.cwg.mod.net.messages.server.ClientLocalePacket
import net.fabricmc.api.ClientModInitializer
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents
import net.minecraft.client.Minecraft

class CobblemonWikiGuiFabricClient: ClientModInitializer, CobblemonWikiGuiClientImplementation {
    override fun onInitializeClient() {
        CobblemonWikiGuiClient.initialize(this)
        CobblemonWikiGuiFabric.networkManager.registerClientHandlers()

        // Send client locale to server when joining
        ClientPlayConnectionEvents.JOIN.register { _, _, _ ->
            val minecraft = Minecraft.getInstance()
            val locale = minecraft.options.languageCode
            CobblemonWikiGuiNetwork.sendToServer(ClientLocalePacket(locale))
        }
    }
}