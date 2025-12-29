package com.cwg.mod.server.net

import com.cwg.mod.CobblemonWikiGui
import com.cwg.mod.api.network.ServerNetworkPacketHandler
import com.cwg.mod.net.messages.client.pokemon.PokemonNamesSyncPacket
import com.cwg.mod.net.messages.server.ClientLocalePacket
import net.minecraft.server.MinecraftServer
import net.minecraft.server.level.ServerPlayer

/**
 * Server-side handler for client locale packet
 * When received, loads and sends appropriate Pokemon names for the client's locale
 */
object ClientLocalePacketHandler : ServerNetworkPacketHandler<ClientLocalePacket> {
    override fun handle(packet: ClientLocalePacket, server: MinecraftServer, player: ServerPlayer) {
        server.execute {
            val locale = packet.locale.lowercase()
            CobblemonWikiGui.LOGGER.info("Received locale '$locale' from player ${player.name.string}")

            // Load Pokemon names config for this locale
            val pokemonNamesConfig = CobblemonWikiGui.loadPokemonNamesConfig(locale)

            // Send to player if config has mappings
            if (pokemonNamesConfig.size() > 0) {
                PokemonNamesSyncPacket(pokemonNamesConfig.toMap()).sendToPlayer(player)
                CobblemonWikiGui.LOGGER.info("Sent ${pokemonNamesConfig.size()} Pokemon name mappings for locale '$locale' to player ${player.name.string}")
            } else {
                CobblemonWikiGui.LOGGER.info("No Pokemon name mappings found for locale '$locale'")
            }
        }
    }
}

