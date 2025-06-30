
package com.cwg.mod.neoforge.permission

import com.cwg.mod.CobblemonWikiGui
import com.cwg.mod.api.permission.CobblemonWikiGuiPermissions
import com.cwg.mod.api.permission.Permission
import com.cwg.mod.api.permission.PermissionValidator
import net.minecraft.commands.CommandSourceStack
import net.minecraft.resources.ResourceLocation
import net.minecraft.server.level.ServerPlayer
import net.neoforged.neoforge.common.NeoForge
import net.neoforged.neoforge.server.permission.PermissionAPI
import net.neoforged.neoforge.server.permission.events.PermissionGatherEvent
import net.neoforged.neoforge.server.permission.nodes.PermissionNode
import net.neoforged.neoforge.server.permission.nodes.PermissionTypes

object ForgePermissionValidator : PermissionValidator {

    private val nodes = hashMapOf<ResourceLocation, PermissionNode<Boolean>>()

    init {
        NeoForge.EVENT_BUS.addListener<PermissionGatherEvent.Nodes> { event ->
            CobblemonWikiGui.LOGGER.info("Starting Forge permission node registry")
            event.addNodes(this.createNodes())
            CobblemonWikiGui.LOGGER.debug("Finished Forge permission node registry")
        }
    }

    override fun initialize() {
        CobblemonWikiGui.LOGGER.info("Booting ForgePermissionApiPermissionValidator, player permissions will be checked using MinecraftForge' PermissionAPI, non player command sources will use Minecraft' permission level system, see https://docs.minecraftforge.net/en/latest/ and https://minecraft.fandom.com/wiki/Permission_level")
    }

    override fun hasPermission(player: ServerPlayer, permission: Permission): Boolean {
        val node = this.findNode(permission) ?: return player.hasPermissions(permission.level.numericalValue)
        return PermissionAPI.getPermission(player, node)
    }

    override fun hasPermission(player: ServerPlayer, permission: String, level: Int): Boolean {
        val node = PermissionNode(permission.split(".").first(), permission.substringAfter("."), PermissionTypes.BOOLEAN, { player, _, _ -> player?.hasPermissions(level) == true })
        return PermissionAPI.getPermission(player, node)
    }

    override fun hasPermission(source: CommandSourceStack, permission: Permission): Boolean {
        val player = this.extractPlayerFromSource(source) ?: return source.hasPermission(permission.level.numericalValue)
        val node = this.findNode(permission) ?: return source.hasPermission(permission.level.numericalValue)
        return PermissionAPI.getPermission(player, node)
    }

    override fun hasPermission(source: CommandSourceStack, permission: String, level: Int): Boolean {
        val player = this.extractPlayerFromSource(source) ?: return source.hasPermission(4)
        val node = PermissionNode(permission.split(".").first(), permission.substringAfter("."), PermissionTypes.BOOLEAN, { player, _, _ -> player?.hasPermissions(level) == true })
        return PermissionAPI.getPermission(player, node)
    }

    private fun createNodes() = CobblemonWikiGuiPermissions.all().map { permission ->
        // 3rd arg is default value if no implementation is present essentially
        val node = PermissionNode(permission.identifier, PermissionTypes.BOOLEAN, { player, _, _ -> player?.hasPermissions(permission.level.numericalValue) == true })
        this.nodes[permission.identifier] = node
        node
    }

    private fun findNode(permission: Permission) = this.nodes[permission.identifier]

    private fun extractPlayerFromSource(source: CommandSourceStack) = source.player

}