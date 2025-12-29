package com.cwg.mod.client.net.pokemon

import com.cwg.mod.CobblemonWikiGui
import com.cwg.mod.api.network.ClientNetworkPacketHandler
import com.cwg.mod.config.PokemonNamesConfig
import com.cwg.mod.net.messages.client.pokemon.PokemonNamesSyncPacket
import net.minecraft.client.Minecraft

/**
 * Client-side handler for Pokemon name synchronization packet
 * Handles localized Pokemon name mappings from server
 */
object PokemonNamesSyncPacketHandler : ClientNetworkPacketHandler<PokemonNamesSyncPacket> {
    override fun handle(packet: PokemonNamesSyncPacket, client: Minecraft) {
        client.execute {
            CobblemonWikiGui.pokemonNamesConfig = PokemonNamesConfig.fromMap(packet.pokemonNames)
            CobblemonWikiGui.LOGGER.info("Synchronized ${packet.pokemonNames.size} Pokemon name mappings from server")
        }
    }
}