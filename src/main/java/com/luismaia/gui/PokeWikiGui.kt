package com.luismaia.gui

import com.cobblemon.mod.common.Cobblemon.MODID
import com.cobblemon.mod.common.CobblemonItems
import com.cobblemon.mod.common.api.pokemon.PokemonSpecies.getByPokedexNumber
import com.cobblemon.mod.common.pokemon.Species
import com.luismaia.CobblemonWikiGui
import com.luismaia.config.CobblemonWikiGuiConfig
import com.luismaia.helper.GuiHelper
import com.luismaia.util.CobblemonUtil
import com.luismaia.util.Helper
import eu.pb4.sgui.api.elements.GuiElement
import eu.pb4.sgui.api.elements.GuiElementBuilder
import eu.pb4.sgui.api.gui.SimpleGui
import net.minecraft.block.Blocks
import net.minecraft.item.ItemStack
import net.minecraft.item.Items
import net.minecraft.screen.ScreenHandlerType
import net.minecraft.server.network.ServerPlayerEntity
import net.minecraft.text.Text

object PokeWikiGui {
    private var config = CobblemonWikiGui.getConfigCobblemonWikiGui()

    fun open(species: Species, playerEntity: ServerPlayerEntity): SimpleGui {
        val content = contentMain(species, playerEntity)

        if (content.size > MAX_CONTENT) {
            throw RuntimeException("The content provided exceeds $MAX_CONTENT elements or is invalid.")
        }

        val gui = SimpleGui(ScreenHandlerType.GENERIC_9X6, playerEntity, false)
        gui.setTitle(Text.literal("\u00A7l        Pokemon Wiki"))

        GuiHelper.setLine(GuiHelper.LineType.HORIZONTAL,gui, 0, 0, 8, RED_PANE)
        GuiHelper.setLine(GuiHelper.LineType.VERTICAL, gui, 0, 1, 3, RED_PANE)
        GuiHelper.setLine(GuiHelper.LineType.HORIZONTAL, gui, 4, 0, 8, RED_PANE)
        gui.setSlot(48, createRelativeButton(species, -1).build())
        gui.setSlot(49, GuiHelper.createPokemonButton(species).build())
        gui.setSlot(50, createRelativeButton(species, +1).build())
        GuiHelper.setLine(GuiHelper.LineType.VERTICAL, gui, 8, 1, 3, RED_PANE)
        GuiHelper.setLine(GuiHelper.LineType.HORIZONTAL, gui, 5, 0, 2, RED_PANE)
        GuiHelper.setLine(GuiHelper.LineType.HORIZONTAL, gui, 5, 6, 8, RED_PANE)
        for (j in content.indices) {
            gui.setSlot(CONTENT_SPACE[j], content[j])
        }

        gui.open()

        return gui
    }

    private val RED_PANE: GuiElement = GuiHelper.createEmptyButton(ItemStack(Blocks.RED_STAINED_GLASS_PANE, 1)).build()

    private val CONTENT_SPACE = arrayOf(
        10, 11, 12, 13, 14, 15, 16,
        19, 20, 21, 22, 23, 24, 25,
        28, 29, 30, 31, 32, 33, 34
//        37, 38, 39, 40, 41, 42, 43,
//        46, 47, 48, 49, 50, 51, 52,
        )

    private val MAX_CONTENT: Int = CONTENT_SPACE.size

    private fun createRelativeButton(species: Species, step: Int): GuiElementBuilder {
        val relative = getByPokedexNumber(species.nationalPokedexNumber + step, MODID)
        if(relative != null){
            return GuiHelper.createPokemonButton(relative)
                .setLore(listOf<Text>(Text.literal(config?.pokeInfo ?: CobblemonWikiGuiConfig().pokeInfo)))
                .setCallback { _, _, _, gui ->
                    run {
                        gui.close()
                        open(relative, gui.player)
                    }
                }
        }
        return GuiHelper.createEmptyButton(RED_PANE.itemStack)
    }

