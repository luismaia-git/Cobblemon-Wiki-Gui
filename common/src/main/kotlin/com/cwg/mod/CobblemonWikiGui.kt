package com.cwg.mod

import com.cwg.mod.api.permission.PermissionValidator
import com.cwg.mod.command.CobblemonWikiGuiCommands
import com.cwg.mod.config.CobblemonWikiGuiConfig
import com.cwg.mod.config.CobblemonWikiGuiLang
import com.cwg.mod.permission.LaxPermissionValidator
import com.cwg.mod.util.ifDedicatedServer
import com.cwg.mod.util.server
import com.mojang.brigadier.CommandDispatcher
import net.minecraft.client.Minecraft
import net.minecraft.commands.CommandBuildContext
import net.minecraft.commands.CommandSourceStack
import net.minecraft.commands.Commands
import net.minecraft.resources.ResourceKey
import net.minecraft.world.level.Level
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger
import java.io.File
import java.io.FileReader
import java.io.FileWriter
import kotlin.properties.Delegates

object CobblemonWikiGui {

    const val MOD_ID: String = "cobblemon-wiki-gui"
    const val MOD_NAME: String = "Cobblemon Wiki Gui"
    const val VERSION: String = "2.1.0"
    const val CONFIG_PATH = "config/$MOD_ID/main.json"
    const val CONFIG_LANG_PATH = "config/$MOD_ID/lang.json"

    @JvmField
    val LOGGER: Logger = LogManager.getLogger()
    lateinit var implementation: CobblemonWikiGuiImplementation

    lateinit var config: CobblemonWikiGuiConfig

    lateinit var langConfig: CobblemonWikiGuiLang

    var permissionValidator: PermissionValidator by Delegates.observable(LaxPermissionValidator().also { it.initialize() }) { _, _, newValue -> newValue.initialize() }

    var isDedicatedServer = false;

    fun preInitialize(implementation: CobblemonWikiGuiImplementation) {
        this.implementation = implementation
        implementation.registerPermissionValidator()
        this.loadConfig()
    }

    fun initialize() {
        LOGGER.info(
            String.format(
                "%s v%s loaded! This mod was made by LuisMaia. https://github.com/luismaia-git",
                MOD_NAME,
                VERSION
            )
        )

        ifDedicatedServer {
            isDedicatedServer = true
        }

    }

    fun getLevel(dimension: ResourceKey<Level>): Level? {
        return if (isDedicatedServer) {
            server()?.getLevel(dimension)
        } else {
            val mc = Minecraft.getInstance()
            return mc.singleplayerServer?.getLevel(dimension) ?: mc.level
        }
    }

    private fun initializeConfig() {
        loadMainConfig()
        loadLangConfig()

        saveConfig(this.config)
        saveLangConfig(this.langConfig)
    }

    fun loadConfig() {
        initializeConfig()
    }

    fun reloadConfig() {
        initializeConfig()
    }


    private fun loadMainConfig() {
        val configFile = File(CONFIG_PATH)
        configFile.parentFile.mkdirs()

        // Check config existence and load if it exists, otherwise create default.
        if (configFile.exists()) {
            try {
                val fileReader = FileReader(configFile)
                this.config = CobblemonWikiGuiConfig.GSON.fromJson(fileReader, CobblemonWikiGuiConfig::class.java)
                fileReader.close()
            } catch (exception: Exception) {
                LOGGER.error("Failed to load the config! Using default config until the following has been addressed:")
                this.config = CobblemonWikiGuiConfig()
                exception.printStackTrace()
            }

        } else {
            this.config = CobblemonWikiGuiConfig()
        }
    }

    fun saveConfig(config: CobblemonWikiGuiConfig) {
        try {
            val configFile = File(CONFIG_PATH)
            val fileWriter = FileWriter(configFile)
            // Put the config to json then flush the writer to commence writing.
            CobblemonWikiGuiConfig.GSON.toJson(config, fileWriter)
            fileWriter.flush()
            fileWriter.close()
        } catch (exception: Exception) {
            LOGGER.error("Failed to save the config! Please consult the following stack trace:")
            exception.printStackTrace()
        }
    }



    private fun loadLangConfig() {
        val configFile = File(CONFIG_LANG_PATH)
        configFile.parentFile.mkdirs()

        // Check config existence and load if it exists, otherwise create default.
        if (configFile.exists()) {
            try {
                val fileReader = FileReader(configFile)
                this.langConfig = CobblemonWikiGuiLang.GSON.fromJson(fileReader, CobblemonWikiGuiLang::class.java)
                fileReader.close()
            } catch (exception: Exception) {
                LOGGER.error("Failed to load the lang config! Using default config until the following has been addressed:")
                this.langConfig = CobblemonWikiGuiLang()
                exception.printStackTrace()
            }

        } else {
            this.langConfig = CobblemonWikiGuiLang()
        }
    }

    fun saveLangConfig(config: CobblemonWikiGuiLang) {
        try {
            val configFile = File(CONFIG_LANG_PATH)
            val fileWriter = FileWriter(configFile)
            // Put the config to json then flush the writer to commence writing.
            CobblemonWikiGuiLang.GSON.toJson(config, fileWriter)
            fileWriter.flush()
            fileWriter.close()
        } catch (exception: Exception) {
            LOGGER.error("Failed to save the lang config! Please consult the following stack trace:")
            exception.printStackTrace()
        }
    }

}