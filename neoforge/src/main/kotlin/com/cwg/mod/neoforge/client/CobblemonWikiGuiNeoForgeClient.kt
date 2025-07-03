package com.cwg.mod.neoforge.client


import com.cwg.mod.CobblemonWikiGuiClientImplementation
import com.cwg.mod.client.CobblemonWikiGuiClient
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent
import thedarkcolour.kotlinforforge.neoforge.forge.MOD_BUS

object CobblemonWikiGuiNeoForgeClient : CobblemonWikiGuiClientImplementation {

    fun init() {
        with(MOD_BUS) {
            addListener(::onClientSetup)
        }
    }

    private fun onClientSetup(event: FMLClientSetupEvent) {
        event.enqueueWork {
            CobblemonWikiGuiClient.initialize(this)
        }
    }
}