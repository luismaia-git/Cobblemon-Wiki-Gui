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

    private const val ITEMS_PER_PAGE = 14

    fun open(species: FormData, player: ServerPlayer, page: Int = 0): SimpleGui {
        val gui = SimpleGui(MenuType.GENERIC_9x4, player, false)
        val redPane = GuiHelper.RED_PANE

        gui.title = Component.literal("Cobblemon Wiki - Evolutions").red()

        val evolutions = species.evolutions
        val evolutionButtons: MutableList<GuiElement> = mutableListOf()

        if (evolutions.isEmpty()) {
            val noEvolutionButton = GuiHelper
                .createEmptyButton(Items.PAPER.defaultInstance)
                .setName(lang.noEvolutionFound.format(species.name).text().yellow())
                .setLore(listOf(lang.goBackClick.text()))
                .setCallback { _, _, _, gui ->
                    gui.close()
                    PokeWikiGui.open(species, gui.player)
                }
                .build()
            gui.setSlot(13, noEvolutionButton)
        } else {
            // Criar botões de todas as evoluções
            evolutions.forEach { evolution ->
                val pokemonSpecies = evolution.result.species?.let {
                    try {
                        PokemonSpecies.getByIdentifier(it.asIdentifierDefaultingNamespace())
                    } catch (_: ResourceLocationException) {
                        null
                    }
                }

                if (pokemonSpecies != null) {
                    val loreRequirements = CobblemonUtil.getRequirementsToWikiGui(evolution)
                    val button = GuiHelper.createPokemonButton(pokemonSpecies.standardForm)
                        .setLore(loreRequirements)
                        .setCallback { _, _, _, gui ->
                            gui.close()
                            PokeWikiGui.open(pokemonSpecies.standardForm, gui.player)
                        }
                        .build()
                    evolutionButtons.add(button)
                }
            }


            val totalPages = (evolutionButtons.size + ITEMS_PER_PAGE - 1) / ITEMS_PER_PAGE
            val currentPage = page.coerceIn(0, maxOf(0, totalPages - 1))
            val startIndex = currentPage * ITEMS_PER_PAGE
            val endIndex = minOf(startIndex + ITEMS_PER_PAGE, evolutionButtons.size)

            for (i in startIndex until endIndex) {
                val slotIndex = CONTENT_SPACE[i - startIndex]
                gui.setSlot(slotIndex, evolutionButtons[i])
            }

            if (currentPage > 0) {
                val prevButton = GuiHelper
                    .createEmptyButton(Items.ARROW.defaultInstance)
                    .setName(Component.literal("←").yellow())
                    .setCallback { _, _, _, gui ->
                        gui.close()
                        open(species, gui.player, currentPage - 1)
                    }
                    .build()
                gui.setSlot(18, prevButton)
            }

            if (currentPage < totalPages - 1) {
                val nextButton = GuiHelper
                    .createEmptyButton(Items.ARROW.defaultInstance)
                    .setName(Component.literal("→").yellow())
                    .setCallback { _, _, _, gui ->
                        gui.close()
                        open(species, gui.player, currentPage + 1)
                    }
                    .build()
                gui.setSlot(26, nextButton)
            }

            if (totalPages > 1) {
                val pageIndicator = GuiHelper
                    .createEmptyButton(Items.BOOK.defaultInstance)
                    .setName(Component.literal("Current page ${currentPage + 1}/${totalPages}").yellow())
                    .build()
                gui.setSlot(22, pageIndicator)
            }
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