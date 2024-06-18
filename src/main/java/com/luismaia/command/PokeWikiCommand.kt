package com.luismaia.command

import com.cobblemon.mod.common.command.argument.PokemonArgumentType.Companion.getPokemon
import com.cobblemon.mod.common.command.argument.PokemonArgumentType.Companion.pokemon
import com.luismaia.CobblemonWikiGui
import com.luismaia.config.CobblemonWikiGuiConfig
import com.luismaia.gui.PokeWikiGui
import com.mojang.brigadier.Command
import com.mojang.brigadier.CommandDispatcher
import com.mojang.brigadier.arguments.StringArgumentType
import com.mojang.brigadier.context.CommandContext
import net.minecraft.server.command.CommandManager
import net.minecraft.server.command.ServerCommandSource
import net.minecraft.text.Text


object PokeWikiCommand {
    const val NAME: String = "pokewiki"
    val ALIASES: List<String> = listOf("pwiki", "pokemonwiki","cobblemonwiki", "cobblewiki", "cwiki")

    fun register(
        dispatcher: CommandDispatcher<ServerCommandSource>,
    ) {
        dispatcher.register(
            CommandManager.literal(NAME)
                .then(
                    CommandManager.argument("pokemon", pokemon())
                        .executes { context: CommandContext<ServerCommandSource> ->
                            execute(
                                context, null
                            )
                        }.then(CommandManager.argument("player", StringArgumentType.word())
                            .executes { ctx ->
                                val playerName = StringArgumentType.getString(ctx, "player")
                                execute(
                                    ctx, playerName
                                )
                            }
                        )
                )
        )

        for (alias: String in ALIASES) {
            dispatcher.register(
                CommandManager.literal(alias)
                    .then(
                        CommandManager.argument("pokemon", pokemon())
                            .executes { context: CommandContext<ServerCommandSource> ->
                                execute(
                                    context, null
                                )
                            }.then(CommandManager.argument("player", StringArgumentType.word())
                                .executes { ctx ->
                                    val playerName = StringArgumentType.getString(ctx, "player")
                                    execute(
                                        ctx, playerName
                                    )
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
            val player = context.source.server.playerManager.getPlayer(playerName)
            if (player != null) { //inseriu o nome do player
                PokeWikiGui.open(species, player)
                return Command.SINGLE_SUCCESS
            } else {
                //se o player nao foi achado
                context.source.sendError(Text.literal("Player $playerName not found"))
                return Command.SINGLE_SUCCESS
            }
        } else {
            if (!context.source.isExecutedByPlayer) {
                context.source.sendMessage(Text.literal(CobblemonWikiGui.getConfigCobblemonWikiGui()?.pokewikiErrorNotplayer
                    ?: CobblemonWikiGuiConfig().pokewikiErrorNotplayer))
                return Command.SINGLE_SUCCESS
            }
            if(playerContext != null) PokeWikiGui.open(species, playerContext)
        }
        return Command.SINGLE_SUCCESS
    }
}