package com.cwg.mod.util

import com.cobblemon.mod.common.api.spawning.CobblemonSpawnPools
import com.cobblemon.mod.common.api.spawning.condition.SpawningCondition
import com.cobblemon.mod.common.api.spawning.detail.PokemonSpawnDetail
import com.cobblemon.mod.common.api.text.blue
import com.cobblemon.mod.common.api.text.bold
import com.cobblemon.mod.common.api.text.text
import com.cobblemon.mod.common.api.text.yellow
import com.cobblemon.mod.common.pokemon.FormData
import com.cobblemon.mod.common.registry.BiomeIdentifierCondition
import com.cobblemon.mod.common.registry.BiomeTagCondition
import com.cobblemon.mod.common.registry.FluidIdentifierCondition
import com.cobblemon.mod.common.registry.FluidTagCondition
import com.cobblemon.mod.common.registry.ItemIdentifierCondition
import com.cobblemon.mod.common.registry.ItemTagCondition
import com.cobblemon.mod.common.registry.StructureIdentifierCondition
import com.cobblemon.mod.common.registry.StructureTagCondition
import com.cwg.mod.CobblemonWikiGui
import net.minecraft.ChatFormatting
import net.minecraft.network.chat.Component
import net.minecraft.network.chat.MutableComponent
import net.minecraft.resources.ResourceLocation

/**
 * Formats spawn pool lookups and per-condition lore (biomes/weather/time/etc) for the
 * spawn-conditions GUI. Split out of CobblemonUtil because this is the logic most exposed
 * to breaking changes whenever Cobblemon's spawning API changes between versions.
 */
object SpawnInfoFormatter {
    private val lang = CobblemonWikiGui.langConfig

    // World spawn pool can hold thousands of entries once addons are installed; indexing it
    // once by species avoids a full linear scan every time a player opens the spawn GUI.
    @Volatile
    private var spawnPoolIndex: Map<String, List<PokemonSpawnDetail>>? = null

    private fun buildSpawnPoolIndex(): Map<String, List<PokemonSpawnDetail>> =
        CobblemonSpawnPools.WORLD_SPAWN_POOL
            .filterIsInstance<PokemonSpawnDetail>()
            .filter { it.pokemon.species != null }
            .groupBy { it.pokemon.species!! }

    fun invalidateSpawnPoolCache() {
        spawnPoolIndex = null
    }

    fun getSpawnDetails(formData: FormData): List<PokemonSpawnDetail> {
        val index = spawnPoolIndex ?: buildSpawnPoolIndex().also { spawnPoolIndex = it }
        return index[formData.species.resourceIdentifier.path].orEmpty()
    }

    private val dayCycleLabels: Map<String, String>
        get() = mapOf(
            "Any time" to lang.cycleAnyTime,
            "Day" to lang.cycleDay,
            "Night" to lang.cycleNight,
            "Noon" to lang.cycleNoon,
            "Midnight" to lang.cycleMidnight,
            "Dawn" to lang.cycleDawn,
            "Dusk" to lang.cycleDusk,
            "Twilight" to lang.cycleTwilight,
            "Morning" to lang.cycleMorning,
            "Afternoon" to lang.cycleAfternoon,
        )

    fun getSpawnTime(conditions: MutableList<SpawningCondition<*>>): MutableList<Component> {
        val timeRanges = mutableListOf<IntRange>()

        conditions.forEach { condition ->
            condition.timeRange?.ranges?.let { ranges ->
                timeRanges.addAll(ranges)
            }
        }

        val labels = dayCycleLabels
        val matchingCycles = matchingDayCycleKeys(timeRanges, dayCycleMap)
            .map { key -> (labels[key] ?: key).text().yellow() as Component }
            .toMutableList()

        if (matchingCycles.isEmpty()) {
            matchingCycles.add(lang.cycleAnyTime.text().yellow())
        }
        return matchingCycles
    }

    private fun label(prefix: String, name: String): MutableComponent =
        Component.literal(prefix).withStyle(ChatFormatting.DARK_GRAY)
            .append(Component.literal("$name: ").withStyle(ChatFormatting.GRAY))

    private fun tagged(tag: String, name: String, color: ChatFormatting): MutableComponent =
        Component.literal("[$tag] ").withStyle(ChatFormatting.DARK_GRAY)
            .append(Component.literal(name).withStyle(color))

    private fun joined(items: List<Component>): MutableComponent {
        val separator = Component.literal(", ").withStyle(ChatFormatting.DARK_GRAY)
        val result = Component.literal("")
        items.forEachIndexed { i, item ->
            if (i > 0) result.append(separator)
            result.append(item)
        }
        return result
    }

