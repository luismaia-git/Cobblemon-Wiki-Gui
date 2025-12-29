package com.cwg.mod.net.messages.server

import com.cwg.mod.api.network.NetworkPacket
import com.cwg.mod.util.cobblemonWikiResource
import net.minecraft.network.RegistryFriendlyByteBuf

/**
 * Packet sent from client to server to inform the server of the client's locale
 * This allows the server to send the appropriate localized Pokemon names
 */
class ClientLocalePacket(
    val locale: String
) : NetworkPacket<ClientLocalePacket> {
    override val id = ID

    override fun encode(buffer: RegistryFriendlyByteBuf) {
        buffer.writeUtf(locale)
    }

    companion object {
        val ID = cobblemonWikiResource("client_locale")

        fun decode(buffer: RegistryFriendlyByteBuf): ClientLocalePacket {
            val locale = buffer.readUtf()
            return ClientLocalePacket(locale)
        }
    }
}

