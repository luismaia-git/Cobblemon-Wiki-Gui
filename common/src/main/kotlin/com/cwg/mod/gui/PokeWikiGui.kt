package com.cwg.mod.gui

import com.cobblemon.mod.common.CobblemonItems
import com.cobblemon.mod.common.api.pokemon.PokemonSpecies.getByPokedexNumber
import com.cobblemon.mod.common.api.text.red
import com.cobblemon.mod.common.api.text.text
import com.cobblemon.mod.common.pokemon.Species
import com.cwg.mod.CobblemonWikiGui
import com.cwg.mod.helper.GuiHelper
import com.cwg.mod.util.CobblemonUtil
import eu.pb4.sgui.api.elements.GuiElement
import eu.pb4.sgui.api.elements.GuiElementBuilder
import eu.pb4.sgui.api.gui.SimpleGui
import net.minecraft.network.chat.Component
import net.minecraft.server.level.ServerPlayer
import net.minecraft.world.inventory.MenuType
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.Items

object PokeWikiGui {
    val lang = CobblemonWikiGui.langConfig
    private val redPane: GuiElement = GuiHelper.RED_PANE
    fun open(species: Species, playerEntity: ServerPlayer): SimpleGui {

        val content = contentMain(species, playerEntity)

        if (content.size > MAX_CONTENT) {
            throw RuntimeException("The content provided exceeds $MAX_CONTENT elements or is invalid.")
        }

        val gui = SimpleGui(MenuType.GENERIC_9x6, playerEntity, false)
        gui.title = Component.literal("Cobblemon Wiki").red()

        GuiHelper.setLine(GuiHelper.LineType.HORIZONTAL,gui, 0, 0, 8, redPane)
        GuiHelper.setLine(GuiHelper.LineType.VERTICAL, gui, 0, 1, 3, redPane)
        GuiHelper.setLine(GuiHelper.LineType.HORIZONTAL, gui, 4, 0, 8, redPane)
        gui.setSlot(48, createRelativeButton(species, -1).build())
        gui.setSlot(49, GuiHelper.createPokemonButton(species).build())
        gui.setSlot(50, createRelativeButton(species, +1).build())
        GuiHelper.setLine(GuiHelper.LineType.VERTICAL, gui, 8, 1, 3, redPane)
        GuiHelper.setLine(GuiHelper.LineType.HORIZONTAL, gui, 5, 0, 2, redPane)
        GuiHelper.setLine(GuiHelper.LineType.HORIZONTAL, gui, 5, 6, 8, redPane)

        for (j in content.indices) {
            gui.setSlot(CONTENT_SPACE[j], content[j])
        }

        for (i in 0 until gui.size) {
            if (gui.getSlot(i) == null) {
                gui.setSlot(i, redPane)
            }
        }

        gui.open()

        return gui
    }



    private val CONTENT_SPACE = arrayOf(
        10, 11, 12, 13, 14, 15, 16,
        19, 20, 21, 22, 23, 24, 25,
        28, 29, 30, 31, 32, 33, 34
//      37, 38, 39, 40, 41, 42, 43,
//      46, 47, 48, 49, 50, 51, 52,
    )

    private val MAX_CONTENT: Int = CONTENT_SPACE.size

    private fun createRelativeButton(species: Species, step: Int): GuiElementBuilder {
        val relative = getByPokedexNumber(species.nationalPokedexNumber + step)
        if(relative != null){
            return GuiHelper.createPokemonButton(relative)
                .setLore(listOf<Component>(lang.pokeInfo.text()))
                .setCallback { _, _, _, gui ->
                    run {
                        gui.close()
                        open(relative, gui.player)
                    }
                }
        }
        return GuiHelper.createEmptyButton(redPane.itemStack)
    }

