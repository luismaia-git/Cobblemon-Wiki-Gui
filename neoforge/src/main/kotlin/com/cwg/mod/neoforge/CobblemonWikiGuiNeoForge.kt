package com.cwg.mod.neoforge

import com.cwg.mod.CobblemonWikiGui
import com.cwg.mod.CobblemonWikiGuiImplementation
import com.cwg.mod.Environment
import com.cwg.mod.ModAPI
import com.cwg.mod.command.CobblemonWikiGuiCommands
import com.cwg.mod.neoforge.client.CobblemonWikiGuiNeoForgeClient
import com.cwg.mod.neoforge.net.CobblemonWikiGuiNeoForgeNetworkManager
import com.cwg.mod.neoforge.permission.ForgePermissionValidator
import com.cwg.mod.net.messages.client.lang.LangSyncPacket
import com.mojang.brigadier.arguments.ArgumentType
import java.util.UUID
import net.minecraft.commands.synchronization.ArgumentTypeInfo
import net.minecraft.commands.synchronization.ArgumentTypeInfos
import net.minecraft.core.registries.Registries
import net.minecraft.resources.ResourceLocation
import net.minecraft.server.MinecraftServer
import net.minecraft.server.level.ServerPlayer
import net.minecraft.server.packs.resources.PreparableReloadListener
import net.neoforged.api.distmarker.Dist
import net.neoforged.fml.ModList
import net.neoforged.fml.common.Mod
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent
import net.neoforged.fml.loading.FMLEnvironment
import net.neoforged.neoforge.common.NeoForge
import net.neoforged.neoforge.event.AddReloadListenerEvent
import net.neoforged.neoforge.event.OnDatapackSyncEvent
import net.neoforged.neoforge.event.RegisterCommandsEvent
import net.neoforged.neoforge.event.entity.player.PlayerEvent
import net.neoforged.neoforge.registries.DeferredRegister
import net.neoforged.neoforge.server.ServerLifecycleHooks
import thedarkcolour.kotlinforforge.neoforge.forge.MOD_BUS
import kotlin.reflect.KClass

@Mod(CobblemonWikiGui.MOD_ID)
class CobblemonWikiGuiNeoForge : CobblemonWikiGuiImplementation {
    override val modAPI = ModAPI.NEOFORGE

    private val hasBeenSynced = hashSetOf<UUID>()

    private val commandArgumentTypes = DeferredRegister.create(Registries.COMMAND_ARGUMENT_TYPE, CobblemonWikiGui.MOD_ID)
    private val reloadableResources = arrayListOf<PreparableReloadListener>()
    private val queuedWork = arrayListOf<() -> Unit>()


    override val networkManager = CobblemonWikiGuiNeoForgeNetworkManager


    init {
        with(MOD_BUS) {
            this@CobblemonWikiGuiNeoForge.commandArgumentTypes.register(this)
            addListener(this@CobblemonWikiGuiNeoForge::initialize)
            CobblemonWikiGui.preInitialize(this@CobblemonWikiGuiNeoForge)
            addListener(networkManager::registerMessages)
        }
        with(NeoForge.EVENT_BUS) {
            addListener(::onDatapackSync)
            addListener(::onLogin)
            addListener(::onLogout)
            addListener(::registerCommands)
            addListener(::onReload)
        }

        if (FMLEnvironment.dist == Dist.CLIENT) {
            CobblemonWikiGuiNeoForgeClient.init()
        }
    }

    fun initialize(event: FMLCommonSetupEvent) {
        CobblemonWikiGui.LOGGER.info("Initializing...")
        event.enqueueWork {
            this.queuedWork.forEach { it.invoke() }
        }
        CobblemonWikiGui.initialize()
    }


    fun onDatapackSync(event: OnDatapackSyncEvent) {
        if (event.player != null) {
            sendLangConfigToPlayer(event.player!!)
        } else {
            event.playerList.players.forEach { this.sendLangConfigToPlayer(it) }
        }
    }

    private fun sendLangConfigToPlayer(player: ServerPlayer) {
        val langMap = CobblemonWikiGui.langConfig.toMap()
        LangSyncPacket(langMap).sendToPlayer(player)
    }

    fun onLogin(event: PlayerEvent.PlayerLoggedInEvent) {
        this.hasBeenSynced.add(event.entity.uuid)

        if (event.entity is ServerPlayer) {
            sendLangConfigToPlayer(event.entity as ServerPlayer)
        }
    }

    fun onLogout(event: PlayerEvent.PlayerLoggedOutEvent) {
        this.hasBeenSynced.remove(event.entity.uuid)
    }

    override fun isModInstalled(id: String) = ModList.get().isLoaded(id)

    override fun environment(): Environment {
        return if (FMLEnvironment.dist.isClient) Environment.CLIENT else Environment.SERVER
    }

    override fun registerPermissionValidator() {
        CobblemonWikiGui.permissionValidator = ForgePermissionValidator
    }


    override fun <A : ArgumentType<*>, T : ArgumentTypeInfo.Template<A>> registerCommandArgument(identifier: ResourceLocation, argumentClass: KClass<A>, serializer: ArgumentTypeInfo<A, T>) {

        //This is technically a supplier not a function (it is unused), but we need to explicitly say whether its a supplier or a function
        //Idk how to explicitly say its a supplier, so lets just make it a function by specifying a param
        this.commandArgumentTypes.register(identifier.path) { it ->
            ArgumentTypeInfos.registerByClass(argumentClass.java, serializer)
        }
    }

    private fun registerCommands(e: RegisterCommandsEvent) {
        CobblemonWikiGuiCommands.register(e.dispatcher, e.buildContext, e.commandSelection)
    }



    private fun onReload(e: AddReloadListenerEvent) {
        this.reloadableResources.forEach(e::addListener)
    }

    override fun server(): MinecraftServer? = ServerLifecycleHooks.getCurrentServer()


}