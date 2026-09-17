package com.cwg.mod.util

import com.cobblemon.mod.common.api.conditional.RegistryLikeCondition
import com.cobblemon.mod.common.api.conditional.RegistryLikeIdentifierCondition
import com.cobblemon.mod.common.api.conditional.RegistryLikeTagCondition
import com.cobblemon.mod.common.api.spawning.MoonPhaseRange
import com.cobblemon.mod.common.api.spawning.condition.MoonPhase
import com.cobblemon.mod.common.util.asTranslated
import com.cwg.mod.CobblemonWikiGui
import net.minecraft.advancements.critereon.ItemPredicate
import net.minecraft.resources.ResourceLocation

/**
 * Turns upstream Cobblemon/vanilla identifiers into human-readable, lang.json-aware labels
 * instead of raw registry ids or Kotlin toString() dumps. Shared by [SpawnInfoFormatter] and
 * [EvolutionRequirementFormatter].
 */
/**
 * Pure string logic behind [ResourceLocation.toReadableLabel], kept free of Minecraft types so
 * it can be unit tested directly (this project's test source set does not bootstrap the game).
 */
internal fun readableLabel(namespace: String, path: String): String {
    val label = path.removePrefix("is_")
        .replace('_', ' ')
        .split(' ')
        .joinToString(" ") { it.replaceFirstChar(Char::uppercase) }
    return if (namespace in setOf("minecraft", "cobblemon")) label
    else "$label (${namespace.replaceFirstChar(Char::uppercase)})"
}

internal fun ResourceLocation.toReadableLabel(): String = readableLabel(namespace, path)

internal fun RegistryLikeCondition<*>.toReadableLabel(): String = when (this) {
    is RegistryLikeTagCondition<*> -> tag.location.toReadableLabel()
    is RegistryLikeIdentifierCondition<*> -> identifier.toReadableLabel()
    else -> toString()
}

internal fun MoonPhase.toReadableLabel(): String {
    val lang = CobblemonWikiGui.langConfig
    return when (this) {
        MoonPhase.FULL_MOON -> lang.moonPhaseFullMoon
        MoonPhase.WANING_GIBBOUS -> lang.moonPhaseWaningGibbous
        MoonPhase.THIRD_QUARTER -> lang.moonPhaseThirdQuarter
        MoonPhase.WANING_CRESCENT -> lang.moonPhaseWaningCrescent
        MoonPhase.NEW_MOON -> lang.moonPhaseNewMoon
        MoonPhase.WAXING_CRESCENT -> lang.moonPhaseWaxingCrescent
        MoonPhase.FIRST_QUARTER -> lang.moonPhaseFirstQuarter
        MoonPhase.WAXING_GIBBOUS -> lang.moonPhaseWaxingGibbous
    }
}

internal fun MoonPhaseRange.toReadableLabel(): String =
    ranges.flatMap { it.toList() }
        .distinct()
        .sorted()
        .joinToString(", ") { MoonPhase.values()[it].toReadableLabel() }

internal fun ItemPredicate.toReadableLabel(): String {
    val holderSet = items.orElse(null) ?: return "any item"
    val tagKey = holderSet.unwrapKey().orElse(null)
    return if (tagKey != null) {
        tagKey.location.toReadableLabel()
    } else {
        holderSet.joinToString(", ") { it.value().descriptionId.asTranslated().string }
    }
}
