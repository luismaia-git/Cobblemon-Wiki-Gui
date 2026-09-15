package com.cwg.mod.gui

import com.cobblemon.mod.common.api.pokemon.PokemonSpecies
import com.cobblemon.mod.common.api.text.red
import com.cwg.mod.CobblemonWikiGui
import com.cwg.mod.helper.GuiHelper
import eu.pb4.sgui.api.elements.GuiElement
import eu.pb4.sgui.api.gui.SimpleGui
import net.minecraft.network.chat.Component
import net.minecraft.server.level.ServerPlayer
import net.minecraft.world.inventory.MenuType

/**
 * A paginated browser listing every implemented Pokémon species so players can find one without
 * already knowing its exact name (previously the only way into the wiki was `/pwiki <name>`).
 */
object PokedexGui {
    val lang = CobblemonWikiGui.langConfig

    private val CONTENT_SPACE = arrayOf(
        10, 11, 12, 13, 14, 15, 16,
        19, 20, 21, 22, 23, 24, 25,
        28, 29, 30, 31, 32, 33, 34,
    )

    fun open(player: ServerPlayer, page: Int = 0): SimpleGui {
        val gui = SimpleGui(MenuType.GENERIC_9x6, player, false)
        val redPane = GuiHelper.RED_PANE

        gui.title = Component.literal(lang.pokedexTitle).red()

        val speciesButtons: List<GuiElement> = PokemonSpecies.implemented
            .sortedBy { it.nationalPokedexNumber }
            .map { species ->
                GuiHelper.createPokemonButton(species.standardForm)
                    .setLore(listOf(lang.pokeInfo.let { Component.literal(it) }))
                    .setCallback { _, _, _, sgui ->
                        sgui.close()
                        PokeWikiGui.open(species.standardForm, sgui.player)
                    }
                    .build()
            }

        GuiHelper.paginate(
            gui, speciesButtons, page, CONTENT_SPACE,
            prevSlot = 45, nextSlot = 53, indicatorSlot = 49,
            currentPageLabel = lang.currentPage
        ) { newPage -> open(player, newPage) }

        for (i in 0 until gui.size) {
            if (gui.getSlot(i) == null) {
                gui.setSlot(i, redPane)
            }
        }

        gui.open()
        return gui
    }
}
