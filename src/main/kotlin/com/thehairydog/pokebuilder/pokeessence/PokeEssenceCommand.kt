package com.thehairydog.pokebuilder.pokeessence

import com.mojang.brigadier.arguments.IntegerArgumentType
import com.mojang.brigadier.builder.LiteralArgumentBuilder
import net.minecraft.commands.CommandSourceStack
import net.minecraft.commands.Commands
import net.minecraft.network.chat.Component

object PokeEssenceCommand {

    fun register(): LiteralArgumentBuilder<CommandSourceStack> {
        return Commands.literal("pokeessence").then(
            Commands.literal("add")
                .then(Commands.argument("amount", IntegerArgumentType.integer())
                    .executes { context ->
                        val player = context.source.playerOrException
                        val amount = IntegerArgumentType.getInteger(context, "amount")
                        PokeEssenceHandler.add(player, amount)
                        context.source.sendSuccess({Component.literal("Added $amount PokéEssence!") }, false)
                        1
                    }
            )

        )
            .then(Commands.literal("remove")
                .then(Commands.argument("amount", IntegerArgumentType.integer()))
                .executes { context ->
                    val player = context.source.playerOrException
                    val amount = IntegerArgumentType.getInteger(context, "amount")
                    val success = PokeEssenceHandler.remove(player, amount)
                    if (success) {
                        context.source.sendSuccess({Component.literal("Removed $amount PokéEssence!")}, false)
                    } else {
                        context.source.sendFailure(Component.literal("Not enough PokéEssence!"))
                    }
                    1
                }
        )
            .then(
                Commands.literal("essence")
                    .executes { ctx ->
                        val player = ctx.source.playerOrException
                        val current = PokeEssenceHandler.get(player)
                        ctx.source.sendSuccess({Component.literal("You have $current PokéEssence")}, false)
                        1
                    })
    }

}