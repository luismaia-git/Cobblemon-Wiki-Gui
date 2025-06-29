package com.cwg.mod.api.permission
// Taken from the Cobblemon project
import net.minecraft.resources.ResourceLocation

interface Permission {

    val identifier: ResourceLocation

    val literal: String

    val level: PermissionLevel

}