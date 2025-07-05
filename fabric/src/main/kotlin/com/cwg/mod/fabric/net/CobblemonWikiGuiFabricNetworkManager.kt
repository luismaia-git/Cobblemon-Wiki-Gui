package com.cwg.mod.fabric.net

import com.cwg.mod.CobblemonWikiGuiNetwork
import com.cwg.mod.NetworkManager
import com.cwg.mod.api.network.NetworkPacket
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking
import net.minecraft.server.level.ServerPlayer

object CobblemonWikiGuiFabricNetworkManager : NetworkManager {
    fun registerMessages() {
        CobblemonWikiGuiNetwork.s2cPayloads.map { FabricPacketInfo(it) }.forEach { it.registerPacket(client = true) }
        CobblemonWikiGuiNetwork.c2sPayloads.map { FabricPacketInfo(it) }.forEach { it.registerPacket(client = false) }
    }

    fun registerClientHandlers() {
        CobblemonWikiGuiNetwork.s2cPayloads.map { FabricPacketInfo(it) }.forEach { it.registerClientHandler() }
    }

    fun registerServerHandlers() {
        CobblemonWikiGuiNetwork.c2sPayloads.map { FabricPacketInfo(it) }.forEach { it.registerServerHandler() }
    }

    override fun sendPacketToPlayer(player: ServerPlayer, packet: NetworkPacket<*>) {
        ServerPlayNetworking.send(player, packet)
    }

    override fun sendToServer(packet: NetworkPacket<*>) {
        ClientPlayNetworking.send(packet)
    }
}