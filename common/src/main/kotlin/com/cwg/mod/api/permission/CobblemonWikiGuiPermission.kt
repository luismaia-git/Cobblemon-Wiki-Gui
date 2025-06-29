package com.cwg.mod.api.permission
// Taken from the Cobblemon project
import com.cwg.mod.CobblemonWikiGui
import com.cwg.mod.util.cobblemonWikiResource

data class CobblemonWikiGuiPermission(
    private val node: String,
    override val level: PermissionLevel
) : Permission {
    
    override val identifier = cobblemonWikiResource(this.node)

    override val literal = "${CobblemonWikiGui.MOD_ID}.${this.node}"
}
