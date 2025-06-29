package com.cwg.mod.util

import com.cwg.mod.CobblemonWikiGui
import com.cwg.mod.Environment
import net.minecraft.server.MinecraftServer


/** Runs the given [Runnable] if the caller is on the SERVER side. */
fun ifServer(runnable: Runnable) {
    if (CobblemonWikiGui.implementation.environment() == Environment.SERVER) {
        runnable.run()
    }
}

/** Runs the given [Runnable] if the caller is a dedicated server. */
fun ifDedicatedServer(action: Runnable) {
    if (CobblemonWikiGui.implementation.environment() == Environment.SERVER) {
        action.run()
    }
}

fun server(): MinecraftServer? = CobblemonWikiGui.implementation.server()
