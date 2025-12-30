package com.cwg.mod

import com.cwg.mod.api.permission.PermissionValidator
import com.cwg.mod.command.CobblemonWikiGuiCommands
import com.cwg.mod.config.CobblemonWikiGuiConfig
import com.cwg.mod.config.CobblemonWikiGuiLang
import com.cwg.mod.config.PokemonNamesConfig
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

    const val MOD_ID: String = "cobblemon_wiki_gui"
    const val MOD_NAME: String = "Cobblemon Wiki Gui"
    const val VERSION: String = "2.2.0"
    const val CONFIG_PATH = "config/$MOD_ID/main.json"
    const val CONFIG_LANG_PATH = "config/$MOD_ID/lang.json"

    // Template path for localized Pokemon names - %s will be replaced with locale (e.g., zh_cn, ru_ru)
    const val CONFIG_POKEMON_NAMES_PATH_TEMPLATE = "config/$MOD_ID/pokemon_names_%s.json"

    @JvmField
    val LOGGER: Logger = LogManager.getLogger()
    lateinit var implementation: CobblemonWikiGuiImplementation

    lateinit var config: CobblemonWikiGuiConfig

    lateinit var langConfig: CobblemonWikiGuiLang

    // Initialize with empty config to avoid crashes before server sync
    var pokemonNamesConfig: PokemonNamesConfig = PokemonNamesConfig()

    var permissionValidator: PermissionValidator by Delegates.observable(LaxPermissionValidator().also { it.initialize() }) { _, _, newValue -> newValue.initialize() }

    var isDedicatedServer = false;

    fun preInitialize(implementation: CobblemonWikiGuiImplementation) {
        this.implementation = implementation
        implementation.registerPermissionValidator()
        this.loadConfig()

        // Register custom argument types
        registerArgumentTypes()
    }

    private fun registerArgumentTypes() {
        val localeSpeciesId = net.minecraft.resources.ResourceLocation.fromNamespaceAndPath(MOD_ID, "locale_species")
        implementation.registerCommandArgument(
            localeSpeciesId,
            com.cwg.mod.command.argument.LocaleSpeciesArgumentType::class,
            com.cwg.mod.command.argument.LocaleSpeciesArgumentType.Info()
        )
        LOGGER.info("Registered LocaleSpeciesArgumentType")
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
        // Ensure the loaded Pokemon names config is assigned to the global variable
        this.pokemonNamesConfig = loadPokemonNamesConfig(this.config.language)

        // Log the resulting config size so we can verify it was loaded during initialization
        LOGGER.info("PokemonNamesConfig size after initializeConfig: ${this.pokemonNamesConfig.size()}")

        saveConfig(this.config)
        saveLangConfig(this.langConfig)
        savePokemonNamesConfig(this.pokemonNamesConfig, this.config.language)
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

    /**
     * Load Pokemon names configuration for a specific locale
     * @param locale The locale code (e.g., "zh_cn", "ru_ru", "en_us")
     * @return PokemonNamesConfig instance, or empty config if file doesn't exist
     */
    fun loadPokemonNamesConfig(locale: String): PokemonNamesConfig {
        val normalizedLocale = locale.lowercase()
        val configPath = String.format(CONFIG_POKEMON_NAMES_PATH_TEMPLATE, normalizedLocale)
        val configFile = File(configPath)

        // For en_us (default English), return empty config as no translation needed
        if (normalizedLocale == "en_us") {
            LOGGER.info("English locale detected - no Pokemon name translation needed")
            return PokemonNamesConfig()
        }

        configFile.parentFile?.mkdirs()

        // Log absolute path for debugging
        LOGGER.info("Looking for Pokemon names config at: ${configFile.absolutePath}")
        LOGGER.info("File exists: ${configFile.exists()}, Can read: ${configFile.canRead()}")

        if (configFile.exists()) {
            try {
                val fileReader = FileReader(configFile)
                val map = PokemonNamesConfig.GSON.fromJson(fileReader, Map::class.java) as? Map<String, String>
                fileReader.close()

                if (map != null && map.isNotEmpty()) {
                    LOGGER.info("Loaded ${map.size} Pokemon name mappings for locale: $normalizedLocale")
                    return PokemonNamesConfig.fromMap(map)
                } else {
                    LOGGER.info("Config file exists but is empty or invalid at: ${configFile.absolutePath}")
                }
            } catch (exception: Exception) {
                LOGGER.error("Failed to load Pokemon names config for locale $normalizedLocale:")
                exception.printStackTrace()
            }
        } else {
            LOGGER.info("No Pokemon names config found for locale: $normalizedLocale")
            LOGGER.info("Please place your config file at: ${configFile.absolutePath}")
            // Create an example file for users to fill in
            createExamplePokemonNamesFile(configFile, normalizedLocale)
        }

        // Fallback: search parent directories for a non-empty config file (useful for developer run configs)
        try {
            var dir: File? = File(System.getProperty("user.dir"))?.absoluteFile
            var levels = 0
            while (dir != null && levels < 6) {
                val candidate = File(dir, configPath)
                if (candidate.exists() && candidate.canRead()) {
                    LOGGER.info("Found fallback Pokemon names config at: ${candidate.absolutePath}")
                    val reader = FileReader(candidate)
                    val fallbackMap = PokemonNamesConfig.GSON.fromJson(reader, Map::class.java) as? Map<String, String>
                    reader.close()
                    if (fallbackMap != null && fallbackMap.isNotEmpty()) {
                        LOGGER.info("Loaded ${fallbackMap.size} Pokemon name mappings from fallback for locale: $normalizedLocale")
                        return PokemonNamesConfig.fromMap(fallbackMap)
                    }
                }
                dir = dir.parentFile
                levels++
            }
        } catch (e: Exception) {
            LOGGER.error("Error while searching for fallback Pokemon names config:")
            e.printStackTrace()
        }

        return PokemonNamesConfig()
    }

    /**
     * Create an example Pokemon names configuration file
     * Tries to extract from bundled resources first, otherwise creates a basic example
     */
    private fun createExamplePokemonNamesFile(configFile: File, locale: String) {
        try {
            // Try to extract from bundled resources
            val resourcePath = "/data/cobblemon_wiki_gui/pokemon_names_${locale}.json"
            val resourceStream = this::class.java.getResourceAsStream(resourcePath)

            if (resourceStream != null) {
                LOGGER.info("Found bundled config for locale $locale, extracting...")
                configFile.outputStream().use { output ->
                    resourceStream.use { input ->
                        input.copyTo(output)
                    }
                }
                LOGGER.info("Extracted bundled Pokemon names config for locale $locale to: ${configFile.path}")
            } else {
                // No bundled resource, create basic example
                LOGGER.info("No bundled config found, creating basic example for locale $locale")
                val exampleMap = mapOf(
                    "example_localized_name_1" to "pikachu",
                    "example_localized_name_2" to "charizard",
                    "example_localized_name_3" to "mewtwo"
                )
                val fileWriter = FileWriter(configFile)
                PokemonNamesConfig.GSON.toJson(exampleMap, fileWriter)
                fileWriter.flush()
                fileWriter.close()
                LOGGER.info("Created example Pokemon names config file for locale $locale at: ${configFile.path}")
            }
        } catch (exception: Exception) {
            LOGGER.error("Failed to create example Pokemon names config file:")
            exception.printStackTrace()
        }
    }

    /**
     * Save Pokemon names configuration for a specific locale
     */
    fun savePokemonNamesConfig(config: PokemonNamesConfig, locale: String) {
        try {
            val normalizedLocale = locale.lowercase()
            val configPath = String.format(CONFIG_POKEMON_NAMES_PATH_TEMPLATE, normalizedLocale)
            val configFile = File(configPath)
            configFile.parentFile?.mkdirs()

            val fileWriter = FileWriter(configFile)
            PokemonNamesConfig.GSON.toJson(config.toMap(), fileWriter)
            fileWriter.flush()
            fileWriter.close()
            LOGGER.info("Saved Pokemon names config for locale: $normalizedLocale")
        } catch (exception: Exception) {
            LOGGER.error("Failed to save Pokemon names config:")
            exception.printStackTrace()
        }
    }

}