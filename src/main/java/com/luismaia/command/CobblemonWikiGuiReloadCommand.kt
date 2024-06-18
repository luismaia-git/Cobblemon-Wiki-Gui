package com.luismaia.command

import com.cobblemon.mod.common.command.argument.PokemonArgumentType.Companion.getPokemon
import com.cobblemon.mod.common.command.argument.PokemonArgumentType.Companion.pokemon
import com.luismaia.CobblemonWikiGui
import com.luismaia.gui.PokeWikiGui
import com.mojang.brigadier.Command
import com.mojang.brigadier.CommandDispatcher
import com.mojang.brigadier.arguments.ArgumentType
import com.mojang.brigadier.arguments.StringArgumentType
import com.mojang.brigadier.context.CommandContext
import net.minecraft.server.command.CommandManager
import net.minecraft.server.command.ServerCommandSource
import net.minecraft.text.Text


object CobblemonWikiGuiReloadCommand {
    const val NAME: String = "cobblemonwiki"
    var permission = CobblemonWikiGui.permissions.getPermission("CWGReload")

    private fun requiresPermission(context: ServerCommandSource): Boolean {
        if (CobblemonWikiGui.getConfigManager().isEnablePermissionNodes()) {
            if (context.isExecutedByPlayer) {
                return CobblemonWikiGui.permissions.hasPermission(context.player!!,
                    this.permission!!)
            } else {
                return true
            }
        } else {
            return true
        }
    }
    fun register(
        dispatcher: CommandDispatcher<ServerCommandSource>,
    ) {
        dispatcher.register(
            CommandManager.literal(NAME)
                .requires(this::requiresPermission)
                .then(
                    CommandManager.argument("reload", StringArgumentType.string())
                        .executes { context: CommandContext<ServerCommandSource> ->
                            execute(
                                context
                            )
                        }
                )
        )


    }


    fun execute(context: CommandContext<ServerCommandSource>): Int {
        val playerContext = context.source.player

        if (context.source.isExecutedByPlayer) {
            CobblemonWikiGui.reloadConfigCobblemonWikiGui(playerContext!!)
            return Command.SINGLE_SUCCESS
        }

        CobblemonWikiGui.reloadConfigCobblemonWikiGui(null)
        return Command.SINGLE_SUCCESS
    }
}