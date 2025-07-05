package com.cwg.mod.neoforge.net
//taken from Cobblemon Project

import com.cwg.mod.api.network.ClientNetworkPacketHandler
import com.cwg.mod.api.network.NetworkPacket
import com.cwg.mod.api.network.ServerNetworkPacketHandler
import com.cwg.mod.net.PacketRegisterInfo
import net.minecraft.client.Minecraft
import net.minecraft.server.level.ServerPlayer
import net.neoforged.neoforge.network.handling.IPayloadHandler
import net.neoforged.neoforge.network.registration.PayloadRegistrar

class NeoForgePacketInfo<T : NetworkPacket<T>>(val info: PacketRegisterInfo<T>) {
    fun registerToClient(registrar: PayloadRegistrar) {
        val handler = IPayloadHandler<T> { arg, _ ->
            val clientHandler = info.handler as ClientNetworkPacketHandler<T>
            clientHandler.handle(arg, Minecraft.getInstance())
        }

        registrar.playToClient(info.payloadId, info.codec, handler)
    }

    fun registerToServer(registrar: PayloadRegistrar) {
        val handler = IPayloadHandler<T> { arg, ctx ->
            val serverHandler = info.handler as ServerNetworkPacketHandler<T>
            serverHandler.handle(arg, ctx.player().server!!, ctx.player() as ServerPlayer)
        }

        registrar.playToServer(info.payloadId, info.codec, handler)
    }
}