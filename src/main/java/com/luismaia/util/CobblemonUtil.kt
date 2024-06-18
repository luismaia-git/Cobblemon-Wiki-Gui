package com.luismaia.util

import com.cobblemon.mod.common.api.drop.ItemDropEntry
import com.cobblemon.mod.common.api.pokemon.evolution.Evolution
import com.cobblemon.mod.common.api.pokemon.stats.Stats
import com.cobblemon.mod.common.api.spawning.CobblemonSpawnPools
import com.cobblemon.mod.common.api.spawning.detail.PokemonSpawnDetail
import com.cobblemon.mod.common.api.spawning.detail.SpawnDetail
import com.cobblemon.mod.common.api.text.aqua
import com.cobblemon.mod.common.api.text.plus
import com.cobblemon.mod.common.api.text.text
import com.cobblemon.mod.common.api.text.yellow
import com.cobblemon.mod.common.api.types.ElementalTypes
import com.cobblemon.mod.common.pokemon.Species
import com.cobblemon.mod.common.util.asTranslated
import com.luismaia.CobblemonWikiGui
import com.luismaia.config.CobblemonWikiGuiConfig

import net.minecraft.text.MutableText
import net.minecraft.text.Style
import net.minecraft.text.Text
import net.minecraft.text.TextColor
import net.minecraft.util.Formatting
import net.minecraft.world.World
import kotlin.math.roundToInt


object CobblemonUtil {

    private val darkAqua = Style.EMPTY.withColor(TextColor.parse("dark_aqua"))
    private val darkPurple = Style.EMPTY.withColor(TextColor.parse("dark_purple"))
    private val gold = Style.EMPTY.withColor(TextColor.parse("gold"))
    private val red = Style.EMPTY.withColor(TextColor.parse("red"))
    private val lightPurple = Style.EMPTY.withColor(TextColor.parse("light_purple"))
    private val yellow = Style.EMPTY.withColor(TextColor.parse("yellow"))

    private fun toWikiGui(payload: MutableText): MutableList<Text> {
        val lore: MutableList<Text> = ArrayList()
        lore.add(payload)
        return lore
    }

    private fun toWikiGui(mutableCollection: MutableList<Text>): MutableList<Text> {
        return mutableCollection
    }

   private fun getType(pokemon: Species): MutableText {
        return pokemon.types.map { it.displayName.copy() }.reduce { acc, next -> acc.plus("/").plus(next) }
    }

    private fun getEvolutions(pokemon: Species): MutableText {
        if(pokemon.evolutions.isEmpty()){
            return "X".text()
        }
        val payload = pokemon.evolutions.map { it: Evolution ->
            it.result.species!!.split(' ')
            .joinToString(" ") { it.replaceFirstChar(Char::uppercaseChar) } }.reduce { acc, next -> acc.plus("/").plus(next) }

        return payload.aqua()
    }

    private fun getCatchRate(pokemon: Species): Text {
        val baseRate: String = (pokemon.catchRate / 255.0 * 100.0).roundToInt().toString()
        return "$baseRate%".aqua()
    }

    fun getTypeToWikiGui(pokemon: Species): MutableList<Text> {
        return toWikiGui(getType(pokemon).yellow())
    }

    fun getBaseStatsToWikiGui(pokemon: Species): MutableList<Text> {
        val lore: MutableList<Text> = ArrayList()

        val initialString = " §b- §a"
        lore.add(
            Text.translatable("cobblemon.ui.stats.hp").setStyle(lightPurple)
                .append(
                    initialString +
                            (pokemon.baseStats[Stats.HP])

                )
        )
        lore.add(
            Text.translatable("cobblemon.ui.stats.atk").setStyle(red)
                .append(
                    initialString + (pokemon.baseStats[Stats.ATTACK])
                )
        )
        lore.add(
            Text.translatable("cobblemon.ui.stats.def").setStyle(gold)
                .append(initialString + (pokemon.baseStats[Stats.DEFENCE]))
        )
        lore.add(
            Text.translatable("cobblemon.ui.stats.sp_atk").setStyle(darkPurple)
                .append(initialString + (pokemon.baseStats[Stats.SPECIAL_ATTACK]))
        )
        lore.add(
            Text.translatable("cobblemon.ui.stats.sp_def").setStyle(yellow)
                .append(initialString + (pokemon.baseStats[Stats.SPECIAL_DEFENCE]))
        )
        lore.add(
            Text.translatable("cobblemon.ui.stats.speed").setStyle(darkAqua)
                .append(initialString + (pokemon.baseStats[Stats.SPEED]))
        )
        lore.add(getBaseFriendship(pokemon))
        return lore
    }

