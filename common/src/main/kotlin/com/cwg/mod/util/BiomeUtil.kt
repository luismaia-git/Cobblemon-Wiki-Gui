package com.cwg.mod.util

import com.cobblemon.mod.common.api.spawning.condition.SpawningCondition
import net.minecraft.core.Registry
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.level.Level
import net.minecraft.world.level.biome.Biome
import net.minecraft.core.registries.Registries

data class CobblemonBiome(val identifier: ResourceLocation, val biome: Biome)

object BiomeUtils {

    fun getAllBiomes(world: Level) : List<CobblemonBiome> {
        // Correct way to get the Registry<Biome> in 1.21.1 Mojang Mappings
        val registry: Registry<Biome> = world.registryAccess().registryOrThrow(Registries.BIOME)

        return registry.entrySet().map { entry ->  CobblemonBiome(entry.key.location(), entry.value)}
    }

    /**
     * Verifica se é possível spawnar em um bioma com base nas condições fornecidas.
     *
     * @param biome O bioma a ser verificado.
     * @param world O mundo atual.
     * @param condition A condição de spawning, tipada explicitamente como <Biome>.
     * @return true se for possível spawnar, false caso contrário.
     */
    fun canSpawnAt (biome: Biome, world: Level, condition: SpawningCondition<*>) : Boolean {

        val registry: Registry<Biome> = world.registryAccess().registryOrThrow(Registries.BIOME)

        if (condition.biomes == null) return false

        val count = condition.biomes!!.count { biomeCondition ->
            biomeCondition.fits(
                biome,
                registry
            )
        }

        return count > 0
    }
}