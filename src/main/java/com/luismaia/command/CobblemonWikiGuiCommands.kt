package com.luismaia.command

import com.mojang.brigadier.CommandDispatcher
import net.minecraft.command.CommandRegistryAccess
import net.minecraft.server.command.CommandManager
import net.minecraft.server.command.ServerCommandSource

object CobblemonWikiGuiCommands {
    fun register(dispatcher: CommandDispatcher<ServerCommandSource>, registry: CommandRegistryAccess, selection: CommandManager.RegistrationEnvironment) {
        PokeWikiCommand.register(dispatcher)
        CobblemonWikiGuiReloadCommand.register(dispatcher)
    }

}
