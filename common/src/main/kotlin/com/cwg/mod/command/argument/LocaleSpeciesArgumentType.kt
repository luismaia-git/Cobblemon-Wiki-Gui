package com.cwg.mod.command.argument

import com.cobblemon.mod.common.Cobblemon
import com.cobblemon.mod.common.api.pokemon.PokemonSpecies
import com.cobblemon.mod.common.pokemon.Species
import com.cobblemon.mod.common.util.asIdentifierDefaultingNamespace
import com.cwg.mod.CobblemonWikiGui
import com.google.gson.JsonObject
import com.mojang.brigadier.StringReader
import com.mojang.brigadier.arguments.ArgumentType
import com.mojang.brigadier.context.CommandContext
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType
import com.mojang.brigadier.suggestion.Suggestions
import com.mojang.brigadier.suggestion.SuggestionsBuilder
import net.minecraft.commands.SharedSuggestionProvider
import net.minecraft.commands.synchronization.ArgumentTypeInfo
import net.minecraft.network.FriendlyByteBuf
import net.minecraft.network.chat.Component
import java.util.concurrent.CompletableFuture



/**
 * Pokemon species argument type with localized name support
 *
 * This ArgumentType extends the original SpeciesArgumentType functionality,
 * supporting Pokemon queries using localized names (Chinese, Russian, etc.)
 * while maintaining full compatibility with English names.
 *
 * Supported languages are automatically loaded based on client locale.
 */
class LocaleSpeciesArgumentType : ArgumentType<Species> {

    companion object {
        val EXAMPLES: List<String> = listOf("pikachu", "皮卡丘", "Пикачу")
        val INVALID_POKEMON = Component.translatable("cobblemon.command.pokespawn.invalid-pokemon")

        /**
         * Create a ChineseSpeciesArgumentType instance
         */
        fun species(): LocaleSpeciesArgumentType {
            return LocaleSpeciesArgumentType()
        }

        /**
         * Get Pokemon species from command context
         */
        fun getPokemon(context: CommandContext<*>, name: String): Species {
            return context.getArgument(name, Species::class.java)
        }
    }

    override fun parse(reader: StringReader): Species {
        val start = reader.cursor

        // First, try to read a complete parameter (may contain localized characters)
        val input = if (reader.canRead() && reader.peek() != ' ') {
            // Read until next space or end of string
            val startPos = reader.cursor
            while (reader.canRead() && reader.peek() != ' ') {
                reader.skip()
            }
            val endPos = reader.cursor
            reader.string.substring(startPos, endPos)
        } else {
            ""
        }

        // Debug info
        CobblemonWikiGui.LOGGER.info("LocalizedSpeciesArgumentType: Parsing input '$input'")
        CobblemonWikiGui.LOGGER.info("LocalizedSpeciesArgumentType: Config size ${CobblemonWikiGui.pokemonNamesConfig.size()}")

        // First, try localized name lookup
        val englishName = CobblemonWikiGui.pokemonNamesConfig.getEnglishName(input)
        CobblemonWikiGui.LOGGER.info("LocalizedSpeciesArgumentType: Localized name '$input' mapped to English name '$englishName'")

        if (englishName != null) {
            val species = PokemonSpecies.getByName(englishName)
            CobblemonWikiGui.LOGGER.info("LocalizedSpeciesArgumentType: Found species from English name '$englishName': $species")
            if (species != null) {
                return species
            }
        }

        // If localized lookup fails, try standard parsing (supports namespaces and case handling)
        try {
            reader.cursor = start // Reset read position
            val species = PokemonSpecies.getByIdentifier(reader.asIdentifierDefaultingNamespace())
            CobblemonWikiGui.LOGGER.info("LocalizedSpeciesArgumentType: Found species using standard parsing: $species")
            if (species != null) {
                return species
            }
        } catch (e: Exception) {
            CobblemonWikiGui.LOGGER.info("LocalizedSpeciesArgumentType: Standard parsing failed: ${e.message}")
        }

        // Finally, try direct name lookup (compatibility)
        val species = PokemonSpecies.getByName(input)
        CobblemonWikiGui.LOGGER.info("LocalizedSpeciesArgumentType: Direct name lookup result: $species")
        if (species != null) {
            return species
        }

        // If nothing found, throw exception
        CobblemonWikiGui.LOGGER.info("LocalizedSpeciesArgumentType: All parsing methods failed, throwing exception")
        throw SimpleCommandExceptionType(INVALID_POKEMON).createWithContext(reader)
    }

    override fun <S> listSuggestions(
        context: CommandContext<S>,
        builder: SuggestionsBuilder
    ): CompletableFuture<Suggestions> {
        val remaining = builder.remaining.lowercase()

        // Collect all suggestions
        val suggestions = mutableListOf<String>()

        // Add localized name suggestions
        CobblemonWikiGui.pokemonNamesConfig.getAllLocalizedNames()
            .filter { it.lowercase().startsWith(remaining) || it.contains(remaining) }
            .take(10) // Limit localized suggestions
            .forEach { suggestions.add(it) }

        // Add English name suggestions (using same logic as original SpeciesArgumentType)
        PokemonSpecies.species
            .map {
                if (it.resourceIdentifier.namespace == Cobblemon.MODID)
                    it.resourceIdentifier.path
                else
                    it.resourceIdentifier.toString()
            }
            .filter { it.lowercase().startsWith(remaining) }
            .take(10) // Limit English suggestions
            .forEach { suggestions.add(it) }

        // Use SharedSuggestionProvider to ensure correct suggestion format
        return SharedSuggestionProvider.suggest(suggestions, builder)
    }

    override fun getExamples(): Collection<String> {
        return EXAMPLES
    }

    /**
     * ArgumentTypeInfo for serializing LocaleSpeciesArgumentType
     */
    class Info : ArgumentTypeInfo<LocaleSpeciesArgumentType, Info.Template> {
        override fun serializeToNetwork(template: Template, buffer: FriendlyByteBuf) {
            // No data to serialize - this argument type has no parameters
        }

        override fun deserializeFromNetwork(buffer: FriendlyByteBuf): Template {
            return Template()
        }

        override fun serializeToJson(template: Template, json: JsonObject) {
            // No data to serialize - this argument type has no parameters
        }

        override fun unpack(argumentType: LocaleSpeciesArgumentType): Template {
            return Template()
        }

        inner class Template : ArgumentTypeInfo.Template<LocaleSpeciesArgumentType> {
            override fun instantiate(commandBuildContext: net.minecraft.commands.CommandBuildContext): LocaleSpeciesArgumentType {
                return LocaleSpeciesArgumentType()
            }

            override fun type(): ArgumentTypeInfo<LocaleSpeciesArgumentType, *> {
                return this@Info
            }
        }
    }
}