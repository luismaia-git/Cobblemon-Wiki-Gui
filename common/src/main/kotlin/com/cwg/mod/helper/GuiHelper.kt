package com.cwg.mod.helper

import com.cobblemon.mod.common.api.text.text
import com.cobblemon.mod.common.api.text.yellow
import com.cobblemon.mod.common.item.PokemonItem
import com.cobblemon.mod.common.pokemon.FormData
import com.cwg.mod.CobblemonWikiGui
import eu.pb4.sgui.api.elements.GuiElement
import eu.pb4.sgui.api.elements.GuiElementBuilder
import eu.pb4.sgui.api.gui.SimpleGui
import net.minecraft.core.component.DataComponents
import net.minecraft.network.chat.Component
import net.minecraft.util.Unit
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.Items
import net.minecraft.world.level.block.Blocks

object GuiHelper {

    val RED_PANE: GuiElement = createEmptyButton(ItemStack(Blocks.RED_STAINED_GLASS_PANE, 1)).build()

    enum class LineType {
        HORIZONTAL,
        VERTICAL
    }


    fun setLine(lineType: LineType, gui: SimpleGui, rowOrColumnPos: Int, startIndex: Int, endIndex: Int, guiElement: GuiElement) {
        val numCols = 9
        when (lineType) {
            LineType.HORIZONTAL -> {
                if (startIndex in 0 until numCols && endIndex in 0 until numCols && rowOrColumnPos in 0 until 6) {
                    for (col in startIndex..endIndex) {
                        val index = rowOrColumnPos * numCols + col
                        gui.setSlot(index,guiElement )
                    }
                } else {
                    CobblemonWikiGui.LOGGER.warn("GuiHelper.setLine: values out of allowed bounds (HORIZONTAL row=$rowOrColumnPos, start=$startIndex, end=$endIndex)")
                }
            }
            LineType.VERTICAL -> {
                if (startIndex in 0 until 6 && endIndex in 0 until 6 && rowOrColumnPos in 0 until numCols) {
                    for (row in startIndex..endIndex) {
                        val index = row * numCols + rowOrColumnPos
                        gui.setSlot(index,guiElement )
                    }
                } else {
                    CobblemonWikiGui.LOGGER.warn("GuiHelper.setLine: values out of allowed bounds (VERTICAL col=$rowOrColumnPos, start=$startIndex, end=$endIndex)")
                }
            }
        }
    }

    fun createEmptyButton(stack: ItemStack): GuiElementBuilder {
        stack.remove(DataComponents.LORE)
        stack.set(DataComponents.HIDE_ADDITIONAL_TOOLTIP, Unit.INSTANCE)
        return GuiElementBuilder(stack).setName(Component.empty()).hideDefaultTooltip()
    }

    fun createPokemonButton(formData: FormData): GuiElementBuilder {
        val species = formData.species
        val pokemonItem = PokemonItem.from(species)
        val displayName = Component.literal("§b[§e#${species.nationalPokedexNumber}§b] §a${species.name}")
        return GuiElementBuilder(pokemonItem).setName(displayName)
    }

    /**
     * Lays out a page of [items] into [contentSpace], and wires prev/next arrows + a page
     * indicator when there's more than one page. [navigate] is called with the target page
     * index; callers are expected to close [gui] and reopen with that page.
     *
     * Shared by EvolutionsGui and SpawnConditionGui so their pagination can't drift apart.
     */
    fun paginate(
        gui: SimpleGui,
        items: List<GuiElement>,
        page: Int,
        contentSpace: Array<Int>,
        prevSlot: Int,
        nextSlot: Int,
        indicatorSlot: Int,
        currentPageLabel: String,
        navigate: (Int) -> kotlin.Unit
    ) {
        val itemsPerPage = contentSpace.size
        val totalPages = (items.size + itemsPerPage - 1) / itemsPerPage
        val currentPage = page.coerceIn(0, maxOf(0, totalPages - 1))
        val startIndex = currentPage * itemsPerPage
        val endIndex = minOf(startIndex + itemsPerPage, items.size)

        for (i in startIndex until endIndex) {
            gui.setSlot(contentSpace[i - startIndex], items[i])
        }

        if (currentPage > 0) {
            gui.setSlot(
                prevSlot,
                createEmptyButton(Items.ARROW.defaultInstance)
                    .setName(Component.literal("←").yellow())
                    .setCallback { _, _, _, sgui -> sgui.close(); navigate(currentPage - 1) }
                    .build()
            )
        }

        if (currentPage < totalPages - 1) {
            gui.setSlot(
                nextSlot,
                createEmptyButton(Items.ARROW.defaultInstance)
                    .setName(Component.literal("→").yellow())
                    .setCallback { _, _, _, sgui -> sgui.close(); navigate(currentPage + 1) }
                    .build()
            )
        }

        if (totalPages > 1) {
            gui.setSlot(
                indicatorSlot,
                createEmptyButton(Items.BOOK.defaultInstance)
                    .setName(currentPageLabel.format(currentPage + 1, totalPages).text().yellow())
                    .build()
            )
        }
    }

}

