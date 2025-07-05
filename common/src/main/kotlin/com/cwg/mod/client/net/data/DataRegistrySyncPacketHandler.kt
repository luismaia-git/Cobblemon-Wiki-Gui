package com.cwg.mod.client.net.data

import com.cwg.mod.api.network.ClientNetworkPacketHandler
import com.cwg.mod.net.messages.client.data.DataRegistrySyncPacket
import net.minecraft.client.Minecraft
import net.minecraft.network.RegistryFriendlyByteBuf

class DataRegistrySyncPacketHandler<P, T : DataRegistrySyncPacket<P, T>> : ClientNetworkPacketHandler<T> {
    override fun handle(packet: T, client: Minecraft) {
        val buffer = requireNotNull(packet.buffer) { "Buffer missing on DataRegistrySyncPacket" }

        packet.entries.clear()
        packet.entries.addAll(buffer.readList { buf ->
            val entry = packet.decodeEntry(buf as RegistryFriendlyByteBuf)
            entry
        }.filterNotNull())
        buffer.release()
        packet.synchronizeDecoded(packet.entries)
    }
}