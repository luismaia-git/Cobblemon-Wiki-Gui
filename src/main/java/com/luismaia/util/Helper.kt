package com.luismaia.util

import net.minecraft.item.ItemStack

object Helper {
    fun removeLore(itemStack: ItemStack): ItemStack {
        val newItemStack = itemStack.copy()

        if (newItemStack.hasNbt() && newItemStack.nbt!!.contains("display")) {
            val displayTag = newItemStack.nbt!!.getCompound("display")
            if (displayTag.contains("Lore")) {
                displayTag.remove("Lore")
                // Remove o display tag se estiver vazio após a remoção da lore
                if (displayTag.isEmpty) {
                    newItemStack.nbt!!.remove("display")
                }
            }
        }

        return newItemStack
    }
}