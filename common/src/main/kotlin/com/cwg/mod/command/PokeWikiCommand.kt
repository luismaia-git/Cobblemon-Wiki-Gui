package com.cwg.mod.command

import com.cobblemon.mod.common.command.argument.FormArgumentType
import com.cwg.mod.api.permission.CobblemonWikiGuiPermissions
import com.cwg.mod.command.argument.LocaleSpeciesArgumentType
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
            .then(argument(SPECIES, LocaleSpeciesArgumentType.species())
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
            .then(argument(SPECIES, LocaleSpeciesArgumentType.species())
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

    fun execute(context: CommandContext<CommandSourceStack>, player: ServerPlayer, formData: com.cobblemon.mod.common.pokemon.FormData?): Int {
        try {
            val pokemonSpecies = LocaleSpeciesArgumentType.getPokemon(context, SPECIES)

            if (!context.source.isPlayer && player == context.source.playerOrException) {
                context.source.sendFailure(Component.literal("Command without target player cannot be executed by console"))
                return 0
            }

            val species = if (formData != null) {
                // Get the form name from the provided FormData
                val formName = formData.name

                // Get the correct form from the parsed species (e.g., Articuno's galar form)
                val correctForm = pokemonSpecies.getFormByName(formName)

                if (correctForm == null) {
                    // Try flexible matching as fallback
                    val normalizedInput = formName.lowercase().replace("-", "").replace("_", "").replace(" ", "")

                    pokemonSpecies.forms.find { form ->
                        val normalizedFormName = form.name.lowercase().replace("-", "").replace("_", "").replace(" ", "")
                        val normalizedShowdownId = form.formOnlyShowdownId().lowercase().replace("-", "").replace("_", "")

                        normalizedFormName == normalizedInput ||
                        normalizedFormName.startsWith(normalizedInput) ||
                        normalizedInput.startsWith(normalizedFormName) ||
                        normalizedShowdownId == normalizedInput ||
                        normalizedShowdownId.startsWith(normalizedInput)
                    } ?: run {
                        val availableForms = pokemonSpecies.forms.joinToString(", ") { it.name }
                        context.source.sendFailure(
                            Component.literal("Form '${formName}' not found for ${pokemonSpecies.name}. Available forms: ${availableForms}")
                        )
                        return 0
                    }
                } else {
                    correctForm
                }
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

    private fun CommandContext<CommandSourceStack>.form(): com.cobblemon.mod.common.pokemon.FormData? {
        return try {
            // FormArgumentType returns FormData, not String
            getArgument(FORM, com.cobblemon.mod.common.pokemon.FormData::class.java)
        } catch (e: Exception) {
            null
        }
    }

    // Função auxiliar para obter o jogador do contexto
    private fun CommandContext<CommandSourceStack>.player(): ServerPlayer {
        return EntityArgument.getPlayer(this, PLAYER)
    }
}