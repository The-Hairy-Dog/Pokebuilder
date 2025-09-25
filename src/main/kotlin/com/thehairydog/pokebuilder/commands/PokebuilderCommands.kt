package com.thehairydog.pokebuilder.commands

import com.cobblemon.mod.common.util.party
import com.mojang.brigadier.CommandDispatcher
import com.mojang.brigadier.arguments.IntegerArgumentType
import net.minecraft.commands.CommandSourceStack
import net.minecraft.commands.Commands
import net.minecraft.network.chat.Component
import net.minecraft.network.chat.OutgoingChatMessage

object PokebuilderCommands {

    fun register(dispatcher: CommandDispatcher<CommandSourceStack>) {
        dispatcher.register(
            Commands.literal("pokebuilder")
                .then(
                    Commands.argument("slot", IntegerArgumentType.integer(1,6))
                        .executes { context ->
                            val source = context.source
                            val player = source.player

                            val slot = IntegerArgumentType.getInteger(context, "slot")
                            val party = player?.party()
                            if (party != null) {
                                val pokemon = party.get(slot - 1)

                                if (pokemon != null) {
                                    player.sendSystemMessage(Component.literal("Slot $slot contains ${pokemon.species.name}"))
                                } else {
                                    player.sendSystemMessage(Component.literal("Slot $slot is empty!"))
                                }

                            } else {
                                source.sendFailure(Component.literal("You must be a player to use this command!"))
                            }




                            1

                        }
                )
        )
    }

}