    fun getCatchRateToWikiGui(pokemon: Species): MutableList<Text> {
        val lore : MutableList<Text> = ArrayList()
        lore.add(getCatchRate(pokemon))
        return toWikiGui(lore)
    }

    fun getEvolutionsToWikiGui(pokemon: Species): MutableList<Text> {
        return toWikiGui(getEvolutions(pokemon))
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

    fun getSpawnTime(species: Species): MutableList<Text> {
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

        val matchingCycles = mutableListOf<Text>()

        for ((cycleName, cycleRanges) in dayCycleMap) {
            if (cycleRanges.any { range -> timeRanges.any { it == range } }) {
                matchingCycles.add(cycleName.text().yellow())
            }
        }
        if (matchingCycles.isEmpty()) {
            matchingCycles.add(Text.literal("Any time").yellow())
        }
        return matchingCycles
    }

    private fun getSpawnBiomes(species: Species, world: World): MutableList<Text> {
        val biomes = BiomeUtils.getAllBiomes(world)
        val validBiomes = biomes.filter { biome ->
            getSpawnDetails(species).any { s ->
                s.conditions.any { c ->
                    BiomeUtils.canSpawnAt(biome.biome, world, c)
                }
            }
        }
        val lore: MutableList<Text> = ArrayList()
        var formatedText: MutableText = "".text()
        var i = 0

        validBiomes.forEach { biome ->
            if (i > 0 && i.mod(3) == 0) {
                lore.add(formatedText)
                formatedText = "".text()
            }

            if (i.mod(3) != 0) {
                formatedText.append(" / ".aqua())
            }

            formatedText.append("biome.${biome.identifier.toTranslationKey()}".asTranslated().yellow())

            i += 1
        }

        if (formatedText.content.toString().isNotEmpty()) {
            lore.add(formatedText)
        }

        return lore
    }

    fun getSpawnBiomesToWikiGui(species: Species, world: World): MutableList<Text> {
        return toWikiGui(getSpawnBiomes(species, world))
    }

    private fun getMovesByLevel(species : Species): MutableList<Text> {
        val lore: MutableList<Text> = ArrayList()
        var i = 0
        var formattedText = "".text()
        for ((level, moves) in species.moves.levelUpMoves) {
            for (move in moves) {

                if (i > 0 && i.mod(3) == 0) {
                    lore.add(formattedText)
                    formattedText = "".text()
                }

                if (i.mod(3) != 0) {
                    formattedText.append(" / ".yellow())
                }

                formattedText.append(
                    Text.literal(level.toString())
                        .formatted(Formatting.YELLOW)
                    .append(
                        Text.literal(" : ")
                            .formatted(Formatting.RESET)
                    )
                    .append(move.displayName.copy().formatted(Formatting.AQUA)))
                i += 1
            }

        }
            if (formattedText.content.toString().isNotEmpty()) {
                lore.add(formattedText)
            }
            return lore
        }


    fun getMovesByLevelToWikiGui(species: Species): MutableList<Text> {
        return getMovesByLevel(species)
    }

    //tmMoves tutorMoves evolutionMoves eggMoves formChangeMoves abilities base friendship drops eggGroups dynamax forms
    //behaviour  evyield  baseExpYield maleRatio

    fun getTmMoves(species: Species): MutableList<Text> {
        val lore: MutableList<Text> = ArrayList()
        var i = 0
        var formattedText = "".text()

        for (move in species.moves.tmMoves) {

            if (i > 0 && i.mod(4) == 0) {
                lore.add(formattedText)
                formattedText = "".text()
            }

            if (i.mod(4) != 0) {
                formattedText.append(" / ".yellow())
            }

            formattedText.append(move.displayName.copy().formatted(Formatting.AQUA))
            i+=1

        }
        if (formattedText.content.toString().isNotEmpty()) {
            lore.add(formattedText)
        }

        return lore

    }

    fun getTutorMoves(species: Species): MutableList<Text> {
        val lore: MutableList<Text> = ArrayList()
        var i = 0
        var formattedText = "".text()
        for (move in species.moves.tutorMoves) {

            if (i > 0 && i.mod(4) == 0) {
                lore.add(formattedText)
                formattedText = "".text()
            }

            if (i.mod(4) != 0) {
                formattedText.append(" / ".yellow())
            }

            formattedText.append( move.displayName.copy().formatted(Formatting.AQUA) )
            i+=1
        }
        return lore
    }

    fun getEvolutionMoves(species: Species): MutableList<Text> {
        val payload: MutableList<Text> = ArrayList()

        for (move in species.moves.evolutionMoves) {
            val formattedText = move.displayName.copy().formatted(Formatting.AQUA)
            payload.add(formattedText)
        }

        return payload
    }

    fun getEggMoves(species: Species): MutableList<Text> {
        val payload: MutableList<Text> = ArrayList()

        for (move in species.moves.eggMoves) {
            val formattedText = move.displayName.copy().formatted(Formatting.AQUA)
            payload.add(formattedText)
        }

        return payload
    }

    fun getFormChangeMoves(species: Species): MutableList<Text> {
        val payload: MutableList<Text> = ArrayList()

        for (move in species.moves.formChangeMoves) {
            val formattedText = move.displayName.copy().formatted(Formatting.AQUA)
            payload.add(formattedText)
        }

        return payload
    }

    fun getAbilities(species: Species): MutableList<Text> {
        val payload: MutableList<Text> = ArrayList()
        species.abilities.forEach {
            payload.add(it.template.displayName.yellow())
        }
        return payload
    }

    private fun getBaseFriendship(species: Species): MutableText {
        return Text.literal("Friendship - ").aqua().append(species.baseFriendship.toString().yellow())
    }

    fun getDrops(species: Species): MutableList<Text> {
        val payload: MutableList<Text> = ArrayList()

        val drops = species.drops.entries.filterIsInstance<ItemDropEntry>()

        drops.forEach { payload.add(Text.translatable(it.item.toTranslationKey()).aqua().append(Text.literal(" §e"+ it.percentage.toString()+ "%")))}

        if(payload.isEmpty()){
            payload.add(Text.literal("§eNo Drops"))
            return payload
        }
        return payload
    }

    fun getEggGroups(species: Species): MutableList<Text> {
        val payload: MutableList<Text> = ArrayList()
        species.eggGroups.forEach {
            payload.add(it.name.yellow())
        }
        return payload
    }

    fun getDynamax(species: Species): MutableList<Text> {
        val payload: MutableList<Text> = ArrayList()
        val textFormated = if (species.canGmax())  "Yes".yellow() else "Not".yellow()
        payload.add(textFormated)
        return payload
    }

    fun getForms(species: Species): MutableList<Text> {
        val payload: MutableList<Text> = ArrayList()
        species.forms.forEach{
            payload.add(it.name.yellow())
        }
        return payload
    }

    fun getEffectiveness(species: Species): MutableList<Text> {
        val lore: MutableList<Text> = ArrayList()
        val config = CobblemonWikiGui.getInstance()?.getConfigCobblemonWikiGui()

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

    //break
    //        if (weaknessList.isNotEmpty() || resistantList.isNotEmpty())
    //            lore.add("".text())


        if (weaknessList.isNotEmpty()) {
            val mutableText = Text.literal(config?.weakness ?: CobblemonWikiGuiConfig().weakness)
            for (elementalType in weaknessList) {
                mutableText.append(" ".text())
                mutableText.append(
                    elementalType.first.displayName.setStyle(
                        Style.EMPTY
                            .withBold(true)
                            .withColor(elementalType.first.hue)
                    )
                )
            }
            lore.add(mutableText)
        }

        if (resistantList.isNotEmpty()) {
            val mutableText = Text.literal(config?.resistant ?: CobblemonWikiGuiConfig().resistant)
            for (elementalType in resistantList) {
                mutableText.append(" ".text())
                mutableText.append(
                    elementalType.first.displayName.setStyle(
                        Style.EMPTY
                            .withBold(true)
                            .withColor(elementalType.first.hue)
                    )
                )
            }
            lore.add(mutableText)
        }

        if (immuneList.isNotEmpty()) {
            val mutableText = Text.translatable(config?.immune ?: CobblemonWikiGuiConfig().immune)
            for (elementalType in immuneList) {
                mutableText.append(" ".text())
                mutableText.append(
                    elementalType.first.displayName.setStyle(
                        Style.EMPTY
                            .withBold(true)
                            .withColor(elementalType.first.hue)
                    )
                )
            }
            lore.add(mutableText)

        }
        return lore
    }

}
