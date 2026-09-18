package com.cwg.mod.util

import com.cobblemon.mod.common.api.drop.ItemDropEntry
import com.cobblemon.mod.common.api.pokemon.evolution.Evolution
import com.cobblemon.mod.common.api.pokemon.stats.Stats
import com.cobblemon.mod.common.api.spawning.condition.SpawningCondition
import com.cobblemon.mod.common.api.spawning.detail.PokemonSpawnDetail
import com.cobblemon.mod.common.api.text.*
import com.cobblemon.mod.common.api.types.ElementalType
import com.cobblemon.mod.common.api.types.ElementalTypes
import com.cobblemon.mod.common.pokemon.FormData
import com.cobblemon.mod.common.util.asTranslated
import com.cwg.mod.CobblemonWikiGui
import net.minecraft.ChatFormatting
import net.minecraft.network.chat.Component
import net.minecraft.network.chat.MutableComponent
import net.minecraft.network.chat.Style
import kotlin.math.roundToInt


object CobblemonUtil {

    private val darkAqua = Style.EMPTY.withColor(ChatFormatting.DARK_AQUA).withItalic(false)
    private val darkPurple = Style.EMPTY.withColor(ChatFormatting.DARK_PURPLE).withItalic(false)
    private val gold = Style.EMPTY.withColor(ChatFormatting.GOLD).withItalic(false)
    private val red = Style.EMPTY.withColor(ChatFormatting.RED).withItalic(false)
    private val lightPurple = Style.EMPTY.withColor(ChatFormatting.LIGHT_PURPLE).withItalic(false)
    private val yellow = Style.EMPTY.withColor(ChatFormatting.YELLOW).withItalic(false)
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
        // Each type keeps its own hue (like the Effectiveness tile's typeListComponent) instead
        // of the whole "Type1/Type2" string being forced to one color, which previously made a
        // dual-type mon's first type show the wrong (hardcoded yellow) color.
        return pokemon.types
            .map { type -> type.displayName.copy().setStyle(Style.EMPTY.withColor(type.hue)) }
            .reduce { acc, next -> acc.append("/".text()).append(next) }
    }

    fun getRequirementsToWikiGui(evolution: Evolution): MutableList<Component> =
        EvolutionRequirementFormatter.getRequirementsToWikiGui(evolution)

    fun getSpawnDetails(formData: FormData): List<PokemonSpawnDetail> =
        SpawnInfoFormatter.getSpawnDetails(formData)

    fun getSpawnTime(conditions: MutableList<SpawningCondition<*>>): MutableList<Component> =
        SpawnInfoFormatter.getSpawnTime(conditions)

    fun addConditionSection(
        lore: MutableList<Component>,
        title: String,
        conditions: List<SpawningCondition<*>>
    ) = SpawnInfoFormatter.addConditionSection(lore, title, conditions)

    private fun getCatchRate(pokemon: FormData): MutableComponent {
        val baseRateDouble: Double = (pokemon.catchRate / 255.0) * 100.0
        val baseRate: String = baseRateDouble.roundToInt().toString()
        return "$baseRate%".aqua()
    }

    fun getTypeToWikiGui(pokemon: FormData): MutableList<Component> {
        return toWikiGui(getType(pokemon))
    }

    fun getBaseStatsToWikiGui(pokemon: FormData): MutableList<Component> {
        val lore: MutableList<Component> = ArrayList()

        val initialString = " §b- §a"
        lore.add(
            Component.translatable("cobblemon.ui.stats.hp").setStyle(lightPurple)
                .append(initialString + (pokemon.baseStats[Stats.HP]))
        )
        lore.add(
            Component.translatable("cobblemon.ui.stats.atk").setStyle(red)
                .append(initialString + (pokemon.baseStats[Stats.ATTACK]))
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
        val payload: MutableSet<Component> = mutableSetOf()
        species.abilities.forEach {
            payload.add(it.template.displayName.yellow())
        }
        return payload.toMutableList()
    }

    fun getEVYield(species: FormData): MutableList<Component> {
        val lore: MutableList<Component> = ArrayList()

        val initialString = " §b- §a"
        species.evYield[Stats.HP]?.let {
            if (it > 0) {
                lore.add(
                    Component.translatable("cobblemon.ui.stats.hp").setStyle(lightPurple)
                        .append(initialString + it)
                )
            }
        }
        species.evYield[Stats.ATTACK]?.let {
            if (it > 0) {
                lore.add(
                    Component.translatable("cobblemon.ui.stats.atk").setStyle(red)
                        .append(initialString + it)
                )
            }
        }
        species.evYield[Stats.DEFENCE]?.let {
            if (it > 0) {
                lore.add(
                    Component.translatable("cobblemon.ui.stats.def").setStyle(gold)
                        .append(initialString + it)
                )
            }
        }
        species.evYield[Stats.SPECIAL_ATTACK]?.let {
            if (it > 0) {
                lore.add(
                    Component.translatable("cobblemon.ui.stats.sp_atk").setStyle(darkPurple)
                        .append(initialString + it)
                )
            }
        }
        species.evYield[Stats.SPECIAL_DEFENCE]?.let {
            if (it > 0) {
                lore.add(
                    Component.translatable("cobblemon.ui.stats.sp_def").setStyle(yellow)
                        .append(initialString + it)
                )
            }
        }
        species.evYield[Stats.SPEED]?.let {
            if (it > 0) {
                lore.add(
                    Component.translatable("cobblemon.ui.stats.speed").setStyle(darkAqua)
                        .append(initialString + it)
                )
            }
        }
        return lore
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

    fun getRideable(formData: FormData): MutableList<Component> {
        val payload: MutableList<Component> = ArrayList()
        val riding = formData.species.riding
        val isRideable = riding.seats.isNotEmpty() || !riding.behaviours.isNullOrEmpty()
        payload.add((if (isRideable) "Yes" else "Not").yellow())
        return payload
    }

    fun getForms(form: FormData): MutableList<Component> {
        val payload: MutableList<Component> = ArrayList()
        if (form.name != form.species.standardForm.name) {
            val formDisplayName = form.name.replaceFirstChar { it.uppercase() }
            payload.add(formDisplayName.yellow())
        } else {
            form.species.forms.forEach {
                payload.add(it.name.yellow())
            }
        }
        return payload
    }

    fun getEffectiveness(species: FormData): MutableList<Component> {
        val lore: MutableList<Component> = ArrayList()

        val weaknessList = mutableListOf<ElementalType>()
        val resistantList = mutableListOf<ElementalType>()
        val immuneList = mutableListOf<ElementalType>()

        for (type in ElementalTypes.all()) {
            val multiplier = TypeChart.getMultiplier(type, species.types)
            when {
                multiplier == 0.0 -> immuneList.add(type)
                multiplier > 1.0 -> weaknessList.add(type)
                multiplier < 1.0 -> resistantList.add(type)
            }
        }

        fun typeListComponent(label: String, types: List<ElementalType>): Component {
            val component = label.text()
            for (elementalType in types) {
                component.append(" ".text())
                component.append(
                    elementalType.displayName.setStyle(
                        Style.EMPTY
                            .withBold(true)
                            .withColor(elementalType.hue)
                    )
                )
            }
            return component
        }

        if (weaknessList.isNotEmpty()) lore.add(typeListComponent(lang.weakness, weaknessList))
        if (resistantList.isNotEmpty()) lore.add(typeListComponent(lang.resistant, resistantList))
        if (immuneList.isNotEmpty()) lore.add(typeListComponent(lang.immune, immuneList))

        return lore
    }

}
