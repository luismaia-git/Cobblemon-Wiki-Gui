package com.cwg.mod.util
// Taken from the Cobblemon project
import com.mojang.brigadier.builder.ArgumentBuilder
import com.mojang.brigadier.builder.LiteralArgumentBuilder
import com.mojang.brigadier.tree.LiteralCommandNode
import net.minecraft.commands.CommandSourceStack

fun <T : ArgumentBuilder<CommandSourceStack, T>> ArgumentBuilder<CommandSourceStack, T>.appendRequirement(requirement: (src: CommandSourceStack) -> Boolean): T = this.requires(this.requirement.and(requirement))

// Taken from the Velocity project
/**
 * Clones this node into a new one with the given alias.
 *
 * @param S The type of the command source.
 * @param alias The alias for the command.
 * @return A [LiteralArgumentBuilder] for the cloned command.
 */
fun <S : Any> LiteralCommandNode<S>.alias(alias: String): LiteralArgumentBuilder<S> {
    val builder = LiteralArgumentBuilder.literal<S>(alias.lowercase())
        .requires(this.requirement)
        .forward(this.redirect, this.redirectModifier, this.isFork)
        .executes(this.command)
    this.children.forEach { child ->
        builder.then(child)
    }
    return builder
}