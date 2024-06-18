package com.luismaia.config

import com.luismaia.CobblemonWikiGui
import kotlinx.serialization.decodeFromString

import com.google.gson.Gson
import kotlinx.serialization.SerializationException
import kotlinx.serialization.json.Json
import net.fabricmc.loader.api.FabricLoader
import net.minecraft.server.network.ServerPlayerEntity
import net.minecraft.text.Style
import net.minecraft.text.Text
import net.minecraft.util.Formatting
import org.slf4j.Logger
import java.io.FileReader
import java.io.FileWriter
import java.nio.file.Files
import java.nio.file.Path

object ConfigManager {

    private val logger: Logger = CobblemonWikiGui.LOGGER
    private val rootPath : Path =  FabricLoader.getInstance().configDir.resolve("cobblemon-wiki-gui/")
    private val configFilepathName = rootPath.resolve("config.json")
    private val configFile = configFilepathName.toFile()

    var config: CobblemonWikiGuiConfig? = null
    
    init {
        logger.info("============================================")
        logger.info("Initialize Config module")
        logger.info("--------------------------------------------")
        initConfig()
        logger.info("============================================")
    }

    private fun initConfig(){

        val parentDirectory = configFilepathName.parent
        if (parentDirectory != null && Files.notExists(parentDirectory)) {
            Files.createDirectories(parentDirectory)
        }
        createFileConfig(null)
    }
    private fun createFileConfig(player: ServerPlayerEntity?){
        try {
            if (Files.notExists(configFilepathName)) {
                logger.error("Config file does not exist.")
                player?.sendMessage(Text.literal(config?.chatTitle).setStyle(Style.EMPTY.withColor(Formatting.GREEN)).append(Text.literal("Creating a new file config.json").setStyle(Style.EMPTY.withColor(Formatting.RED))))

                Files.createFile(configFilepathName)

                logger.info("File config.json created!")
                player?.sendMessage(Text.literal(config?.chatTitle).setStyle(Style.EMPTY.withColor(Formatting.GREEN)).append(Text.literal("File config.json created!")))
            } else {
                logger.info("File config.json already exists!")
                player?.sendMessage(Text.literal(config?.chatTitle).setStyle(Style.EMPTY.withColor(Formatting.GREEN)).append(Text.literal("File config.json already exists!")))
            }
        } catch (e: Exception){
            logger.error("Houve um problema! ${e.message}")

        }
    }

    fun loadConfig() {
        when (configFileStatus()) {
            ConfigFileStatus.NOT_EXIST -> {
                createFileConfig(null)
                createInitialDataConfigJson(null)
            }
            ConfigFileStatus.EMPTY -> {
                logger.info("Config file is empty.")
                createInitialDataConfigJson(null)
            }
            ConfigFileStatus.INVALID -> {
                // Execute code for INVALID
                logger.info("Config file is invalid.")
                logger.info("Format the file \"config.json\" correctly")
            }
            ConfigFileStatus.VALID -> {
                // Execute code for VALID
                logger.info("Config file is valid.")
                logger.info("Loading config.json")
                val gson = Gson()
                FileReader(configFile).use { reader ->
                    config = gson.fromJson(reader, CobblemonWikiGuiConfig::class.java)
                }
            }
        }
    }

    private fun createInitialDataConfigJson(player: ServerPlayerEntity?) {
        logger.info("Creating default data for config.json")
        player?.sendMessage(Text.literal(config?.chatTitle).setStyle(Style.EMPTY.withColor(Formatting.GREEN)).append(Text.literal("Creating default data for config.json")))

        try {
            configFile.parentFile.mkdirs()
            val gson = Gson()
            FileWriter(configFile).use { writer ->
                gson.toJson(CobblemonWikiGuiConfig(), writer)
            }
            logger.info("Initial default data for config.json created!")
        } catch (e: Exception){
            logger.error(e.toString())
        }
    }
    

    fun reload(player: ServerPlayerEntity?){
        player?.sendMessage(Text.literal(config?.chatTitle).setStyle(Style.EMPTY.withColor(Formatting.GREEN)).append(Text.literal("Reloading config.json...")))
        logger.info("Reloading config.json...")

        when (configFileStatus()) {
            ConfigFileStatus.NOT_EXIST -> {
                createFileConfig(player)
                createInitialDataConfigJson(player)
            }
            ConfigFileStatus.EMPTY -> {
                logger.info("Config file is empty.")
                createInitialDataConfigJson(player)
            }
            ConfigFileStatus.INVALID -> {
                // Execute code for INVALID
                logger.info("Config file is invalid.")
                logger.info("Format the file \"config.json\" correctly")

                player?.sendMessage(Text.literal(config?.chatTitle).setStyle(Style.EMPTY.withColor(Formatting.GREEN)).append(Text.literal("Config file is invalid. Format the file \"config.json\" correctly")).setStyle(Style.EMPTY.withColor(Formatting.RED)))

            }
            ConfigFileStatus.VALID -> {
                // Execute code for VALID
                logger.info("Config file is valid.")
                logger.info("Re-Loading config.json")
                val gson = Gson()
                FileReader(configFile).use { reader ->
                    this.config = gson.fromJson(reader, CobblemonWikiGuiConfig::class.java)
                }

                player?.sendMessage(Text.literal(config?.chatTitle).setStyle(Style.EMPTY.withColor(Formatting.GREEN)).append(Text.literal("Success reload config.json")))
            }
        }
    }


    private fun getRootPath() : Path {
        return this.rootPath
    }

    private fun validateConfigJson(json: String): Boolean {
        return try {
            val jsonParser = Json { ignoreUnknownKeys = true }

            jsonParser.decodeFromString<CobblemonWikiGuiConfig>(json)
            true
        } catch (e: SerializationException) {
            logger.error("Serialization exception! $e")
            e.printStackTrace()
            false
        }
    }

    private fun configFileStatus(): ConfigFileStatus {

        if(!configFileExists()) return ConfigFileStatus.NOT_EXIST
        if(!configFileNotEmpty()) return ConfigFileStatus.EMPTY

        try { //improve it, because any anomaly character in config.json throws error
            if(!validateConfigJson(this.configFile.readText()))

                return ConfigFileStatus.INVALID
        }catch (e: SerializationException){
            return ConfigFileStatus.INVALID
        } catch (e: IllegalArgumentException){
            return ConfigFileStatus.INVALID
        }

        return ConfigFileStatus.VALID
    }

    private enum class ConfigFileStatus(number: Int) {
        NOT_EXIST(1),
        EMPTY(2),
        INVALID(3),
        VALID(4)
    }

    private fun configFileExistsAndNotEmpty(): Boolean {
        return configFileExists() && configFileNotEmpty()
    }

    private fun configFileExists(): Boolean {
        return this.configFile.exists() && this.configFile.length() != 0L
    }

    private fun configFileNotEmpty(): Boolean {
        return this.configFile.length() != 0L
    }

    fun isEnablePermissionNodes(): Boolean {
        return this.config?.isEnablePermissionNodes ?: return false
    }

}