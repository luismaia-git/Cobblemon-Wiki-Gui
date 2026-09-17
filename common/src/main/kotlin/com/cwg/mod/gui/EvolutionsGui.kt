package com.cwg.mod.gui

import com.cobblemon.mod.common.api.pokemon.PokemonSpecies
import com.cobblemon.mod.common.api.text.red
import com.cobblemon.mod.common.api.text.text
import com.cobblemon.mod.common.api.text.yellow
import com.cobblemon.mod.common.pokemon.FormData
import com.cobblemon.mod.common.util.asIdentifierDefaultingNamespace
import com.cwg.mod.CobblemonWikiGui
import com.cwg.mod.helper.GuiHelper
import com.cwg.mod.util.CobblemonUtil
import eu.pb4.sgui.api.elements.GuiElement
import eu.pb4.sgui.api.gui.SimpleGui
import net.minecraft.ResourceLocationException
import net.minecraft.network.chat.Component
import net.minecraft.server.level.ServerPlayer
import net.minecraft.world.inventory.MenuType
import net.minecraft.world.item.Items

object EvolutionsGui {
    val lang = CobblemonWikiGui.langConfig

    private val CONTENT_SPACE = arrayOf(
        10, 11, 12, 13, 14, 15, 16,
        19, 20, 21, 22, 23, 24, 25,
    )

    fun open(species: FormData, player: ServerPlayer, page: Int = 0): SimpleGui {
        val gui = SimpleGui(MenuType.GENERIC_9x4, player, false)
        val redPane = GuiHelper.RED_PANE

        gui.title = Component.literal(lang.evolutionsTitle).red()

        val evolutions = species.evolutions
        val evolutionButtons: MutableList<GuiElement> = mutableListOf()

        if (evolutions.isEmpty()) {
            val noEvolutionButton = GuiHelper
                .createEmptyButton(Items.PAPER.defaultInstance)
                .setName(lang.noEvolutionFound.format(species.species.name).text().yellow())
                .setLore(listOf(lang.goBackClick.text()))
                .setCallback { _, _, _, gui ->
                    gui.close()
                    PokeWikiGui.open(species, gui.player)
                }
                .build()
            gui.setSlot(13, noEvolutionButton)
        } else {
            evolutions.forEach { evolution ->
                val pokemonSpecies = evolution.result.species?.let {
                    try {
                        PokemonSpecies.getByIdentifier(it.asIdentifierDefaultingNamespace())
                    } catch (_: ResourceLocationException) {
                        null
                    }
                }

                if (pokemonSpecies != null) {
                    val evolutionForm = pokemonSpecies.getFormByName(species.name) ?: pokemonSpecies.standardForm
                    val loreRequirements = CobblemonUtil.getRequirementsToWikiGui(evolution)
                    val button = GuiHelper.createPokemonButton(evolutionForm)
                        .setLore(loreRequirements)
                        .setCallback { _, _, _, gui ->
                            gui.close()
                            PokeWikiGui.open(evolutionForm, gui.player)
                        }
                        .build()
                    evolutionButtons.add(button)
                }
            }


            GuiHelper.paginate(
                gui, evolutionButtons, page, CONTENT_SPACE,
                prevSlot = 18, nextSlot = 26, indicatorSlot = 22,
                currentPageLabel = lang.currentPage
            ) { newPage -> open(species, player, newPage) }
        }

        val backButton = GuiHelper
            .createEmptyButton(Items.BARRIER.defaultInstance)
            .setName(lang.goBackClick.text().yellow())
            .setCallback { _, _, _, gui ->
                gui.close()
                PokeWikiGui.open(species, gui.player)
            }
            .build()
        gui.setSlot(0, backButton)

        for (i in 0 until gui.size) {
            if (gui.getSlot(i) == null) {
                gui.setSlot(i, redPane)
            }
        }

        gui.open()
        return gui
    }
}