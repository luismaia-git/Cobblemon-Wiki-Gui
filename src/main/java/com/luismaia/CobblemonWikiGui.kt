package com.luismaia

import com.luismaia.command.CobblemonWikiGuiCommands
import com.luismaia.config.CobblemonWikiGuiConfig
import com.luismaia.config.ConfigManager
import com.luismaia.util.Permissions
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
    private var instance : CobblemonWikiGui? = null

    private var config : CobblemonWikiGuiConfig? = null
    private val configManager: ConfigManager = ConfigManager
    val permissions = Permissions()

    fun initialize() {
        this.instance = this
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
            this.config = getConfigCobblemonWikiGui()
        } catch (e: IOException) {
            LOGGER.error("Erro ao carregar config.json")
            LOGGER.info(e.message)
        }
    }

    fun getConfigCobblemonWikiGui(): CobblemonWikiGuiConfig? {
        return this.configManager.config
    }
    fun getConfigManager(): ConfigManager {
        return this.configManager
    }

    fun reloadConfigCobblemonWikiGui(playerEntity: ServerPlayerEntity?) {
        this.configManager.reload(playerEntity)
    }

    fun getInstance(): CobblemonWikiGui? {
        return this.instance
    }


}