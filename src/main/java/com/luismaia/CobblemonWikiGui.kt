package com.luismaia

import com.cobblemon.mod.common.Cobblemon
import com.luismaia.command.CobblemonWikiGuiCommands
import com.luismaia.config.CobblemonWikiGuiConfig
import com.luismaia.config.ConfigManager
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback
import net.minecraft.server.network.ServerPlayerEntity
import net.minecraft.text.Text
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.io.IOException

object CobblemonWikiGui {

    val MOD_ID: String = "cobblemon-wiki-gui"
    val LOGGER: Logger = LoggerFactory.getLogger(MOD_ID)
    val MOD_NAME: String = "Cobblemon Wiki Gui"
    val VERSION: String = "1.0.0"

    private var config : CobblemonWikiGuiConfig? = null
    private val configManager: ConfigManager = ConfigManager


    fun initialize() {

        initConfigBase()

        LOGGER.info(
            String.format(
                "%s v%s loaded! This mod was made by LuisMaia. https://github.com/luismaia-git",
                MOD_NAME,
                VERSION
            )
        )

        CommandRegistrationCallback.EVENT.register(CobblemonWikiGuiCommands::register)
    }

    private fun initConfigBase() {
        try {
            configManager.loadConfig()
            this.config = configManager.config
        } catch (e: IOException) {
            LOGGER.error("Erro ao carregar config.json")
            LOGGER.info(e.message)
        }
    }

    fun getConfigCobblemonWikiGui(): CobblemonWikiGuiConfig? {
        return this.config
    }

    fun reloadConfigCobblemonWikiGui(playerEntity: ServerPlayerEntity?) {
        playerEntity?.sendMessage(Text.literal("this feature is incomplete"))
    //this.configManager.reload(playerEntity)
    }

}