package com.cwg.mod.util

import com.cobblemon.mod.common.CobblemonItems
import com.cobblemon.mod.common.api.conditional.RegistryLikeTagCondition
import com.cobblemon.mod.common.api.pokemon.PokemonProperties
import com.cobblemon.mod.common.api.pokemon.PokemonSpecies
import com.cobblemon.mod.common.api.pokemon.evolution.Evolution
import com.cobblemon.mod.common.api.spawning.TimeRange
import com.cobblemon.mod.common.api.text.text
import com.cobblemon.mod.common.pokemon.evolution.variants.BlockClickEvolution
import com.cobblemon.mod.common.pokemon.evolution.variants.ItemInteractionEvolution
import com.cobblemon.mod.common.pokemon.evolution.variants.TradeEvolution
import com.cobblemon.mod.common.pokemon.requirements.*
import com.cobblemon.mod.common.util.asTranslated
import com.cwg.mod.CobblemonWikiGui
import net.minecraft.network.chat.Component
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.level.block.Block

/**
 * Builds the human-readable requirement lines shown on the evolutions GUI (trade partner,
 * item interaction, biome/structure/level/stat requirements, etc). Split out of CobblemonUtil
 * since evolution requirement types are another surface that regularly changes between
 * Cobblemon versions.
 */
object EvolutionRequirementFormatter {
    private val lang = CobblemonWikiGui.langConfig

