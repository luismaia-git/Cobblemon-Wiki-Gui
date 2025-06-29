package com.cwg.mod.api.permission
// Taken from the Cobblemon project
import net.minecraft.commands.CommandSourceStack
import net.minecraft.server.level.ServerPlayer

interface PermissionValidator {
    fun initialize()
    fun hasPermission(player: ServerPlayer, permission: Permission): Boolean
    fun hasPermission(player: ServerPlayer, permission: String, level: Int): Boolean = hasPermission(player, CobblemonWikiGuiPermission(permission, PermissionLevel.byNumericValue(level)))
    fun hasPermission(source: CommandSourceStack, permission: Permission): Boolean
    fun hasPermission(source: CommandSourceStack, permission: String, level: Int): Boolean = hasPermission(source, CobblemonWikiGuiPermission(permission, PermissionLevel.byNumericValue(level)))
}