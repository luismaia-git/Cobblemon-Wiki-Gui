package com.cwg.mod.api.network

import net.minecraft.server.MinecraftServer
import net.minecraft.server.level.ServerPlayer

interface ServerNetworkPacketHandler<T: NetworkPacket<T>>: PacketHandler<T> {
    fun handle(packet: T, server: MinecraftServer, player: ServerPlayer)
}