    fun getRequirementsToWikiGui(evolution: Evolution): MutableList<Component> {
        val requirements = evolution.requirements
        val loreRequirements: MutableList<Component> = ArrayList()

        var requiredContextIdentifier: ResourceLocation? = null

        when (evolution) {
            is TradeEvolution -> {
                val tradePokemonString = evolution.requiredContext.asString()

                val tradePokemon by lazy {
                    tradePokemonString.let {
                        PokemonProperties.parse(it)
                    }
                }

                val linkCableTranslatedComponent = "${CobblemonItems.LINK_CABLE.descriptionId}".asTranslated()

                val translation = tradePokemon.species?.let { speciesName ->
                    val tradeSpecies = PokemonSpecies.getByName(speciesName)
                    val displayName = tradeSpecies?.translatedName?.string ?: speciesName
                    lang.tradeSpecific.format(displayName, linkCableTranslatedComponent.string).asTranslated()
                } ?: lang.tradeAny.format(linkCableTranslatedComponent.string).asTranslated()

                loreRequirements.add(translation)
            }
            is ItemInteractionEvolution -> {
                val itemName = evolution.requiredContext.toReadableLabel()
                val text = lang.useItem.format(itemName).asTranslated()
                loreRequirements.add(text)
            }

            is BlockClickEvolution -> {
                val block = evolution.requiredContext
                if (block is RegistryLikeTagCondition<Block>) requiredContextIdentifier = block.tag.location
                val itemName = "block.${requiredContextIdentifier?.toLanguageKey()}".asTranslated()
                val fullText = lang.rightClick.format( itemName.string).asTranslated()
                loreRequirements.add(fullText)
            }
        }

        requirements.forEach { requirement ->
            when (requirement) {
                is BiomeRequirement -> {
                    requirement.biomeCondition?.let { biome ->
                        val text = lang.biomeCondition.format(biome.toReadableLabel()).asTranslated()
                        loreRequirements.add(text)
                    }

                    requirement.biomeAnticondition?.let { biome ->
                        val text = lang.biomeAntiCondition.format(biome.toReadableLabel()).asTranslated()
                        loreRequirements.add(text)
                    }
                }
                is LevelRequirement -> {
                    val level = requirement.minLevel
                    val text = lang.level.format(level).text()
                    loreRequirements.add(text)
                }
                is HeldItemRequirement -> {
                    val itemName = requirement.itemCondition.toReadableLabel()
                    val text = lang.heldItem.format(itemName).asTranslated()
                    loreRequirements.add(text)
                }
                is FriendshipRequirement -> {
                    val friendshipAmount = requirement.amount
                    val text = lang.friendship.format(friendshipAmount).text()
                    loreRequirements.add(text)
                }
                is TimeRangeRequirement -> {
                    val range = requirement.range

                    fun areTimeRangesEquivalent(tr1: TimeRange, tr2: TimeRange): Boolean {
                        if (tr1.ranges.size != tr2.ranges.size) {
                            return false
                        }
                        return tr1.ranges == tr2.ranges
                    }

                    var nameDay: String? = null

                    for ((name, predefinedRange) in TimeRange.timeRanges) {
                        if (areTimeRangesEquivalent(range, predefinedRange)) {
                            nameDay = name
                            break
                        }
                    }

                    val text = lang.timeRange.format(nameDay).text()
                    loreRequirements.add(text)
                }
                is MoveSetRequirement -> {
                    val moveName = requirement.move.name
                    val text = lang.moveSet.format(moveName).text()
                    loreRequirements.add(text)
                }
                is MoveTypeRequirement -> {
                    val moveType = requirement.type.name
                    val text = lang.moveType.format(moveType).text()
                    loreRequirements.add(text)
                }
                is StatCompareRequirement -> {
                    val stat1 = requirement.lowStat.replaceFirstChar(Char::uppercaseChar)
                    val stat2 = requirement.highStat.replaceFirstChar(Char::uppercaseChar)
                    val text = lang.statCompare.format(stat1, stat2).text()
                    loreRequirements.add(text)
                }
                is StatEqualRequirement -> {
                    val stat1 = requirement.statOne.replaceFirstChar(Char::uppercaseChar)
                    val stat2 = requirement.statTwo.replaceFirstChar(Char::uppercaseChar)
                    val text = lang.statEqual.format(stat1, stat2).text()
                    loreRequirements.add(text)
                }
                is AttackDefenceRatioRequirement -> {
                    val ratio = requirement.ratio
                    val text = when (ratio) {
                        AttackDefenceRatioRequirement.AttackDefenceRatio.ATTACK_HIGHER -> lang.attackDefenceRatioAttackHigher.text()
                        AttackDefenceRatioRequirement.AttackDefenceRatio.DEFENCE_HIGHER -> lang.attackDefenceRatioDefenceHigher.text()
                        AttackDefenceRatioRequirement.AttackDefenceRatio.EQUAL -> lang.attackDefenceRatioEqual.text()
                    }

                    loreRequirements.add(text)
                }
                is UseMoveRequirement -> {
                    val move = requirement.move.name
                    val moveTimes = requirement.amount
                    val text = lang.useMove.format(move, moveTimes).text()
                    loreRequirements.add(text)
                }
                is RecoilRequirement -> {
                    val recoil = requirement.amount
                    val text = lang.recoil.format(recoil).text()
                    loreRequirements.add(text)
                }
                is DefeatRequirement -> {
                    val defeat = requirement.target.species
                    val text = lang.defeat.format(defeat).text()
                    loreRequirements.add(text)
                }
                is BlocksTraveledRequirement -> {
                    val amountTravelBlocks = requirement.amount
                    val text = lang.blocksTraveled.format(amountTravelBlocks).text()
                    loreRequirements.add(text)
                }
                is StructureRequirement -> {
                    requirement.structureCondition?.let { structure ->
                        val text = lang.structureCondition.format(structure.toReadableLabel()).asTranslated()
                        loreRequirements.add(text)
                    }

                    requirement.structureAnticondition?.let { structure ->
                        val text = lang.structureAntiCondition.format(structure.toReadableLabel()).asTranslated()
                        loreRequirements.add(text)
                    }
                }
                is AnyRequirement -> {
                    loreRequirements.add(lang.anyRequirement.text())
                }

                is MoonPhaseRequirement -> {
                    loreRequirements.add(lang.moonPhaseRequirement.format(requirement.moonPhase.toReadableLabel()).text())
                }

            }
        }

        if (loreRequirements.isEmpty()) {
            loreRequirements.add("No requirements".text())
        }

        return loreRequirements
    }
}
