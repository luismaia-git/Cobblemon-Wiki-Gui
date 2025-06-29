package com.cwg.mod.command

import com.mojang.brigadier.CommandDispatcher
import net.minecraft.commands.CommandBuildContext
import net.minecraft.commands.CommandSourceStack
import net.minecraft.commands.Commands


object CobblemonWikiGuiCommands {
    fun register(dispatcher: CommandDispatcher<CommandSourceStack>, registry: CommandBuildContext, selection: Commands.CommandSelection) {
        PokeWikiCommand.register(dispatcher)
        CobblemonWikiGuiReloadCommand.register(dispatcher)
        TestCommand.register(dispatcher)
    }

}
