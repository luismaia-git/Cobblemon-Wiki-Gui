package com.cwg.mod.util
// Taken from the Cobblemon project

import com.cwg.mod.CobblemonWikiGui
import com.cwg.mod.api.permission.Permission
import com.mojang.brigadier.builder.ArgumentBuilder
import net.minecraft.commands.CommandSourceStack

/**
 * Creates an [ArgumentBuilder.requirement] for a permission.
 * If you'd like to apply a requirement as well from a single function use [requiresWithPermission].
 *
 * @param T the type of the [ArgumentBuilder].
 * @param permission The [Permission] for this command
 * @param appendRequirement If the existing [ArgumentBuilder.requirement] should be appended to this permission as a single predicate. Defaults to true
 * @return the [ArgumentBuilder].
 */
fun <T : ArgumentBuilder<CommandSourceStack, T>> ArgumentBuilder<CommandSourceStack, T>.permission(permission: Permission, appendRequirement: Boolean = true): T {
    val permissionPredicate = { src: CommandSourceStack -> CobblemonWikiGui.permissionValidator.hasPermission(source = src, permission = permission)  }
    return if (appendRequirement) this.requires(this.requirement.and(permissionPredicate)) else this.requires(permissionPredicate)
}

/**
 * Creates an [ArgumentBuilder.requirement] merged with a permission.
 *
 * @param T the type of the [ArgumentBuilder].
 * @param permission The [Permission] for this command
 * @param predicate The requirement for the command.
 * @return the [ArgumentBuilder].
 */
fun <T : ArgumentBuilder<CommandSourceStack, T>> ArgumentBuilder<CommandSourceStack, T>.requiresWithPermission(permission: Permission, predicate: (src: CommandSourceStack) -> Boolean): T {
    this.requires(predicate)
    return this.permission(permission)
}