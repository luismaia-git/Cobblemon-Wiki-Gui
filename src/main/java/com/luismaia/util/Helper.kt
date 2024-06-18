package com.luismaia.util

import net.minecraft.item.ItemStack


object Helper {
    fun removeLore(itemStack: ItemStack): ItemStack {
        itemStack.getOrCreateNbt().putInt("HideFlags", 32)

        return itemStack
    }

}