    private fun contentMain(species: Species, serverPlayerEntity: ServerPlayerEntity): Array<GuiElement?> {

        val content: Array<GuiElement?> = arrayOf(

            GuiHelper.createEmptyButton(ItemStack(CobblemonItems.LIGHT_BALL)).setName(Text.literal(config?.type ?: CobblemonWikiGuiConfig().type))
                    .setLore(CobblemonUtil.getTypeToWikiGui(species).toMutableList())
                    .build()
            ,
            GuiHelper.createEmptyButton(ItemStack(CobblemonItems.ELECTIRIZER)).setName(Text.literal(config?.effectiveness ?: CobblemonWikiGuiConfig().effectiveness))
                .setLore(CobblemonUtil.getEffectiveness(species))
                .build()
            ,
            GuiHelper.createEmptyButton(Helper.removeLore(ItemStack(CobblemonItems.POKE_BALL)))
                .setName(Text.literal(config?.catchrate ?: CobblemonWikiGuiConfig().catchrate))
                .setLore(CobblemonUtil.getCatchRateToWikiGui(species))
                .build()
            ,
            GuiHelper.createEmptyButton(ItemStack(CobblemonItems.WEAKNESS_POLICY))
                .setName(Text.literal(config?.basestats ?: CobblemonWikiGuiConfig().basestats)).setLore(
                    CobblemonUtil.getBaseStatsToWikiGui(species)
                )
                .build()
            ,
            GuiHelper.createEmptyButton(ItemStack(Items.OAK_SAPLING))
                .setName(Text.literal(config?.spawnbiome ?: CobblemonWikiGuiConfig().spawnbiome))
                .setLore(CobblemonUtil.getSpawnBiomesToWikiGui(species, serverPlayerEntity.world))
                .build()
            ,
            GuiHelper.createEmptyButton(ItemStack(Items.CLOCK))
                .setName(Text.literal(config?.spawntime ?: CobblemonWikiGuiConfig().spawntime))
                .setLore(CobblemonUtil.getSpawnTime(species))
                .build()
            ,
            GuiHelper.createPokemonButton(species)
                .setName(Text.literal(config?.evolutions ?: CobblemonWikiGuiConfig().evolutions))
                .setLore(CobblemonUtil.getEvolutionsToWikiGui(species))
                .build()
            ,
            GuiHelper.createEmptyButton(ItemStack(CobblemonItems.ABILITY_CAPSULE))
                .setName(Text.literal(config?.abilities ?: CobblemonWikiGuiConfig().abilities)).setLore(
                    CobblemonUtil.getAbilities(species)
                )
                .build()
            ,
            GuiHelper.createEmptyButton(ItemStack(CobblemonItems.LEVEL_BALL))
                .setName(Text.literal(config?.movesbylevel ?: CobblemonWikiGuiConfig().movesbylevel)).setLore(
                    CobblemonUtil.getMovesByLevelToWikiGui(species)
                )
                .build()
            ,
            GuiHelper.createEmptyButton(ItemStack(Items.MUSIC_DISC_13))
                .setName(Text.literal(config?.tmMoves ?: CobblemonWikiGuiConfig().tmMoves))
                .setLore(
                    CobblemonUtil.getTmMoves(species)
                )
                .build()
            ,
            GuiHelper.createEmptyButton(ItemStack(Items.MUSIC_DISC_PIGSTEP))
                .setName(Text.literal(config?.tutorMoves ?: CobblemonWikiGuiConfig().tutorMoves)).setLore(
                    CobblemonUtil.getTutorMoves(species)
                )
                .build()
            ,
            GuiHelper.createEmptyButton(ItemStack(Items.MUSIC_DISC_5))
                .setName(Text.literal(config?.evolutionMoves ?: CobblemonWikiGuiConfig().evolutionMoves)).setLore(
                    CobblemonUtil.getEvolutionMoves(species)
                )
                .build()

            ,
            GuiHelper.createEmptyButton(ItemStack(CobblemonItems.POWER_WEIGHT))
                .setName(Text.literal(config?.formChangeMoves ?: CobblemonWikiGuiConfig().formChangeMoves)).setLore(
                    CobblemonUtil.getFormChangeMoves(species)
                )
                .build()
            ,
            GuiHelper.createEmptyButton(ItemStack(CobblemonItems.OVAL_STONE))
                .setName(Text.literal(config?.eggMoves ?: CobblemonWikiGuiConfig().eggMoves)).setLore(
                    CobblemonUtil.getEggMoves(species)
                )
                .build()
            ,
            GuiHelper.createEmptyButton(ItemStack(CobblemonItems.LUCKY_EGG))
                .setName(Text.literal(config?.eggGroups ?: CobblemonWikiGuiConfig().eggGroups)).setLore(
                    CobblemonUtil.getEggGroups(species)
                )
                .build()
            ,
            GuiHelper.createEmptyButton(ItemStack(CobblemonItems.NORMAL_GEM))
                .setName(Text.literal(config?.forms ?: CobblemonWikiGuiConfig().forms)).setLore(
                    CobblemonUtil.getForms(species)
                )
                .build()

            ,
            GuiHelper.createEmptyButton(ItemStack(CobblemonItems.AIR_BALLOON))
                .setName(Text.literal(config?.dynamax ?: CobblemonWikiGuiConfig().dynamax)).setLore(
                    CobblemonUtil.getDynamax(species)
                )
                .build()
        )
        return content
    }
}
