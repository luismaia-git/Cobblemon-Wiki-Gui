package com.cwg.mod.neoforge.net

import com.cwg.mod.CobblemonWikiGui
import com.cwg.mod.CobblemonWikiGuiNetwork
import com.cwg.mod.NetworkManager
import com.cwg.mod.api.network.NetworkPacket
import com.cwg.mod.client.net.data.DataRegistrySyncPacketHandler
import net.minecraft.client.Minecraft
import net.minecraft.resources.ResourceLocation
import net.minecraft.server.level.ServerPlayer
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent
import net.neoforged.neoforge.network.registration.HandlerThread

// https://neoforged.net/news/20.4networking-rework/
object CobblemonWikiGuiNeoForgeNetworkManager : NetworkManager {
    const val PROTOCOL_VERSION = "1.0.0"

    fun registerMessages(event: RegisterPayloadHandlersEvent) {
        val registrar = event
            .registrar(CobblemonWikiGui.MOD_ID)
            .versioned(PROTOCOL_VERSION)

        val netRegistrar = event
            .registrar(CobblemonWikiGui.MOD_ID)
            .versioned(PROTOCOL_VERSION)
            .executesOn(HandlerThread.NETWORK)

        val syncPackets = HashSet<ResourceLocation>()
        val asyncPackets = HashSet<ResourceLocation>()

        CobblemonWikiGuiNetwork.s2cPayloads.map { NeoForgePacketInfo(it) }.forEach {
            val handleAsync = it.info.handler is DataRegistrySyncPacketHandler<*, *>
            if (handleAsync) asyncPackets += it.info.id
            else syncPackets += it.info.id

            it.registerToClient(if (handleAsync) netRegistrar else registrar)
        }
        CobblemonWikiGuiNetwork.c2sPayloads.map { NeoForgePacketInfo(it) }.forEach { it.registerToServer(registrar) }
    }

    override fun sendPacketToPlayer(player: ServerPlayer, packet: NetworkPacket<*>) {
        player.connection.send(packet)
    }

    override fun sendToServer(packet: NetworkPacket<*>) {
        Minecraft.getInstance().connection?.send(packet)
    }
}