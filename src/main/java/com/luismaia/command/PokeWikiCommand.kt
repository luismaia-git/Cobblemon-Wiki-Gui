package com.luismaia.command

import com.cobblemon.mod.common.command.argument.PokemonArgumentType.Companion.getPokemon
import com.cobblemon.mod.common.command.argument.PokemonArgumentType.Companion.pokemon
import com.luismaia.CobblemonWikiGui
import com.luismaia.gui.PokeWikiGui
import com.mojang.brigadier.Command
import com.mojang.brigadier.CommandDispatcher
import com.mojang.brigadier.context.CommandContext
import net.minecraft.command.argument.EntityArgumentType
import net.minecraft.server.command.CommandManager
import net.minecraft.server.command.ServerCommandSource
import net.minecraft.text.Text


object PokeWikiCommand {

    val ALIASES: List<String> = listOf("pokewiki","pwiki", "pokemonwiki","cobblemonwiki", "cobblewiki", "cwiki")
    var permissionUser = CobblemonWikiGui.permissions.getPermission("CWGShow")
    var permissionAdmin = CobblemonWikiGui.permissions.getPermission("CWGShowAnotherPlayer")

    private fun requiresPermissionUser(context: ServerCommandSource): Boolean {
        if (CobblemonWikiGui.getConfigManager().isEnablePermissionNodes()) {
            if (context.isExecutedByPlayer) {
                return CobblemonWikiGui.permissions.hasPermission(context.player!!,
                    permissionUser!!)
            } else {
                return true
            }
        }

        return true
    }

    private fun requiresPermissionAdmin(context: ServerCommandSource): Boolean {
        if (CobblemonWikiGui.getConfigManager().isEnablePermissionNodes()) {
            if (context.isExecutedByPlayer) {
                val hasPermission = CobblemonWikiGui.permissions.hasPermission(context.player!!, permissionAdmin!!)
                return hasPermission
            } else {
                return true
            }
        }

        return true
    }


    fun register(dispatcher: CommandDispatcher<ServerCommandSource>) {
        for (alias: String in ALIASES) {
            dispatcher.register(
                CommandManager.literal(alias)
                    .requires(this::requiresPermissionUser)
                    .executes { ctx ->
                        execute(ctx, null)
                    }
                    .then(
                        CommandManager.argument("pokemon", pokemon())
                            .executes { ctx ->
                                execute(ctx, null)
                            }
                            .then(
                                CommandManager.argument("player", EntityArgumentType.player())
                                    .requires(this::requiresPermissionAdmin)
                                    .executes { ctx ->
                                        val targetPlayer = EntityArgumentType.getPlayer(ctx, "player")
                                        execute(ctx, targetPlayer.entityName)
                                    }
                            )
                    )
            )
        }

    }

    fun execute(context: CommandContext<ServerCommandSource>, playerName: String?): Int {
        val species = getPokemon(context, "pokemon")
        val playerContext = context.source.player

        if (playerName != null) {
            val playerTarget = context.source.server.playerManager.getPlayer(playerName)
            if (playerTarget != null) {
                PokeWikiGui.open(species, playerTarget)
            } else {
                context.source.sendError(Text.literal("Player $playerName not found"))
            }
        } else {
            if (!context.source.isExecutedByPlayer) {
                context.source.sendMessage(Text.literal("Command without target player cannot be executed by console"))
            }else {
                if (playerContext != null) PokeWikiGui.open(species, playerContext)
            }
        }

        return Command.SINGLE_SUCCESS
    }
}