    private fun contentMain(species: Species, serverPlayerEntity: ServerPlayer): Array<GuiElement?> {
        val content: Array<GuiElement?> = arrayOf(
            GuiHelper
                .createEmptyButton(ItemStack(CobblemonItems.LIGHT_BALL))
                .setName(lang.type.text())
                .setLore(CobblemonUtil.getTypeToWikiGui(species).toMutableList())
                .build()
            ,
            GuiHelper
                .createEmptyButton(ItemStack(CobblemonItems.ELECTIRIZER))
                .setName(lang.effectiveness.text())
                .setLore(CobblemonUtil.getEffectiveness(species))
                .build()
            ,
            GuiHelper
                .createEmptyButton(ItemStack(CobblemonItems.POKE_BALL))
                .setName(lang.catchrate.text())
                .setLore(CobblemonUtil.getCatchRateToWikiGui(species))
                .build()
            ,
            GuiHelper
                .createEmptyButton(ItemStack(CobblemonItems.WEAKNESS_POLICY))
                .setName(lang.basestats.text())
                .setLore(
                    CobblemonUtil.getBaseStatsToWikiGui(species)
                )
                .build()
            ,
            GuiHelper.createEmptyButton(ItemStack(Items.OAK_SAPLING))
                .setName(lang.spawnbiome.text())
                .setLore(CobblemonUtil.getSpawnBiomesToWikiGui(species, serverPlayerEntity.serverLevel()))
                .build()
            ,
            GuiHelper.createPokemonButton(species)
                .setName(lang.evolutions.text())
                .setLore(listOf(lang.seeEvolutions.text()))
                .setCallback { _, _, _, gui ->
                    gui.close()
                    EvolutionsGui.open(species, serverPlayerEntity)
                }
                .build()
            ,
            GuiHelper.createEmptyButton(ItemStack(Items.CLOCK))
                .setName(Component.literal(lang.spawntime))
                .setLore(CobblemonUtil.getSpawnTime(species))
                .build()
            ,
            GuiHelper.createEmptyButton(ItemStack(CobblemonItems.ABILITY_CAPSULE))
                .setName(Component.literal(lang.abilities))
                .setLore(
                    CobblemonUtil.getAbilities(species)
                )
                .build()
            ,
            GuiHelper.createEmptyButton(ItemStack(CobblemonItems.LEVEL_BALL))
                .setName(Component.literal(lang.movesbylevel))
                .setLore(
                    CobblemonUtil.getMovesByLevelToWikiGui(species)
                )
                .build()
            ,
            GuiHelper.createEmptyButton(ItemStack(Items.MUSIC_DISC_13))
                .setName(Component.literal(lang.tmMoves))
                .setLore(
                    CobblemonUtil.getTmMoves(species)
                )
                .build()
            ,
            GuiHelper.createEmptyButton(ItemStack(Items.MUSIC_DISC_PIGSTEP))
                .setName(Component.literal(lang.tutorMoves))
                .setLore(
                    CobblemonUtil.getTutorMoves(species)
                )
                .build()
            ,
            GuiHelper.createEmptyButton(ItemStack(Items.MUSIC_DISC_5))
                .setName(Component.literal(lang.evolutionMoves))
                .setLore(
                    CobblemonUtil.getEvolutionMoves(species)
                )
                .build()
            ,
            GuiHelper.createEmptyButton(ItemStack(CobblemonItems.POWER_WEIGHT))
                .setName(Component.literal(lang.formChangeMoves))
                .setLore(
                    CobblemonUtil.getFormChangeMoves(species)
                )
                .build()
            ,
            GuiHelper.createEmptyButton(ItemStack(CobblemonItems.OVAL_STONE))
                .setName(Component.literal(lang.eggMoves))
                .setLore(
                    CobblemonUtil.getEggMoves(species)
                )
                .build()
            ,
            GuiHelper.createEmptyButton(ItemStack(CobblemonItems.LUCKY_EGG))
                .setName(Component.literal(lang.eggGroups))
                .setLore(
                    CobblemonUtil.getEggGroups(species)
                )
                .build()
            ,
            GuiHelper.createEmptyButton(ItemStack(CobblemonItems.NORMAL_GEM))
                .setName(Component.literal(lang.forms))
                .setLore(
                    CobblemonUtil.getForms(species)
                )
                .build()
            ,
            GuiHelper.createEmptyButton(ItemStack(CobblemonItems.AIR_BALLOON))
                .setName(Component.literal(lang.dynamax))
                .setLore(
                    CobblemonUtil.getDynamax(species)
                )
                .build()
            ,
            GuiHelper.createEmptyButton(ItemStack(Items.BONE))
                .setName(Component.literal(lang.drops))
                .setLore(
                    CobblemonUtil.getDrops(species)
                )
                .build()
        )
        return content
    }
}
