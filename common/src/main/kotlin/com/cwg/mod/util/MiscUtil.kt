package com.cwg.mod.util

import com.cwg.mod.CobblemonWikiGui
import net.minecraft.network.chat.Component
import net.minecraft.resources.ResourceLocation

fun cobblemonWikiResource(path: String) = ResourceLocation.fromNamespaceAndPath(CobblemonWikiGui.MOD_ID, path)

fun Component.bold() = also { it.style.withBold(true) }
