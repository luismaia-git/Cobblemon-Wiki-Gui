package com.cwg.mod.net
// Taken from the Cobblemon project

import com.cwg.mod.api.network.NetworkPacket
import com.cwg.mod.api.network.PacketHandler
import net.minecraft.network.RegistryFriendlyByteBuf
import net.minecraft.network.codec.StreamCodec
import net.minecraft.network.protocol.common.custom.CustomPacketPayload
import net.minecraft.resources.ResourceLocation

class PacketRegisterInfo<T : NetworkPacket<T>>(
    val id: ResourceLocation,
    val decoder: (RegistryFriendlyByteBuf) -> T,
    val handler: PacketHandler<T>,
    codec: StreamCodec<RegistryFriendlyByteBuf, T>? = null
) {
    val payloadId = CustomPacketPayload.Type<T>(id)
    val codec = codec ?: StreamCodec.of(
        { buf, packet -> packet.encode(buf) },
        decoder
    )
}