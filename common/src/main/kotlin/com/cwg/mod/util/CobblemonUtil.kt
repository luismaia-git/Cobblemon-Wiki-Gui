package com.cwg.mod.util

import com.cobblemon.mod.common.CobblemonItems
import com.cobblemon.mod.common.api.conditional.RegistryLikeCondition
import com.cobblemon.mod.common.api.conditional.RegistryLikeTagCondition
import com.cobblemon.mod.common.api.drop.ItemDropEntry
import com.cobblemon.mod.common.api.pokemon.PokemonProperties
import com.cobblemon.mod.common.api.pokemon.PokemonSpecies
import com.cobblemon.mod.common.api.pokemon.evolution.Evolution
import com.cobblemon.mod.common.api.pokemon.stats.Stats
import com.cobblemon.mod.common.api.spawning.CobblemonSpawnPools
import com.cobblemon.mod.common.api.spawning.detail.PokemonSpawnDetail
import com.cobblemon.mod.common.api.tags.CobblemonItemTags
import com.cobblemon.mod.common.api.text.aqua
import com.cobblemon.mod.common.api.text.bold
import com.cobblemon.mod.common.api.text.gray
import com.cobblemon.mod.common.api.text.plus
import com.cobblemon.mod.common.api.text.text
import com.cobblemon.mod.common.api.text.yellow
import com.cobblemon.mod.common.api.types.ElementalTypes
import com.cobblemon.mod.common.pokemon.Species
import com.cobblemon.mod.common.pokemon.evolution.requirements.AnyRequirement
import com.cobblemon.mod.common.pokemon.evolution.requirements.AttackDefenceRatioRequirement
import com.cobblemon.mod.common.pokemon.evolution.requirements.BiomeRequirement
import com.cobblemon.mod.common.pokemon.evolution.requirements.BlocksTraveledRequirement
import com.cobblemon.mod.common.pokemon.evolution.requirements.DefeatRequirement
import com.cobblemon.mod.common.pokemon.evolution.requirements.FriendshipRequirement
import com.cobblemon.mod.common.pokemon.evolution.requirements.HeldItemRequirement
import com.cobblemon.mod.common.pokemon.evolution.requirements.LevelRequirement
import com.cobblemon.mod.common.pokemon.evolution.requirements.MoveSetRequirement
import com.cobblemon.mod.common.pokemon.evolution.requirements.MoveTypeRequirement
import com.cobblemon.mod.common.pokemon.evolution.requirements.RecoilRequirement
import com.cobblemon.mod.common.pokemon.evolution.requirements.StatCompareRequirement
import com.cobblemon.mod.common.pokemon.evolution.requirements.StatEqualRequirement
import com.cobblemon.mod.common.pokemon.evolution.requirements.StructureRequirement
import com.cobblemon.mod.common.pokemon.evolution.requirements.TimeRangeRequirement
import com.cobblemon.mod.common.pokemon.evolution.requirements.UseMoveRequirement
import com.cobblemon.mod.common.pokemon.evolution.variants.BlockClickEvolution
import com.cobblemon.mod.common.pokemon.evolution.variants.ItemInteractionEvolution
import com.cobblemon.mod.common.pokemon.evolution.variants.TradeEvolution
import com.cobblemon.mod.common.registry.ItemIdentifierCondition
import com.cobblemon.mod.common.util.asTranslated
import com.cwg.mod.CobblemonWikiGui
import net.minecraft.ChatFormatting
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.network.chat.Component
import net.minecraft.network.chat.MutableComponent
import net.minecraft.network.chat.Style
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.item.Item
import net.minecraft.world.item.ItemStack
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.Block
import kotlin.math.roundToInt


object CobblemonUtil {

    private val darkAqua = Style.EMPTY.withColor(0x00AAAA)
    private val darkPurple = Style.EMPTY.withColor(0xAA00AA)
    private val gold = Style.EMPTY.withColor(0xFFAA00)
    private val red = Style.EMPTY.withColor(0xFF5555)
    private val lightPurple = Style.EMPTY.withColor(0xFF55FF)
    private val yellow = Style.EMPTY.withColor(0xFFFF55)
    private val lang = CobblemonWikiGui.langConfig

    private fun toWikiGui(payload: MutableComponent): MutableList<Component> {
        val lore: MutableList<Component> = ArrayList()
        lore.add(payload)
        return lore
    }

    private fun toWikiGui(mutableCollection: MutableList<Component>): MutableList<Component> {
        return mutableCollection
    }

   private fun getType(pokemon: Species): MutableComponent {
        return pokemon.types.map { it.displayName.copy() }.reduce { acc, next -> acc.plus("/").plus(next) }
    }

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

