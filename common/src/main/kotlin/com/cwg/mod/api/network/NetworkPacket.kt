package com.cwg.mod.api.network

// Taken from the Cobblemon project

import com.cobblemon.mod.common.api.net.Encodable
import kotlin.compareTo
import kotlin.times

import com.cobblemon.mod.common.util.server
import com.cwg.mod.CobblemonWikiGuiNetwork
import net.minecraft.network.protocol.common.custom.CustomPacketPayload
import net.minecraft.resources.ResourceKey
import net.minecraft.server.level.ServerPlayer
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.level.Level

interface NetworkPacket<T: NetworkPacket<T>> : CustomPacketPayload, Encodable {

    /**
     *
     */
    val id: ResourceLocation

    /**
     * TODO
     *
     * @param player
     */
    fun sendToPlayer(player: ServerPlayer) = CobblemonWikiGuiNetwork.sendPacketToPlayer(player, this)

    /**
     * TODO
     *
     * @param players
     */
    fun sendToPlayers(players: Iterable<ServerPlayer>) {
        if (players.any()) {
            CobblemonWikiGuiNetwork.sendPacketToPlayers(players, this)
        }
    }

    /**
     * TODO
     *
     */
    fun sendToAllPlayers() = CobblemonWikiGuiNetwork.sendToAllPlayers(this)

    /**
     * TODO
     *
     */
    fun sendToServer() = CobblemonWikiGuiNetwork.sendToServer(this)

    // A copy from PlayerManager#sendToAround to work with our packets
    /**
     * TODO
     *
     * @param x
     * @param y
     * @param z
     * @param distance
     * @param worldKey
     * @param exclusionCondition
     */
    fun sendToPlayersAround(x: Double, y: Double, z: Double, distance: Double, worldKey: ResourceKey<Level>, exclusionCondition: (ServerPlayer) -> Boolean = { false }) {
        val server = server() ?: return
        server.playerList.players.filter { player ->
            if (exclusionCondition.invoke(player))
                return@filter false
            val xDiff = x - player.x
            val yDiff = y - player.y
            val zDiff = z - player.z
            return@filter (xDiff * xDiff + yDiff * yDiff + zDiff) < distance * distance
        }
            .forEach { player -> CobblemonWikiGuiNetwork.sendPacketToPlayer(player, this) }
    }

    override fun type() = CustomPacketPayload.Type<T>(id)
}