package com.cwg.mod.util

import com.cobblemon.mod.common.CobblemonItems
import com.cobblemon.mod.common.api.conditional.RegistryLikeTagCondition
import com.cobblemon.mod.common.api.drop.ItemDropEntry
import com.cobblemon.mod.common.api.pokemon.PokemonProperties
import com.cobblemon.mod.common.api.pokemon.PokemonSpecies
import com.cobblemon.mod.common.api.pokemon.evolution.Evolution
import com.cobblemon.mod.common.api.pokemon.stats.Stats
import com.cobblemon.mod.common.api.spawning.CobblemonSpawnPools
import com.cobblemon.mod.common.api.spawning.TimeRange
import com.cobblemon.mod.common.api.spawning.condition.MoonPhase
import com.cobblemon.mod.common.api.spawning.condition.SpawningCondition
import com.cobblemon.mod.common.api.spawning.detail.PokemonSpawnDetail
import com.cobblemon.mod.common.api.text.*
import com.cobblemon.mod.common.api.types.ElementalTypes
import com.cobblemon.mod.common.pokemon.FormData
import com.cobblemon.mod.common.pokemon.evolution.variants.BlockClickEvolution
import com.cobblemon.mod.common.pokemon.evolution.variants.ItemInteractionEvolution
import com.cobblemon.mod.common.pokemon.evolution.variants.TradeEvolution
import com.cobblemon.mod.common.pokemon.requirements.*
import com.cobblemon.mod.common.registry.*
import com.cobblemon.mod.common.util.asTranslated
import com.cwg.mod.CobblemonWikiGui
import net.minecraft.ChatFormatting
import net.minecraft.network.chat.Component
import net.minecraft.network.chat.MutableComponent
import net.minecraft.network.chat.Style
import net.minecraft.network.chat.TextColor
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.level.block.Block
import kotlin.math.roundToInt


object CobblemonUtil {

    private val darkAqua = Style.EMPTY.withColor(TextColor.fromRgb(0x00AAAA)).withItalic(false)
    private val darkPurple = Style.EMPTY.withColor(TextColor.fromRgb(0xAA00AA)).withItalic(false)
    private val gold = Style.EMPTY.withColor(TextColor.fromRgb(0xFFAA00)).withItalic(false)
    private val red = Style.EMPTY.withColor(TextColor.fromRgb(0xFF5555)).withItalic(false)
    private val lightPurple = Style.EMPTY.withColor(TextColor.fromRgb(0xFF55FF)).withItalic(false)
    private val yellow = Style.EMPTY.withColor(TextColor.fromRgb(0xFFFF55)).withItalic(false)
    private val lang = CobblemonWikiGui.langConfig

    private fun toWikiGui(payload: MutableComponent): MutableList<Component> {
        val lore: MutableList<Component> = ArrayList()
        lore.add(payload)
        return lore
    }

    private fun toWikiGui(mutableCollection: MutableList<Component>): MutableList<Component> {
        return mutableCollection
    }

   private fun getType(pokemon: FormData): MutableComponent {
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
               // todo this
//                val itemName = "item.${requiredContextIdentifier?.toLanguageKey()}".asTranslated()
//
//                val text = lang.useItem.format(itemName.string).asTranslated()
//
//                loreRequirements.add(text)
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
                        var biomeCondition : ResourceLocation? = null
                        if(biome is BiomeTagCondition) biomeCondition = biome.tag.location
                        val biomeName = "${biomeCondition?.toLanguageKey()}".asTranslated()

                        val text = lang.biomeCondition.format(biomeName.string).asTranslated()
                        loreRequirements.add(text)
                    }