                val translation =  tradePokemon.species?.let { speciesName ->
                    val tradeSpecies = PokemonSpecies.getByName(speciesName)
                    if (tradeSpecies != null)
                        lang.tradeSpecific.format(tradeSpecies.translatedName.string, linkCableTranslatedComponent.string).asTranslated()
                        lang.tradeSpecific.format(speciesName, linkCableTranslatedComponent.string).asTranslated()
                } ?: lang.tradeAny.format(linkCableTranslatedComponent.string).asTranslated()

                loreRequirements.add(translation)
            }
            is ItemInteractionEvolution -> {

                val item: RegistryLikeCondition<Item> = evolution.requiredContext.item

                if (item is ItemIdentifierCondition) requiredContextIdentifier = item.identifier


                val itemName = "item.${requiredContextIdentifier?.toLanguageKey()}".asTranslated()

                val text = lang.useItem.format(itemName.string).asTranslated()

                loreRequirements.add(text)
            }

            is BlockClickEvolution -> {
                val block = evolution.requiredContext
                if (block is RegistryLikeTagCondition<Block>) requiredContextIdentifier = block.tag.location
                val itemName = "block.${requiredContextIdentifier?.toLanguageKey()}".asTranslated()
                val fullText = lang.rightClick.format( itemName.string).asTranslated()
                loreRequirements.add(fullText);
            }
        }

        requirements.forEach { requirement ->
            when (requirement) {
                is BiomeRequirement -> {
                    requirement.biomeCondition?.let { biome ->
                        val biomeId = biome.toString()
                        val text = lang.biomeCondition.format(biomeId.asTranslated()).asTranslated()
                        loreRequirements.add(text)
                    }

                    requirement.biomeAnticondition?.let { biome ->
                        val biomeId = biome.toString()
                        val text = lang.biomeCondition.format(biomeId.asTranslated()).asTranslated()
                        loreRequirements.add(text)
                    }
                }
                is LevelRequirement -> {
                    val level = requirement.minLevel
                    val text = lang.level.format(level).text()
                    loreRequirements.add(text)
                }
                is HeldItemRequirement -> {
                    val itemCondition = requirement.itemCondition.item
                    var identifier: ResourceLocation? = null;
                    if (itemCondition is ItemIdentifierCondition) identifier = itemCondition.identifier

                    val itemName = "item.${identifier?.toLanguageKey()}".asTranslated()
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
                    val text = lang.timeRange.format(range).text()
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

                    requirement.structureCondition.let { it ->
                        val structure = it.toString()
                        val text = lang.structureCondition.format(structure.asTranslated()).text()
                        loreRequirements.add(text)
                    }

                    requirement.structureAnticondition.let { it ->
                        val structure = it.toString()
                        val text = lang.structureAntiCondition.format(structure.asTranslated()).text()
                        loreRequirements.add(text)
                    }

                }
                is AnyRequirement -> {
                    loreRequirements.add(lang.anyRequirement.text())
                }

            }
        }

        if (loreRequirements.isEmpty()) {
            loreRequirements.add("No requirements".text())
        }

        return loreRequirements
    }

    private fun getCatchRate(pokemon: Species): MutableComponent {
        val baseRate: String = (pokemon.catchRate / 255.0 * 100.0).roundToInt().toString()
        return "$baseRate%".aqua()
    }

    fun getTypeToWikiGui(pokemon: Species): MutableList<Component> {
        return toWikiGui(getType(pokemon).yellow())
    }

    fun getBaseStatsToWikiGui(pokemon: Species): MutableList<Component> {
        val lore: MutableList<Component> = ArrayList()

        val initialString = " §b- §a"
        lore.add(
            Component.translatable("cobblemon.ui.stats.hp").setStyle(lightPurple)
                .append(
                    initialString +
                            (pokemon.baseStats[Stats.HP])

                )
        )
        lore.add(
            Component.translatable("cobblemon.ui.stats.atk").setStyle(red)
                .append(
                    initialString + (pokemon.baseStats[Stats.ATTACK])
                )
        )
        lore.add(
            Component.translatable("cobblemon.ui.stats.def").setStyle(gold)
                .append(initialString + (pokemon.baseStats[Stats.DEFENCE]))
        )
        lore.add(
            Component.translatable("cobblemon.ui.stats.sp_atk").setStyle(darkPurple)
                .append(initialString + (pokemon.baseStats[Stats.SPECIAL_ATTACK]))
        )
        lore.add(
            Component.translatable("cobblemon.ui.stats.sp_def").setStyle(yellow)
                .append(initialString + (pokemon.baseStats[Stats.SPECIAL_DEFENCE]))
        )
        lore.add(
            Component.translatable("cobblemon.ui.stats.speed").setStyle(darkAqua)
                .append(initialString + (pokemon.baseStats[Stats.SPEED]))
        )
        lore.add(getBaseFriendship(pokemon))
        return lore
    }

    fun getCatchRateToWikiGui(pokemon: Species): MutableList<Component> {
        val lore : MutableList<Component> = ArrayList()
        lore.add(getCatchRate(pokemon))
        return toWikiGui(lore)
    }

    private fun getSpawnDetails(species: Species) : List<PokemonSpawnDetail> {

        val cobblemonSpawnPool = CobblemonSpawnPools.WORLD_SPAWN_POOL

        val spawnDetails = cobblemonSpawnPool
            .filterIsInstance<PokemonSpawnDetail>()
            .filter {
                it.pokemon.species != null &&
                        it.pokemon.species == species.resourceIdentifier.path

            }

        return spawnDetails
    }

    fun getSpawnTime(species: Species): MutableList<Component> {
        val dayCycleMap = mapOf(
            "Any time" to listOf(0..23999),
            "Day" to listOf(23460 downTo 12541),
            "Night" to listOf(12542..23459),
            "Noon" to listOf(5000..6999),
            "Midnight" to listOf(17000..18999),
            "Dawn" to listOf(22300 downTo 166),
            "Dusk" to listOf(11834..13701),
            "Twilight" to listOf(11834..13701, 22300 downTo 166),
            "Morning" to listOf(0..4999),
            "Afternoon" to listOf(7000..12039),
            "Any time" to listOf(0..23999),
        )

        val spawnDetailsList = getSpawnDetails(species)

        val timeRanges = mutableListOf<IntRange>()

        spawnDetailsList.forEach { spawnDetails ->
            spawnDetails.conditions.forEach { condition ->
                condition.timeRange?.ranges?.let { ranges ->
                    timeRanges.addAll(ranges)
                }
            }
        }

        val matchingCycles = mutableListOf<Component>()

        for ((cycleName, cycleRanges) in dayCycleMap) {
            if (cycleRanges.any { range -> timeRanges.any { it == range } }) {
                matchingCycles.add(cycleName.text().yellow())
            }
        }
        if (matchingCycles.isEmpty()) {
            matchingCycles.add(Component.literal("Any time").yellow())
        }
        return matchingCycles
    }

    private fun getSpawnBiomes(species: Species, world: Level): MutableList<Component> {
        val biomes = BiomeUtils.getAllBiomes(world)
        val validBiomes = biomes.filter { biome ->
            getSpawnDetails(species).any { s ->
                s.conditions.any { c ->
                    BiomeUtils.canSpawnAt(biome.biome, world, c)
                }
            }
        }
        val lore: MutableList<Component> = ArrayList()
        var component: MutableComponent = "".text()
        var i = 0

        validBiomes.forEach { biome ->
            if (i > 0 && i.mod(3) == 0) {
                lore.add(component)
                component = "".text()
            }

            if (i.mod(3) != 0) {
                component.append(" / ".gray())
            }

            component.append("biome.${biome.identifier.toLanguageKey()}".asTranslated().yellow())

            i += 1
        }

        if (component.contents.toString().isNotEmpty()) {
            lore.add(component)
        }

        return lore
    }

    fun getSpawnBiomesToWikiGui(species: Species, world: Level): MutableList<Component> {
        return toWikiGui(getSpawnBiomes(species, world))
    }

    private fun getMovesByLevel(species : Species): MutableList<Component> {
        val lore: MutableList<Component> = ArrayList()
        var i = 0
        var component = "".text()
        for ((level, moves) in species.moves.levelUpMoves) {
            for (move in moves) {

                if (i > 0 && i.mod(3) == 0) {
                    lore.add(component)
                    component = "".text()
                }

                if (i.mod(3) != 0) {
                    component.append(" / ".gray())
                }

                component.append(
                    Component.literal(level.toString()).withStyle(ChatFormatting.YELLOW)
                    .append(
                        Component.literal(" : ")
                            .withStyle(ChatFormatting.RESET)
                    )
                    .append(move.displayName.copy().withStyle(ChatFormatting.AQUA)))
                i += 1
            }

        }
            if (component.contents.toString().isNotEmpty()) {
                lore.add(component)
            }
            return lore
        }


    fun getMovesByLevelToWikiGui(species: Species): MutableList<Component> {
        return getMovesByLevel(species)
    }


    fun getTmMoves(species: Species): MutableList<Component> {
        val lore: MutableList<Component> = ArrayList()
        var i = 0
        var component = "".text()

        for (move in species.moves.tmMoves) {

            if (i > 0 && i.mod(4) == 0) {
                lore.add(component)
                component = "".text()
            }

            if (i.mod(4) != 0) {
                component.append(" / ".yellow())
            }

            component.append(move.displayName.copy().withStyle(ChatFormatting.AQUA))
            i+=1

        }
        if (component.contents.toString().isNotEmpty()) {
            lore.add(component)
        }

        return lore

    }

    fun getTutorMoves(species: Species): MutableList<Component> {
        val lore: MutableList<Component> = ArrayList()
        var i = 0
        var component = "".text()
        for (move in species.moves.tutorMoves) {

            if (i > 0 && i.mod(4) == 0) {
                lore.add(component)
                component = "".text()
            }

            if (i.mod(4) != 0) {
                component.append(" / ".yellow())
            }

            component.append( move.displayName.copy().withStyle(ChatFormatting.AQUA) )
            i+=1
        }
        return lore
    }

    fun getEvolutionMoves(species: Species): MutableList<Component> {
        val payload: MutableList<Component> = ArrayList()

        for (move in species.moves.evolutionMoves) {
            val component = move.displayName.copy().withStyle(ChatFormatting.AQUA)
            payload.add(component)
        }

        return payload
    }

    fun getEggMoves(species: Species): MutableList<Component> {
        val payload: MutableList<Component> = ArrayList()

        for (move in species.moves.eggMoves) {
            val component = move.displayName.copy().withStyle(ChatFormatting.AQUA)
            payload.add(component)
        }

        return payload
    }

    fun getFormChangeMoves(species: Species): MutableList<Component> {
        val payload: MutableList<Component> = ArrayList()

        for (move in species.moves.formChangeMoves) {
            val component = move.displayName.copy().withStyle(ChatFormatting.AQUA)
            payload.add(component)
        }

        return payload
    }

    fun getAbilities(species: Species): MutableList<Component> {
        val payload: MutableList<Component> = ArrayList()
        species.abilities.forEach {
            payload.add(it.template.displayName.yellow())
        }
        return payload
    }

    private fun getBaseFriendship(species: Species): MutableComponent {
        return lang.baseFriendship.text().aqua().append(" - ").append(species.baseFriendship.toString().yellow())
    }

    fun getDrops(species: Species): MutableList<Component> {
        val payload: MutableList<Component> = ArrayList()

        val drops = species.drops.entries.filterIsInstance<ItemDropEntry>()
        drops.forEach { payload.add("item.${it.item.toLanguageKey()}".asTranslated().aqua().append(Component.literal(" §e"+ it.percentage.toString()+ "%")))}

        if(payload.isEmpty()){
            payload.add(lang.noDrops.yellow())
            return payload
        }
        return payload
    }

    fun getEggGroups(species: Species): MutableList<Component> {
        val payload: MutableList<Component> = ArrayList()
        species.eggGroups.forEach {
            payload.add(it.name.yellow())
        }
        return payload
    }

    fun getDynamax(species: Species): MutableList<Component> {
        val payload: MutableList<Component> = ArrayList()
        val textFormated = if (species.canGmax())  "Yes".yellow() else "Not".yellow()
        payload.add(textFormated)
        return payload
    }

    fun getForms(species: Species): MutableList<Component> {
        val payload: MutableList<Component> = ArrayList()
        species.forms.forEach{
            payload.add(it.name.yellow())
        }
        return payload
    }

    fun getEffectiveness(species: Species): MutableList<Component> {
        val lore: MutableList<Component> = ArrayList()

        val weaknessList = ElementalTypes.all().map { t ->
            t to TypeChart.getEffectiveness(t, species.types)
        }.filter { (_, effectiveness) ->
            effectiveness > 0
        }

        val resistantList = ElementalTypes.all().map { t ->
            t to TypeChart.getEffectiveness(t, species.types)
        }.filter { (_, effectiveness) ->
            effectiveness < 0
        }

        val immuneList = ElementalTypes.all().map { t ->
            t to TypeChart.getImmunity(t, species.types)
        }.filter { (_, isImmune) ->
            !isImmune
        }

        if (weaknessList.isNotEmpty()) {
            val component = lang.weakness.text()
            for (elementalType in weaknessList) {
                component.append(" ".text())
                component.append(
                    elementalType.first.displayName.setStyle(
                        Style.EMPTY
                            .withBold(true)
                            .withColor(elementalType.first.hue)
                    )
                )
            }
            lore.add(component)
        }

        if (resistantList.isNotEmpty()) {
            val component = lang.resistant.text()
            for (elementalType in resistantList) {
                component.append(" ".text())
                component.append(
                    elementalType.first.displayName.setStyle(
                        Style.EMPTY
                            .withBold(true)
                            .withColor(elementalType.first.hue)
                    )
                )
            }
            lore.add(component)
        }

        if (immuneList.isNotEmpty()) {
            val component = lang.immune.text()
            for (elementalType in immuneList) {
                component.append(" ".text())
                component.append(
                    elementalType.first.displayName.setStyle(
                        Style.EMPTY
                            .withBold(true)
                            .withColor(elementalType.first.hue)
                    )
                )
            }
            lore.add(component)

        }
        return lore
    }

}
