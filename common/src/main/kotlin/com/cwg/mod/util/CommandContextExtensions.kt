package com.cwg.mod.util
// Taken from the Cobblemon project

import com.cobblemon.mod.common.util.asUUID
import com.mojang.brigadier.context.CommandContext
import net.minecraft.commands.CommandSourceStack
import net.minecraft.commands.arguments.EntityArgument
import net.minecraft.commands.arguments.ResourceLocationArgument

fun CommandContext<CommandSourceStack>.player(argumentName: String = "player") = EntityArgument.getPlayer(this, argumentName)
fun CommandContext<CommandSourceStack>.string(argumentName: String) = this.getArgument(argumentName, String::class.java)
fun CommandContext<CommandSourceStack>.uuid(argumentName: String) = this.getArgument(argumentName, String::class.java).asUUID
fun CommandContext<CommandSourceStack>.resourceLocation(argumentName: String) = ResourceLocationArgument.getId(this, argumentName)