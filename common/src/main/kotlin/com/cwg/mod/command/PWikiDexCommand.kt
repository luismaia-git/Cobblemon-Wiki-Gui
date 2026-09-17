package com.cwg.mod.command

import com.cwg.mod.CobblemonWikiGui
import com.cwg.mod.api.permission.CobblemonWikiGuiPermissions
import com.cwg.mod.gui.PokedexGui
import com.cwg.mod.util.permission
import com.mojang.brigadier.Command
import com.mojang.brigadier.CommandDispatcher
import com.mojang.brigadier.context.CommandContext
import net.minecraft.commands.CommandSourceStack
import net.minecraft.commands.Commands.literal
import net.minecraft.network.chat.Component

/**
 * Opens the paginated species browser (see [PokedexGui]). Kept as its own command - and its own
 * permission node - rather than a `/pwiki` no-arg branch, for two reasons: Brigadier permissions
 * apply per command node (so a `/pwiki` no-arg branch couldn't have its own gate separate from
 * `/pwiki <species>`), and the name deliberately avoids "pokedex"/"dex" alone since Cobblemon
 * already registers its own `/pokedex` admin command.
 */
object PWikiDexCommand {

    private const val NAME = "pwikidex"

    fun register(dispatcher: CommandDispatcher<CommandSourceStack>) {
        dispatcher.register(literal(NAME)
            .permission(CobblemonWikiGuiPermissions.PWIKIDEX)
            .executes(::execute))
    }

    fun execute(context: CommandContext<CommandSourceStack>): Int {
        try {
            PokedexGui.open(context.source.playerOrException)
            return Command.SINGLE_SUCCESS
        } catch (e: Exception) {
            context.source.sendFailure(Component.literal("An internal error occurred. Check logs for details."))
            CobblemonWikiGui.LOGGER.error("Failed to open PWikiDex browser GUI", e)
            return 0
        }
    }
}
