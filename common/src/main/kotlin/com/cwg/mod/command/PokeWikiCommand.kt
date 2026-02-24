package com.cwg.mod.command

import com.cobblemon.mod.common.command.argument.FormArgumentType
import com.cobblemon.mod.common.command.argument.SpeciesArgumentType
import com.cobblemon.mod.common.pokemon.FormData
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
        val selfCommand = dispatcher.register(literal(NAME)
            .permission(CobblemonWikiGuiPermissions.PWIKI)
            .then(argument(SPECIES, SpeciesArgumentType.species())
                .executes { execute(it, it.source.playerOrException, null) }
                .then(argument(FORM, FormArgumentType.form())
                    .executes { execute(it, it.source.playerOrException, it.getArgument(FORM, FormData::class.java)) })))

        for (alias: String in ALIASES) {
            dispatcher.register(selfCommand.alias(alias))
        }

        val otherCommand = dispatcher.register(literal(NAME_OTHER)
            .permission(CobblemonWikiGuiPermissions.PWIKIANOTHER)
            .then(argument(PLAYER, EntityArgument.player())
                .then(argument(SPECIES, SpeciesArgumentType.species())
                    .executes { execute(it, it.player(), null) }
                    .then(argument(FORM, FormArgumentType.form())
                        .executes { execute(it, it.player(), it.getArgument(FORM, FormData::class.java)) }))))

        for (alias: String in ALIASES_OTHER) {
            dispatcher.register(otherCommand.alias(alias))
        }
    }

    fun execute(context: CommandContext<CommandSourceStack>, player: ServerPlayer, formDataArg: FormData?): Int {
        try {
            val pokemonSpecies = SpeciesArgumentType.getPokemon(context, SPECIES)

            if (!context.source.isPlayer && player == context.source.playerOrException) {
                context.source.sendFailure(Component.literal("Command without target player cannot be executed by console"))
                return 0
            }

            val formData = formDataArg ?: pokemonSpecies.standardForm

            PokeWikiGui.open(formData, player)

            return Command.SINGLE_SUCCESS
        } catch (e: Exception) {
            context.source.sendFailure(Component.literal("An internal error occurred. Check logs for details."))
            e.printStackTrace()
            return 0
        }
    }

    private fun CommandContext<CommandSourceStack>.player(): ServerPlayer {
        return EntityArgument.getPlayer(this, PLAYER)
    }
}