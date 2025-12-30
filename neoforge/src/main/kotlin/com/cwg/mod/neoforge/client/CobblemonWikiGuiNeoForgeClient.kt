package com.cwg.mod.neoforge.client


import com.cwg.mod.CobblemonWikiGuiClientImplementation
import com.cwg.mod.CobblemonWikiGuiNetwork
import com.cwg.mod.client.CobblemonWikiGuiClient
import net.minecraft.client.Minecraft
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent
import net.neoforged.neoforge.client.event.ClientPlayerNetworkEvent
import net.neoforged.neoforge.common.NeoForge
import thedarkcolour.kotlinforforge.neoforge.forge.MOD_BUS

object CobblemonWikiGuiNeoForgeClient : CobblemonWikiGuiClientImplementation {

    fun init() {
        with(MOD_BUS) {
            addListener(::onClientSetup)
        }

        // Send client locale to server when joining
        NeoForge.EVENT_BUS.addListener { event: ClientPlayerNetworkEvent.LoggingIn ->
            val minecraft = Minecraft.getInstance()
            val locale = minecraft.options.languageCode
            CobblemonWikiGuiNetwork.sendToServer(ClientLocalePacket(locale))
        }
    }

    private fun onClientSetup(event: FMLClientSetupEvent) {
        event.enqueueWork {
            CobblemonWikiGuiClient.initialize(this)
        }
    }
}