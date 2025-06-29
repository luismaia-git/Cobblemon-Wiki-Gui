package com.cwg.mod.helper

import com.cobblemon.mod.common.item.PokemonItem
import com.cobblemon.mod.common.pokemon.Species
import eu.pb4.sgui.api.elements.GuiElement
import eu.pb4.sgui.api.elements.GuiElementBuilder
import eu.pb4.sgui.api.gui.SimpleGui
import net.minecraft.core.component.DataComponents
import net.minecraft.network.chat.Component
import net.minecraft.world.item.ItemStack
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
                    println("Valores fora dos limites permitidos.")
                }
            }
            LineType.VERTICAL -> {
                if (startIndex in 0 until 6 && endIndex in 0 until 6 && rowOrColumnPos in 0 until numCols) {
                    for (row in startIndex..endIndex) {
                        val index = row * numCols + rowOrColumnPos
                        gui.setSlot(index,guiElement )
                    }
                } else {
                    println("Valores fora dos limites permitidos.")
                }
            }
        }
    }

    fun createEmptyButton(stack: ItemStack): GuiElementBuilder {
        stack.remove(DataComponents.LORE)
        return GuiElementBuilder(stack).setName(Component.empty())
    }

    fun createPokemonButton(species: Species): GuiElementBuilder {
        val pokemonItem = PokemonItem.from(species)
        val displayName = Component.literal("§b[§e#${species.nationalPokedexNumber}§b] §a${species.name}")
        return GuiElementBuilder(pokemonItem).setName(displayName)
    }



}
