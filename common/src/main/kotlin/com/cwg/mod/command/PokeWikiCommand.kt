package com.cwg.mod.command

import com.cobblemon.mod.common.command.argument.FormArgumentType
import com.cobblemon.mod.common.command.argument.SpeciesArgumentType
import com.cwg.mod.api.permission.CobblemonWikiGuiPermissions
import com.cwg.mod.gui.PokeWikiGui
import com.cwg.mod.util.alias
import com.cwg.mod.util.permission
import com.mojang.brigadier.Command
import com.mojang.brigadier.CommandDispatcher
import com.mojang.brigadier.context.CommandContext
import net.minecraft.commands.CommandSourceStack
import net.minecraft.commands.Commands.argument
import net.minecraft.commands.Commands.literal
import net.minecraft.commands.arguments.EntityArgument
import net.minecraft.network.chat.Component
import net.minecraft.server.level.ServerPlayer

object PokeWikiCommand {

    private const val NAME = "pwiki"
    private const val PLAYER = "player"
    private const val SPECIES = "species"
    private const val FORM = "form"
    private const val NAME_OTHER = "${NAME}other"

    val ALIASES: List<String> = listOf("pokewiki", "pokemonwiki","cobblemonwiki", "cobblewiki", "cwiki")
    val ALIASES_OTHER : List<String> = ALIASES.map { "${it}other" }

    fun register(dispatcher: CommandDispatcher<CommandSourceStack>) {
        // Comando para si mesmo
        val selfCommand = dispatcher.register(literal(NAME)
            .permission(CobblemonWikiGuiPermissions.PWIKI)
            .then(argument(SPECIES, SpeciesArgumentType.species())
                // Executa sem forma especificada
                .executes { execute(it, it.source.playerOrException, null) }
                // Ou com forma especificada (opcional)
                .then(argument(FORM, FormArgumentType.form())
                    .executes { execute(it, it.source.playerOrException, it.form()) })))

        for (alias: String in ALIASES) {
            dispatcher.register(selfCommand.alias(alias))
        }

        // Comando para outro jogador
        val otherCommand = dispatcher.register(literal(NAME_OTHER)
            .permission(CobblemonWikiGuiPermissions.PWIKIANOTHER)
            .then(argument(SPECIES, SpeciesArgumentType.species())
                .then(argument(PLAYER, EntityArgument.player())
                    // Executa sem forma especificada
                    .executes { execute(it, it.player(), null) }
                    // Ou com forma especificada (opcional)
                    .then(argument(FORM, FormArgumentType.form())
                        .executes { execute(it, it.player(), it.form()) }))))

        for (alias: String in ALIASES_OTHER) {
            dispatcher.register(otherCommand.alias(alias))
        }
    }

    fun execute(context: CommandContext<CommandSourceStack>, player: ServerPlayer, formName: String?): Int {
        try {
            val pokemonSpecies = SpeciesArgumentType.getPokemon(context, SPECIES)

            if (!context.source.isPlayer && player == context.source.playerOrException) {
                context.source.sendFailure(Component.literal("Command without target player cannot be executed by console"))
                return 0
            }

            val species = if (formName != null) {
                val form = pokemonSpecies.getFormByName(formName)
                form
            } else {
                pokemonSpecies.standardForm
            }

            PokeWikiGui.open(species, player)

            return Command.SINGLE_SUCCESS
        } catch (e: Exception) {
            context.source.sendFailure(Component.literal("An internal error occurred. Check logs for details."))
            e.printStackTrace()
            return 0
        }
    }

    private fun CommandContext<CommandSourceStack>.form(): String? {
        return try {
            getArgument(FORM, String::class.java)
        } catch (_: Exception) {
            null
        }
    }

    // Função auxiliar para obter o jogador do contexto
    private fun CommandContext<CommandSourceStack>.player(): ServerPlayer {
        return EntityArgument.getPlayer(this, PLAYER)
    }
}