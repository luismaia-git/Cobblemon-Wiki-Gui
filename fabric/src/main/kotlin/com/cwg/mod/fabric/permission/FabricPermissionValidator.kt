package com.cwg.mod.fabric.permission

import com.cwg.mod.CobblemonWikiGui
import com.cwg.mod.api.permission.Permission
import com.cwg.mod.api.permission.PermissionValidator
import me.lucko.fabric.api.permissions.v0.Permissions
import net.minecraft.commands.CommandSourceStack
import net.minecraft.server.level.ServerPlayer

class FabricPermissionValidator : PermissionValidator {
    override fun initialize() {
        CobblemonWikiGui.LOGGER.info("Booting FabricPermissionValidator, permissions will be checked using fabric-permissions-api, see https://github.com/lucko/fabric-permissions-api")
    }

    override fun hasPermission(player: ServerPlayer, permission: Permission) = Permissions.check(player, permission.literal, permission.level.numericalValue)

    override fun hasPermission(source: CommandSourceStack, permission: Permission) = Permissions.check(source, permission.literal, permission.level.numericalValue)

    override fun hasPermission(player: ServerPlayer, permission: String, level: Int) = Permissions.check(player, permission, level)

    override fun hasPermission(source: CommandSourceStack, permission: String, level: Int) = Permissions.check(source, permission, level)
}