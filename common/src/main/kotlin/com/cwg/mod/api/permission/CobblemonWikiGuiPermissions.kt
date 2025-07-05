package com.cwg.mod.api.permission
// Taken from the Cobblemon project
object CobblemonWikiGuiPermissions {

    private const val COMMAND_PREFIX = "command."
    private val permissions = arrayListOf<Permission>()

    @JvmStatic
    val RELOAD = this.create("${COMMAND_PREFIX}cwgreload", PermissionLevel.ALL_COMMANDS)
    @JvmStatic
    val PWIKI = this.create("${COMMAND_PREFIX}pwiki", PermissionLevel.ALL_COMMANDS)


    @JvmStatic
    val PWIKIANOTHER = this.create("${COMMAND_PREFIX}pwikianother", PermissionLevel.ALL_COMMANDS)

    fun all(): Iterable<Permission> = this.permissions

    private fun create(node: String, level: PermissionLevel): Permission {
        val permission = CobblemonWikiGuiPermission(node, level)
        this.permissions += permission
        return permission
    }

}