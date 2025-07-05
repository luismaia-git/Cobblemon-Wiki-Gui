package com.cwg.mod.command

import com.cwg.mod.CobblemonWikiGui
import com.cwg.mod.api.permission.CobblemonWikiGuiPermissions
import com.cwg.mod.util.permission
import com.mojang.brigadier.Command
import com.mojang.brigadier.CommandDispatcher
import com.mojang.brigadier.arguments.StringArgumentType
import com.mojang.brigadier.context.CommandContext
import net.minecraft.commands.CommandSourceStack
import net.minecraft.commands.Commands.argument
import net.minecraft.commands.Commands.literal
import net.minecraft.network.chat.Component

object CobblemonWikiGuiReloadCommand {

    private const val NAME = "cwg"
    private const val RELOAD = "reload"
    fun register(dispatcher: CommandDispatcher<CommandSourceStack>) {
        dispatcher.register(literal(NAME)
            .permission(CobblemonWikiGuiPermissions.RELOAD)
            .then(argument(RELOAD, StringArgumentType.string())
                .executes { it -> execute(it) }))

    }

    fun execute(context: CommandContext<CommandSourceStack>): Int {
        try {
            CobblemonWikiGui.reloadConfig()
            context.source.sendSystemMessage(Component.literal("Reloaded config"))
            return Command.SINGLE_SUCCESS
        } catch (e: Exception) {
            context.source.sendFailure(Component.literal("An internal error occurred. Check logs for details."))
            return 0
        }
    }

}