package com.luismaia.util

import com.cobblemon.mod.common.Cobblemon.permissionValidator
import com.cobblemon.mod.common.api.permission.CobblemonPermission
import com.cobblemon.mod.common.api.permission.PermissionLevel
import net.minecraft.server.network.ServerPlayerEntity

class Permissions {
        // Store all created permissions in a hashmap.
        private val permissions = HashMap<String, CobblemonPermission>()

        /**
         * Constructor to create a permission object. Add your permissions here using the private method.
         */
        init {
            // Add you permissions here.
            createPermission("PokeJourneysCommand", "pokejourneys.pwiki.user", 1)
        }

        /**
         * Method to add a new permission to the hashmap.
         * @param name The reference of the permission.
         * @param permissionNode The permission node.
         * @param permissionLevel The permission level.
         */
        private fun createPermission(name: String, permissionNode: String, permissionLevel: Int) {
            permissions[name] = CobblemonPermission(permissionNode, parsePermission(permissionLevel))
        }

        /**
         * Method to fetch a permission from its reference.
         * @param name The reference of the permission.
         * @return
         */
        fun getPermission(name: String): CobblemonPermission? {
            return permissions[name]
        }

        fun hasPermission(player: ServerPlayerEntity, permission: CobblemonPermission): Boolean {
            return permissionValidator.hasPermission(player, permission)
        }

        private fun parsePermission(permLevel: Int): PermissionLevel {
            for (value in PermissionLevel.values()) {
                if (value.ordinal == permLevel) {
                    return value
                }
            }
            return PermissionLevel.CHEAT_COMMANDS_AND_COMMAND_BLOCKS
        }
}