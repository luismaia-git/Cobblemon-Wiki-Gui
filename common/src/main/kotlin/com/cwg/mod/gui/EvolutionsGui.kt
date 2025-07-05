package com.cwg.mod.gui

import com.cobblemon.mod.common.api.pokemon.PokemonSpecies
import com.cobblemon.mod.common.api.text.red
import com.cobblemon.mod.common.api.text.text
import com.cobblemon.mod.common.api.text.yellow
import com.cobblemon.mod.common.pokemon.Species
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
    fun open(species: Species, player: ServerPlayer): SimpleGui {

        val content: ArrayList<GuiElement?> = arrayListOf()

        val CONTENT_SPACE = arrayOf(
            0,
            10, 11, 12, 13, 14, 15, 16,
            19, 20, 21, 22, 23, 24, 25,
        )

        val gui = SimpleGui(MenuType.GENERIC_9x4, player, false)
        val redPane = GuiHelper.RED_PANE;
        gui.title = Component.literal("Cobblemon Wiki - Evolutions").red()

        val evolutions = species.standardForm.evolutions
        if (evolutions.isEmpty()) {
            content.add(
                GuiHelper
                    .createEmptyButton(Items.PAPER.defaultInstance)
                    .setName(lang.noEvolutionFound.format(species.name).text().yellow())
                    .setLore(listOf(lang.goBackClick.text()))
                    .setCallback { _, _, _, gui ->
                        run {
                            gui.close()
                            PokeWikiGui.open(species, gui.player)
                        }
                    }
                    .build())
        } else {
            content.add(
                GuiHelper
                    .createEmptyButton(Items.BARRIER.defaultInstance)
                    .setName(lang.goBackClick.text().yellow())
                    .setCallback { _, _, _, gui ->
                        run {
                            gui.close()
                            PokeWikiGui.open(species, gui.player)
                        }
                    }
                    .build())

            evolutions.forEach { it ->
                val pokemonSpecies = it.result.species?.let {
                    return@let try {
                        PokemonSpecies.getByIdentifier(it.asIdentifierDefaultingNamespace())
                    } catch (e: ResourceLocationException) {
                        null
                    }
                }

                val loreRequirements = CobblemonUtil.getRequirementsToWikiGui(it)

                if (pokemonSpecies != null) {
                    val button = GuiHelper.createPokemonButton(pokemonSpecies)
                        .setLore(loreRequirements)
                        .setCallback { _, _, _, gui ->
                            run {
                                gui.close()
                                PokeWikiGui.open(pokemonSpecies, gui.player)
                            }
                        }
                        .build()
                    content.add(button)
                }
            }

        }

        for (j in content.indices) {
            gui.setSlot(CONTENT_SPACE[j], content[j])
        }

        for (i in 0 until gui.size) {
            if (gui.getSlot(i) == null) {
                gui.setSlot(i, redPane)
            }
        }

        gui.open()
        return gui

    }

}