    private fun SpawningCondition<*>.toLoreLines(prefix: String = "- "): List<Component> =
        buildList {
            biomes?.takeIf { it.isNotEmpty() }?.let { set ->
                val values = set.map { cond ->
                    when (cond) {
                        is BiomeTagCondition -> Component.literal(cond.tag.location.toReadableLabel()).withStyle(ChatFormatting.GREEN)
                        is FluidTagCondition -> tagged(lang.fluidTag, cond.tag.location.toReadableLabel(), ChatFormatting.AQUA)
                        is ItemTagCondition -> tagged(lang.itemTag, cond.tag.location.toReadableLabel(), ChatFormatting.GOLD)
                        is StructureTagCondition -> tagged(lang.structureTag, cond.tag.location.toReadableLabel(), ChatFormatting.LIGHT_PURPLE)

                        is BiomeIdentifierCondition -> Component.literal(cond.identifier.toReadableLabel()).withStyle(ChatFormatting.GREEN)
                        is FluidIdentifierCondition -> tagged(lang.fluidTag, cond.identifier.toReadableLabel(), ChatFormatting.AQUA)
                        is ItemIdentifierCondition -> tagged(lang.itemTag, cond.identifier.toReadableLabel(), ChatFormatting.GOLD)
                        is StructureIdentifierCondition -> tagged(lang.structureTag, cond.identifier.toReadableLabel(), ChatFormatting.LIGHT_PURPLE)

                        else -> Component.literal("[Unknown] $cond").withStyle(ChatFormatting.RED)
                    }
                }
                add(label(prefix, lang.biomesLabel).append(joined(values)))
            }
            moonPhase?.let {
                add(label(prefix, lang.moonPhaseLabel).append(Component.literal(it.toReadableLabel()).withStyle(ChatFormatting.LIGHT_PURPLE)))
            }
            canSeeSky?.let {
                add(label(prefix, lang.canSeeSkyLabel).append(Component.literal(it.toString()).withStyle(ChatFormatting.AQUA)))
            }

            val axes = buildList<Component> {

                val xRange = when {
                    minX != null && maxX != null -> "$minX-$maxX"
                    minX != null -> "$minX+"
                    maxX != null -> "0-$maxX"
                    else -> null
                }
                xRange?.let { add(Component.literal("X[").withStyle(ChatFormatting.GOLD).append(Component.literal(it).withStyle(ChatFormatting.WHITE)).append(Component.literal("]").withStyle(ChatFormatting.GOLD))) }

                val yRange = when {
                    minY != null && maxY != null -> "$minY-$maxY"
                    minY != null -> "$minY+"
                    maxY != null -> "0-$maxY"
                    else -> null
                }
                yRange?.let { add(Component.literal("Y[").withStyle(ChatFormatting.GOLD).append(Component.literal(it).withStyle(ChatFormatting.WHITE)).append(Component.literal("]").withStyle(ChatFormatting.GOLD))) }

                val zRange = when {
                    minZ != null && maxZ != null -> "$minZ-$maxZ"
                    minZ != null -> "$minZ+"
                    maxZ != null -> "0-$maxZ"
                    else -> null
                }
                zRange?.let { add(Component.literal("Z[").withStyle(ChatFormatting.GOLD).append(Component.literal(it).withStyle(ChatFormatting.WHITE)).append(Component.literal("]").withStyle(ChatFormatting.GOLD))) }
            }

            if (axes.isNotEmpty()) {
                add(label(prefix, lang.areaLabel).append(joined(axes)))
            }

            val lightRange = when {
                minLight != null && maxLight != null -> "$minLight-$maxLight"
                minLight != null -> "$minLight+"
                maxLight != null -> "0-$maxLight"
                else -> null
            }

            lightRange?.let {
                add(label(prefix, lang.lightLabel).append(Component.literal(it).withStyle(ChatFormatting.YELLOW)))
            }

            val skyLightRange = when {
                minSkyLight != null && maxSkyLight != null -> "$minSkyLight-$maxSkyLight"
                minSkyLight != null -> "$minSkyLight+"
                maxSkyLight != null -> "0-$maxSkyLight"
                else -> null
            }
            skyLightRange?.let {
                add(label(prefix, lang.skyLightLabel).append(Component.literal(it).withStyle(ChatFormatting.YELLOW)))
            }

            if (isRaining == true) add(Component.literal(prefix).withStyle(ChatFormatting.DARK_GRAY).append(Component.literal(lang.rainingLabel).withStyle(ChatFormatting.AQUA)))
            if (isThundering == true) add(Component.literal(prefix).withStyle(ChatFormatting.DARK_GRAY).append(Component.literal(lang.thunderingLabel).withStyle(ChatFormatting.DARK_PURPLE)))
            if (isSlimeChunk == true) add(Component.literal(prefix).withStyle(ChatFormatting.DARK_GRAY).append(Component.literal(lang.slimeChunkLabel).withStyle(ChatFormatting.GREEN)))

            structures?.takeIf { it.isNotEmpty() }?.let { set ->
                val values = set.map { either ->
                    val location = either.map(
                        { ResourceLocation.fromNamespaceAndPath(it.namespace, it.path) },
                        { it.location }
                    )
                    Component.literal(location.toReadableLabel()).withStyle(ChatFormatting.LIGHT_PURPLE)
                }
                add(label(prefix, lang.structuresLabel).append(joined(values)))
            }

            markers?.takeIf { it.isNotEmpty() }?.let { set ->
                val values = set.map { Component.literal(it.toString()).withStyle(ChatFormatting.WHITE) }
                add(label(prefix, lang.markersLabel).append(joined(values)))
            }
        }

    fun addConditionSection(
        lore: MutableList<Component>,
        title: String,
        conditions: List<SpawningCondition<*>>
    ) {
        conditions.forEach { condition ->
            val lines = condition.toLoreLines()
            if (lines.isNotEmpty()) {
                lore.add(title.blue().bold())
                lines.forEach { lore.add(it) }
            }
        }
    }
}
