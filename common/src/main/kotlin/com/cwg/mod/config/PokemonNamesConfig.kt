package com.cwg.mod.config;

import com.google.gson.GsonBuilder;

/**
 * Pokemon localized names mapping configuration
 * Manages the mapping between localized names and English names for multiple languages
 */
class PokemonNamesConfig {

    companion object {
        val GSON = GsonBuilder()
                .disableHtmlEscaping()
                .setPrettyPrinting()
                .create()

        /**
         * Create a config instance from a map
         * @param map Map of localized name to English name
         * @return PokemonNamesConfig instance
         */
        fun fromMap(map: Map<String, String>): PokemonNamesConfig {
            val config = PokemonNamesConfig()
            config.pokemonNames.putAll(map)
            return config
        }
    }

    /**
     * Localized name to English name mapping
     * Key: Localized name (e.g., Chinese, Russian), Value: English name
     */
    private val pokemonNames: MutableMap<String, String> = mutableMapOf()

    /**
     * Get English name from localized name
     * @param localizedName Localized Pokemon name
     * @return Corresponding English name, or null if not found
     */
    fun getEnglishName(localizedName: String): String? {
        return pokemonNames[localizedName]
    }

    /**
     * Add or update name mapping
     * @param localizedName Localized Pokemon name
     * @param englishName English Pokemon name
     */
    fun addMapping(localizedName: String, englishName: String) {
        pokemonNames[localizedName] = englishName
    }

    /**
     * Remove name mapping
     * @param localizedName Localized Pokemon name
     */
    fun removeMapping(localizedName: String) {
        pokemonNames.remove(localizedName)
    }

    /**
     * Get all localized names
     * @return Set of all localized names
     */
    fun getAllLocalizedNames(): Set<String> {
        return pokemonNames.keys.toSet()
    }

    /**
     * Get all English names
     * @return Set of all English names
     */
    fun getAllEnglishNames(): Set<String> {
        return pokemonNames.values.toSet()
    }

    /**
     * Check if contains a localized name
     * @param localizedName Localized Pokemon name
     * @return true if exists, false otherwise
     */
    fun containsLocalizedName(localizedName: String): Boolean {
        return pokemonNames.containsKey(localizedName)
    }

    /**
     * Get mapping count
     * @return Total number of mappings
     */
    fun size(): Int {
        return pokemonNames.size
    }

    /**
     * Clear all mappings
     */
    fun clear() {
        pokemonNames.clear()
    }

    /**
     * Convert to Map format
     * @return Map containing all mappings
     */
    fun toMap(): Map<String, String> {
        return pokemonNames.toMap()
    }

    /**
     * Fuzzy match localized names
     * @param partialName Partial localized name
     * @return List of matching localized names
     */
    fun fuzzyMatchLocalizedNames(partialName: String): List<String> {
        return pokemonNames.keys.filter { it.contains(partialName) }.sorted()
    }

    /**
     * Get localized names starting with prefix
     * @param prefix Name prefix
     * @return List of matching localized names
     */
    fun getLocalizedNamesStartingWith(prefix: String): List<String> {
        return pokemonNames.keys.filter { it.startsWith(prefix) }.sorted()
    }
}
