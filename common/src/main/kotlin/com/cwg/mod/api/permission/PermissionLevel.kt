package com.cwg.mod.api.permission
// Taken from the Cobblemon project
enum class PermissionLevel(val numericalValue: Int) {

    NONE(0),
    SPAWN_PROTECTION_BYPASS(1),
    CHEAT_COMMANDS_AND_COMMAND_BLOCKS(2),
    MULTIPLAYER_MANAGEMENT(3),
    ALL_COMMANDS(4);

    companion object {
        fun byNumericValue(value: Int): PermissionLevel {
            return entries.first { it.numericalValue == value }
        }
    }
}