package com.cwg.mod

import com.cwg.mod.api.network.NetworkPacket
import com.mojang.brigadier.arguments.ArgumentType
import net.minecraft.commands.synchronization.ArgumentTypeInfo
import net.minecraft.resources.ResourceLocation
import net.minecraft.server.MinecraftServer
import net.minecraft.server.level.ServerPlayer
import kotlin.reflect.KClass

interface CobblemonWikiGuiImplementation {
    val modAPI: ModAPI;

    fun environment(): Environment

    val networkManager: NetworkManager

    fun isModInstalled(id: String): Boolean

    fun registerPermissionValidator()

    fun <A : ArgumentType<*>, T : ArgumentTypeInfo.Template<A>> registerCommandArgument(identifier: ResourceLocation, argumentClass: KClass<A>, serializer: ArgumentTypeInfo<A, T>)

    fun server(): MinecraftServer?
}

enum class ModAPI {
    FABRIC,
    NEOFORGE
}

enum class Environment {
    CLIENT,
    SERVER
}

interface NetworkManager {
    fun sendPacketToPlayer(player: ServerPlayer, packet: NetworkPacket<*>)

    fun sendToServer(packet: NetworkPacket<*>)
}
