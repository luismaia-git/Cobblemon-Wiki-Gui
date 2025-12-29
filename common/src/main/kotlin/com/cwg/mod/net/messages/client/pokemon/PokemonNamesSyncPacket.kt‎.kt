package com.cwg.mod.net.messages.client.pokemon


import com.cwg.mod.api.network.NetworkPacket
import com.cwg.mod.util.cobblemonWikiResource
import net.minecraft.network.RegistryFriendlyByteBuf
import java.util.Collections

/**
 * Packet for synchronizing Pokemon name mappings from server to client
 * Supports multiple languages based on client locale settings
 */
class PokemonNamesSyncPacket(
    val pokemonNames: Map<String, String>
) : NetworkPacket<PokemonNamesSyncPacket> {
    override val id = ID

    override fun encode(buffer: RegistryFriendlyByteBuf) {
        buffer.writeInt(pokemonNames.size)
        pokemonNames.forEach { (localizedName, englishName) ->
            buffer.writeUtf(localizedName)
            buffer.writeUtf(englishName)
        }
    }

    companion object {
        val ID = cobblemonWikiResource("pokemon_names_sync")

        fun decode(buffer: RegistryFriendlyByteBuf): PokemonNamesSyncPacket {
            val size = buffer.readInt()
            val pokemonNames = mutableMapOf<String, String>()

            repeat(size) {
                val localizedName = buffer.readUtf()
                val englishName = buffer.readUtf()
                pokemonNames[localizedName] = englishName
            }

            return PokemonNamesSyncPacket(Collections.unmodifiableMap(pokemonNames))
        }
    }
}