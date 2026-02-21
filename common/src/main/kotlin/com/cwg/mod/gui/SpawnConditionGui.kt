package com.cwg.mod.gui

import com.cobblemon.mod.common.api.text.blue
import com.cobblemon.mod.common.api.text.bold
import com.cobblemon.mod.common.api.text.red
import com.cobblemon.mod.common.api.text.text
import com.cobblemon.mod.common.api.text.yellow
import com.cobblemon.mod.common.item.PokemonItem
import com.cobblemon.mod.common.pokemon.FormData
import com.cwg.mod.CobblemonWikiGui
import com.cwg.mod.helper.GuiHelper
import com.cwg.mod.util.CobblemonUtil
import com.cwg.mod.util.CobblemonUtil.addConditionSection
import com.cwg.mod.util.CobblemonUtil.getSpawnDetails
import eu.pb4.sgui.api.elements.GuiElement
import eu.pb4.sgui.api.elements.GuiElementBuilder
import eu.pb4.sgui.api.gui.SimpleGui
import net.minecraft.network.chat.Component
import net.minecraft.server.level.ServerPlayer
import net.minecraft.world.inventory.MenuType
import net.minecraft.world.item.Items

object SpawnConditionGui {
    val lang = CobblemonWikiGui.langConfig

    private val CONTENT_SPACE = arrayOf(
        10, 11, 12, 13, 14, 15, 16,
        19, 20, 21, 22, 23, 24, 25,
    )

    private const val ITEMS_PER_PAGE = 14

    fun open(species: FormData, player: ServerPlayer, page: Int = 0): SimpleGui {
        val gui = SimpleGui(MenuType.GENERIC_9x4, player, false)
        val redPane = GuiHelper.RED_PANE

        gui.title = Component.literal("Spawn Conditions").red()

        val spawnConditionButtons: MutableList<GuiElement> = mutableListOf()

        getSpawnDetails(species).forEach { s ->
            val lore: MutableList<Component> = ArrayList()
            addConditionSection(lore, "Conditions: ", s.conditions)
            addConditionSection(lore, "Anti-Conditions: ", s.anticonditions)


            val spawnTimes = CobblemonUtil.getSpawnTime(s.conditions)
            lore.add("Time: ".blue())
            spawnTimes.forEach { time -> lore.add("- ".text().yellow().append(time))}


            val button = GuiElementBuilder(PokemonItem.from(species.species))
                .setName(Component.literal("§a${species.species.name} §b[§e${s.bucket.name}§b]"))
                .setLore(lore)
                .build()
            spawnConditionButtons.add(button)
        }

        if (spawnConditionButtons.isEmpty()) {
            val noSpawnConditionButton = GuiHelper
                .createEmptyButton(Items.PAPER.defaultInstance)
                .setName(lang.noSpawnConditionFound.format(species.species.name).text().yellow())
                .setLore(listOf(lang.goBackClick.text()))
                .setCallback { _, _, _, gui ->
                    gui.close()
                    PokeWikiGui.open(species, gui.player)
                }
                .build()
            gui.setSlot(13, noSpawnConditionButton)
        }

            val totalPages = (spawnConditionButtons.size + ITEMS_PER_PAGE - 1) / ITEMS_PER_PAGE
            val currentPage = page.coerceIn(0, maxOf(0, totalPages - 1))
            val startIndex = currentPage * ITEMS_PER_PAGE
            val endIndex = minOf(startIndex + ITEMS_PER_PAGE, spawnConditionButtons.size)

            for (i in startIndex until endIndex) {
                val slotIndex = CONTENT_SPACE[i - startIndex]
                gui.setSlot(slotIndex, spawnConditionButtons[i])
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