                    requirement.biomeAnticondition?.let { biome ->
                        var biomeCondition : ResourceLocation? = null
                        if(biome is BiomeTagCondition) biomeCondition = biome.tag.location
                        val biomeName = "${biomeCondition?.toLanguageKey()}".asTranslated()
                        val text = lang.biomeAntiCondition.format(biomeName.string).asTranslated()
                        loreRequirements.add(text)
                    }
                }
                is LevelRequirement -> {
                    val level = requirement.minLevel
                    val text = lang.level.format(level).text()
                    loreRequirements.add(text)
                }
                is HeldItemRequirement -> { // todo this
//                    val itemCondition = requirement.itemCondition.
//                    var identifier: ResourceLocation? = null;
//                    if (itemCondition is ItemIdentifierCondition) identifier = itemCondition.identifier
//
//                    val itemName = "item.${identifier?.toLanguageKey()}".asTranslated()
//                    val text = lang.heldItem.format(itemName.string).asTranslated()
//                    loreRequirements.add(text)
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

                    requirement.structureCondition.let { it ->

                        val structure = it?.toString()

                        val text = lang.structureCondition.format(structure?.asTranslated()).asTranslated()
                        loreRequirements.add(text)
                    }

                    requirement.structureAnticondition.let { it ->
                        val structure = it.toString()
                        val text = lang.structureAntiCondition.format(structure.asTranslated()).asTranslated()
                        loreRequirements.add(text)
                    }

                }
                is AnyRequirement -> {
                    loreRequirements.add(lang.anyRequirement.text())
                }

                is MoonPhaseRequirement -> {
                    val req = when (requirement.moonPhase) {
                        MoonPhase.FULL_MOON -> "Moon Phase: Full Moon"
                        MoonPhase.WANING_GIBBOUS -> "Moon Phase: Waning Gibbous"
                        MoonPhase.THIRD_QUARTER -> "Moon Phase: Third Quarter"
                        MoonPhase.WANING_CRESCENT -> "Moon Phase: Waning Crescent"
                        MoonPhase.NEW_MOON -> "Moon Phase: New Moon"
                        MoonPhase.WAXING_CRESCENT -> "Moon Phase: Waxing Crescent"
                        MoonPhase.FIRST_QUARTER -> "Moon Phase: First Quarter"
                        MoonPhase.WAXING_GIBBOUS -> "Moon Phase: Waxing Gibbous"
                    }

                    loreRequirements.add(req.asTranslated())
                }

            }
        }

        if (loreRequirements.isEmpty()) {
            loreRequirements.add("No requirements".text())
        }

        return loreRequirements
    }

    private fun getCatchRate(pokemon: FormData): MutableComponent {
        val baseRateDouble: Double = (pokemon.catchRate / 255.0) * 100.0
        val baseRate: String = baseRateDouble.roundToInt().toString()
        return "$baseRate%".aqua()
    }

    fun getTypeToWikiGui(pokemon: FormData): MutableList<Component> {
        return toWikiGui(getType(pokemon).yellow())
    }

    fun getBaseStatsToWikiGui(pokemon: FormData): MutableList<Component> {
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

    fun getCatchRateToWikiGui(pokemon: FormData): MutableList<Component> {
        val lore : MutableList<Component> = ArrayList()
        lore.add(getCatchRate(pokemon))
        return toWikiGui(lore)
    }

    fun getSpawnDetails(formData: FormData) : List<PokemonSpawnDetail> {

        val cobblemonSpawnPool = CobblemonSpawnPools.WORLD_SPAWN_POOL

        val spawnDetails = cobblemonSpawnPool
            .filterIsInstance<PokemonSpawnDetail>()
            .filter {
                it.pokemon.species != null &&
                        it.pokemon.species == formData.species.resourceIdentifier.path

            }

        return spawnDetails
    }

    fun getSpawnTime(conditions: MutableList<SpawningCondition<*>>): MutableList<Component> {
        val timeRanges = mutableListOf<IntRange>()

        conditions.forEach { condition ->
            condition.timeRange?.ranges?.let { ranges ->
                timeRanges.addAll(ranges)
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

    private fun SpawningCondition<*>.toLoreLines(prefix: String = "- "): List<String> =
        buildList {
            biomes?.takeIf { it.isNotEmpty() }?.let { set ->
                add(
                    "${prefix}Biomes: ${
                        set.joinToString { cond ->
                            when (cond) {
                                is BiomeTagCondition -> "#${cond.tag.location}"
                                is FluidTagCondition -> "[Fluid] #${cond.tag.location}"
                                is ItemTagCondition -> "[Item] #${cond.tag.location}"
                                is StructureTagCondition -> "[Structure] #${cond.tag.location}"

                                is BiomeIdentifierCondition -> "#${cond.identifier}"
                                is FluidIdentifierCondition -> "[Fluid] #${cond.identifier}"
                                is ItemIdentifierCondition -> "[Item] #${cond.identifier}"
                                is StructureIdentifierCondition -> "[Structure] #${cond.identifier}"
                                
                                else -> "[Unknown] $cond"
                            }
                        }
                    }"
                )
            }
            moonPhase?.let { add("${prefix}Moon Phase: $it") }
            canSeeSky?.let { add("${prefix}Can See Sky: $it") }

            val axes = buildList<String> {

                val xRange = when {
                    minX != null && maxX != null -> "$minX-$maxX"
                    minX != null -> "$minX+"
                    maxX != null -> "0-$maxX"
                    else -> null
                }
                xRange?.let { add("X[$it]") }

                val yRange = when {
                    minY != null && maxY != null -> "$minY-$maxY"
                    minY != null -> "$minY+"
                    maxY != null -> "0-$maxY"
                    else -> null
                }
                yRange?.let { add("Y[$it]") }

                val zRange = when {
                    minZ != null && maxZ != null -> "$minZ-$maxZ"
                    minZ != null -> "$minZ+"
                    maxZ != null -> "0-$maxZ"
                    else -> null
                }
                zRange?.let { add("Z[$it]") }
            }

            if (axes.isNotEmpty()) {
                add("${prefix}Area: ${axes.joinToString(", ")}")
            }

            val lightRange = when {
                minLight != null && maxLight != null -> "$minLight-$maxLight"
                minLight != null -> "$minLight+"
                maxLight != null -> "0-$maxLight"
                else -> null
            }

            lightRange?.let { add("${prefix}Light: $it") }

            val skyLightRange = when {
                minSkyLight != null && maxSkyLight != null -> "$minSkyLight-$maxSkyLight"
                minSkyLight != null -> "$minSkyLight+"
                maxSkyLight != null -> "0-$maxSkyLight"
                else -> null
            }
            skyLightRange?.let { add("${prefix}Sky Light: $it") }

            if (isRaining == true) add("${prefix}Raining")
            if (isThundering == true) add("${prefix}Thundering")
            if (isSlimeChunk == true) add("${prefix}Slime Chunk")

            structures?.takeIf { it.isNotEmpty() }?.let { set ->
                val names = set.joinToString { either ->
                    either.map(
                        { it.namespace + ":" + it.path },
                        { it.location.namespace + ":" + it.location.path }
                    )
                }

                add("${prefix}Structures: $names")
            }

            markers?.takeIf { it.isNotEmpty() }
                ?.let { add("${prefix}Markers: ${it.joinToString()}") }
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
                lines.forEach { lore.add(it.yellow()) }
            }
        }
    }

    private fun getMovesByLevel(species : FormData): MutableList<Component> {
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


    fun getMovesByLevelToWikiGui(species: FormData): MutableList<Component> {
        return getMovesByLevel(species)
    }


    fun getTmMoves(species: FormData): MutableList<Component> {
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

    fun getTutorMoves(species: FormData): MutableList<Component> {
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

    fun getEvolutionMoves(species: FormData): MutableList<Component> {
        val payload: MutableList<Component> = ArrayList()

        for (move in species.moves.evolutionMoves) {
            val component = move.displayName.copy().withStyle(ChatFormatting.AQUA)
            payload.add(component)
        }

        return payload
    }

    fun getEggMoves(species: FormData): MutableList<Component> {
        val payload: MutableList<Component> = ArrayList()

        for (move in species.moves.eggMoves) {
            val component = move.displayName.copy().withStyle(ChatFormatting.AQUA)
            payload.add(component)
        }

        return payload
    }

    fun getFormChangeMoves(species: FormData): MutableList<Component> {
        val payload: MutableList<Component> = ArrayList()

        for (move in species.moves.formChangeMoves) {
            val component = move.displayName.copy().withStyle(ChatFormatting.AQUA)
            payload.add(component)
        }

        return payload
    }

    fun getAbilities(species: FormData): MutableList<Component> {
        val payload: MutableList<Component> = ArrayList()
        species.abilities.forEach {
            payload.add(it.template.displayName.yellow())
        }
        return payload
    }

    private fun getBaseFriendship(species: FormData): MutableComponent {
        return lang.baseFriendship.text().aqua().append(" - ").append(species.baseFriendship.toString().yellow())
    }

    fun getDrops(species: FormData): MutableList<Component> {
        val payload: MutableList<Component> = ArrayList()

        val drops = species.drops.entries.filterIsInstance<ItemDropEntry>()
        drops.forEach { payload.add("item.${it.item.toLanguageKey()}".asTranslated().aqua().append(Component.literal(" §e"+ it.percentage.toString()+ "%")))}

        if(payload.isEmpty()){
            payload.add(lang.noDrops.yellow())
            return payload
        }
        return payload
    }

    fun getEggGroups(species: FormData): MutableList<Component> {
        val payload: MutableList<Component> = ArrayList()
        species.eggGroups.forEach {
            payload.add(it.name.yellow())
        }
        return payload
    }

    fun getDynamax(formData: FormData): MutableList<Component> {
        val payload: MutableList<Component> = ArrayList()
        val textFormated = if (formData.species.canGmax())  "Yes".yellow() else "Not".yellow()
        payload.add(textFormated)
        return payload
    }

    fun getForms(form: FormData): MutableList<Component> {
        val payload: MutableList<Component> = ArrayList()
        form.species.forms.forEach{
            payload.add(it.name.yellow())
        }
        return payload
    }

    fun getEffectiveness(species: FormData): MutableList<Component> {
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
