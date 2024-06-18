package com.luismaia.util

import net.minecraft.item.Item
import net.minecraft.item.ItemStack
import net.minecraft.nbt.NbtList
import net.minecraft.nbt.NbtString
import net.minecraft.text.Style
import net.minecraft.text.Text
import net.minecraft.text.Texts


class ItemBuilder {
    var stack: ItemStack? = null

    constructor(item: Item?) {
        this.stack = ItemStack(item)
    }

    constructor(item: ItemStack?) {
        this.stack = item
    }

    fun addLore(lore: Array<Text?>): ItemBuilder {
        val nbt = stack!!.getOrCreateNbt()
        val displayNbt = stack!!.getOrCreateSubNbt("display")
        val nbtLore = NbtList()

        for (text in lore) {
            val line: Text = Texts.join(text?.getWithStyle(Style.EMPTY.withItalic(false)) , Text.of(""))
            nbtLore.add(NbtString.of(Text.Serializer.toJson(line)))
        }

        displayNbt.put("Lore", nbtLore)
        nbt.put("display", displayNbt)
        stack!!.nbt = nbt
        return this
    }

    fun hideAdditional(): ItemBuilder {
        stack!!.addHideFlag(ItemStack.TooltipSection.ADDITIONAL)
        return this
    }

    fun setCustomName(customName: Text): ItemBuilder {
        val pokemonName: Text = Texts.join(customName.getWithStyle(Style.EMPTY.withItalic(false)), Text.of(""))
        stack!!.setCustomName(pokemonName)
        return this
    }

    fun build(): ItemStack? {
        return this.stack
    }
}