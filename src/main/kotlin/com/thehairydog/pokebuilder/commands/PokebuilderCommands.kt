package com.thehairydog.pokebuilder.commands

import com.cobblemon.mod.common.item.PokemonItem
import com.cobblemon.mod.common.pokemon.Pokemon
import com.cobblemon.mod.common.util.party
import com.mojang.brigadier.CommandDispatcher
import com.mojang.brigadier.arguments.IntegerArgumentType
import net.minecraft.commands.CommandSourceStack
import net.minecraft.commands.Commands
import net.minecraft.core.component.DataComponents
import net.minecraft.network.chat.Component
import net.minecraft.server.level.ServerPlayer

object PokebuilderCommands {

    fun register(dispatcher: CommandDispatcher<CommandSourceStack>) {
        dispatcher.register(
            Commands.literal("pokebuilder")
                .then(
                    Commands.argument("slot", IntegerArgumentType.integer(1,6))
                        .executes { context ->
                            val source = context.source

                            val player = source.player ?: run {
                                source.sendFailure(Component.literal("You must be a player to use this command!"))
                                return@executes 0
                            }

                            val slot = IntegerArgumentType.getInteger(context, "slot")
                            val party = player.party()
                            val pokemon = party.get(slot - 1)
                            if (pokemon != null) {
                                givePokemonItem(player, pokemon)
                                player.sendSystemMessage(Component.literal("You received a ${pokemon.species.name} Plushie!"))
                            } else {
                                player.sendSystemMessage(Component.literal("Slot $slot is empty!"))
                            }

                            1

                        }
                )
        )
    }

    fun givePokemonItem(player: ServerPlayer, pokemon: Pokemon) {
        val itemStack = PokemonItem.from(pokemon)
        itemStack.set(DataComponents.CUSTOM_NAME, Component.literal("${pokemon.species.name} Plushie"))
        player.inventory.add(itemStack) // adds to inventory or drops if full
    }


}