
package com.cwg.mod.permission

import com.cwg.mod.CobblemonWikiGui
import com.cwg.mod.api.permission.Permission
import com.cwg.mod.api.permission.PermissionValidator
import net.minecraft.commands.CommandSourceStack
import net.minecraft.server.level.ServerPlayer

/**
 * A [PermissionValidator] that uses the permission level vanilla system.
 * This is only used when the platform has no concept of permissions.
 */
class LaxPermissionValidator : PermissionValidator {

    override fun initialize() {
        CobblemonWikiGui.LOGGER.info("Booting LaxPermissionValidator, permissions will be checked using Minecrafts permission level system, see https://minecraft.fandom.com/wiki/Permission_level")
    }

    override fun hasPermission(player: ServerPlayer, permission: Permission) = player.hasPermissions(permission.level.numericalValue)
    override fun hasPermission(source: CommandSourceStack, permission: Permission) = source.hasPermission(permission.level.numericalValue)
    override fun hasPermission(player: ServerPlayer, permission: String, level: Int) = player.hasPermissions(level)
    override fun hasPermission(source: CommandSourceStack, permission: String, level: Int) = source.hasPermission(level)
}