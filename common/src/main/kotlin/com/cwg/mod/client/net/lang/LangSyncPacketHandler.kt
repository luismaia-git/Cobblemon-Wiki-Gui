package com.cwg.mod.client.net.lang


import com.cwg.mod.CobblemonWikiGui
import com.cwg.mod.api.network.ClientNetworkPacketHandler
import com.cwg.mod.config.CobblemonWikiGuiLang
import com.cwg.mod.net.messages.client.lang.LangSyncPacket
import net.minecraft.client.Minecraft

object LangSyncPacketHandler : ClientNetworkPacketHandler<LangSyncPacket> {
    override fun handle(packet: LangSyncPacket, client: Minecraft) {
        // Garante que a atualização ocorra na thread principal do Minecraft (Client Thread)
        client.execute {
            // Atualiza a instância de langConfig na sua classe principal do mod
            // usando o mapa de strings recebido do pacote.
            CobblemonWikiGui.langConfig = CobblemonWikiGuiLang.fromMap(packet.langStrings)
            println("Cliente recebeu ${packet.langStrings.size} strings de idioma sincronizadas do servidor.")
        }
    }
}