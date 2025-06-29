package com.cwg.mod

import com.cwg.mod.api.network.NetworkPacket
import com.cwg.mod.client.net.lang.LangSyncPacketHandler
import com.cwg.mod.net.PacketRegisterInfo
import com.cwg.mod.net.messages.client.lang.LangSyncPacket
import com.cwg.mod.util.server
import net.minecraft.server.level.ServerPlayer

object CobblemonWikiGuiNetwork {

    fun ServerPlayer.sendPacket(packet: NetworkPacket<*>) {
        sendPacketToPlayer(this, packet)
    }
    fun sendToServer(packet: NetworkPacket<*>) {
        CobblemonWikiGui.implementation.networkManager.sendToServer(packet)
    }
    fun sendToAllPlayers(packet: NetworkPacket<*>) = sendPacketToPlayers(server()!!.playerList.players, packet)
    fun sendPacketToPlayers(players: Iterable<ServerPlayer>, packet: NetworkPacket<*>) = players.forEach { sendPacketToPlayer(it, packet) }

    val s2cPayloads = generateS2CPacketInfoList()
    val c2sPayloads = generateC2SPacketInfoList()

    private fun generateS2CPacketInfoList(): List<PacketRegisterInfo<*>> {
        val list = mutableListOf<PacketRegisterInfo<*>>()

        // Settings packets
        list.add(PacketRegisterInfo(LangSyncPacket.ID, LangSyncPacket::decode, LangSyncPacketHandler))

        return list
    }

    private fun generateC2SPacketInfoList(): List<PacketRegisterInfo<*>> {
        val list = mutableListOf<PacketRegisterInfo<*>>()

        return list
    }

    fun sendPacketToPlayer(player: ServerPlayer, packet: NetworkPacket<*>) {
        CobblemonWikiGui.implementation.networkManager.sendPacketToPlayer(player, packet)
    }
}
