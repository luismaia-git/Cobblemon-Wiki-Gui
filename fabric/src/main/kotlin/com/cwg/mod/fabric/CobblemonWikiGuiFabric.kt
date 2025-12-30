package com.cwg.mod.fabric;

import com.cwg.mod.CobblemonWikiGui;
import com.cwg.mod.CobblemonWikiGuiImplementation
import com.cwg.mod.Environment
import com.cwg.mod.ModAPI
import com.cwg.mod.command.CobblemonWikiGuiCommands
import com.cwg.mod.fabric.net.CobblemonWikiGuiFabricNetworkManager
import com.cwg.mod.fabric.permission.FabricPermissionValidator
import com.cwg.mod.net.messages.client.lang.LangSyncPacket
import com.cwg.mod.net.messages.client.pokemon.PokemonNamesSyncPacket
import com.mojang.brigadier.arguments.ArgumentType
import net.fabricmc.api.EnvType
import net.fabricmc.fabric.api.command.v2.ArgumentTypeRegistry
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents
import net.fabricmc.loader.api.FabricLoader
import net.minecraft.client.Minecraft
import net.minecraft.commands.synchronization.ArgumentTypeInfo
import net.minecraft.resources.ResourceLocation
import net.minecraft.server.MinecraftServer
import kotlin.reflect.KClass

object CobblemonWikiGuiFabric : CobblemonWikiGuiImplementation {
    override val modAPI = ModAPI.FABRIC

    private var server: MinecraftServer? = null

    override val networkManager = CobblemonWikiGuiFabricNetworkManager

    fun initialize() {
        CobblemonWikiGui.preInitialize(this)
        CobblemonWikiGui.initialize()

        networkManager.registerMessages()

        networkManager.registerServerHandlers()
        registerCommands()

        ServerPlayConnectionEvents.JOIN.register { handler, _, _ ->
            val player = handler.player

            // Send language config
            val langMap = CobblemonWikiGui.langConfig.toMap()
            LangSyncPacket(langMap).sendToPlayer(player)

            // Send pokemon names config mappings so the client has localized names
            try {
                val pokemonMap = CobblemonWikiGui.pokemonNamesConfig.toMap()
                PokemonNamesSyncPacket(pokemonMap).sendToPlayer(player)
            } catch (e: Exception) {
                CobblemonWikiGui.LOGGER.error("Failed to send Pokemon names sync packet to player: ", e)
            }

            // TODO: Implement client-to-server locale packet to get actual client language
            // For now, we'll need to check config files or wait for client to send its locale
            // This can be implemented by creating a ClientLocalePacket that the client sends on join
        }

    }

    fun registerCommands() {
        CommandRegistrationCallback.EVENT.register(CobblemonWikiGuiCommands::register)
    }

    override fun isModInstalled(id: String) = FabricLoader.getInstance().isModLoaded(id)

    override fun environment(): Environment {
        return when(FabricLoader.getInstance().environmentType) {
            EnvType.CLIENT -> Environment.CLIENT
            EnvType.SERVER -> Environment.SERVER
            else -> throw IllegalStateException("Fabric implementation cannot resolve environment yet")
        }
    }

    override fun registerPermissionValidator() {
        if (this.isModInstalled("fabric-permissions-api-v0")) {
            CobblemonWikiGui.permissionValidator = FabricPermissionValidator()
        }
    }

    override fun <A : ArgumentType<*>, T : ArgumentTypeInfo.Template<A>> registerCommandArgument(identifier: ResourceLocation, argumentClass: KClass<A>, serializer: ArgumentTypeInfo<A, T>) {
        ArgumentTypeRegistry.registerArgumentType(identifier, argumentClass.java, serializer)
    }


    override fun server(): MinecraftServer? = if (this.environment() == Environment.CLIENT) Minecraft.getInstance().singleplayerServer else this.server



}
