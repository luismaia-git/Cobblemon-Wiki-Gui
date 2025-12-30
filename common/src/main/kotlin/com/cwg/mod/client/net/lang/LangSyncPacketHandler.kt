package com.cwg.mod.client.net.lang

import com.cwg.mod.CobblemonWikiGui
import com.cwg.mod.api.network.ClientNetworkPacketHandler
import com.cwg.mod.config.CobblemonWikiGuiLang
import com.cwg.mod.net.messages.client.lang.LangSyncPacket
import com.cwg.mod.net.messages.client.pokemon.PokemonNamesSyncPacket
import net.minecraft.client.Minecraft

object LangSyncPacketHandler : ClientNetworkPacketHandler<LangSyncPacket> {
    override fun handle(packet: LangSyncPacket, client: Minecraft) {
        client.execute {
            CobblemonWikiGui.langConfig = CobblemonWikiGuiLang.fromMap(packet.langStrings)
        }
    }
}