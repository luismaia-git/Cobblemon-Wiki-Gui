package com.cwg.mod.net.messages.client.lang

import com.cwg.mod.api.network.NetworkPacket
import com.cwg.mod.util.cobblemonWikiResource
import net.minecraft.network.RegistryFriendlyByteBuf
import java.util.Collections

/**
 * A packet that will sync language configuration strings from the server to the client.
 */
class LangSyncPacket(
    val langStrings: Map<String, String>
) : NetworkPacket<LangSyncPacket> {
    override val id = ID

    override fun encode(buffer: RegistryFriendlyByteBuf) {
        buffer.writeInt(langStrings.size)
        langStrings.forEach { (key, value) ->
            buffer.writeUtf(key)
            buffer.writeUtf(value)
        }
    }

    companion object {
        val ID = cobblemonWikiResource("lang_sync_packet")

        fun decode(buffer: RegistryFriendlyByteBuf): LangSyncPacket {
            val size = buffer.readInt()
            val receivedMap = mutableMapOf<String, String>()
            repeat(size) {
                val key = buffer.readUtf()
                val value = buffer.readUtf()
                receivedMap[key] = value
            }
            return LangSyncPacket(Collections.unmodifiableMap(receivedMap))
        }
    }
}