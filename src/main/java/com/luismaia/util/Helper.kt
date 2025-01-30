package com.luismaia.util

import net.minecraft.component.DataComponentTypes;
import net.minecraft.item.ItemStack
import net.minecraft.util.Unit


object Helper {
    fun removeLore(itemStack: ItemStack): ItemStack {
        //itemStack.getOrCreateNbt().putInt("HideFlags", 32)
        itemStack.set(DataComponentTypes.HIDE_TOOLTIP, Unit.INSTANCE)

        return itemStack
    }

}