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
import net.minecraft.world.SimpleContainer
import net.minecraft.world.item.ItemStack

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

                            //val slot = IntegerArgumentType.getInteger(context, "slot")
                            //val party = player.party()
                            //val pokemon = party.get(slot - 1)
                            //if (pokemon != null) {
                            //    givePokemonItem(player, pokemon)
                            //    player.sendSystemMessage(Component.literal("You received a ${pokemon.species.name} Plushie!"))
                            //} else {
                            //    player.sendSystemMessage(Component.literal("Slot $slot is empty!"))
                            //}

                            openServerSideMenu(player)
                            player.sendSystemMessage(Component.literal("Opening Pokebuilder."))

                            1

                        }
                )
        )
    }

    //fun givePokemonItem(player: ServerPlayer, pokemon: Pokemon) {
    //    val itemStack = PokemonItem.from(pokemon)
    //    itemStack.set(DataComponents.CUSTOM_NAME, Component.literal("${pokemon.species.name} Plushie"))
    //    player.inventory.add(itemStack) // adds to inventory or drops if full
    //}
    private fun openServerSideMenu(player: ServerPlayer) {
        val party = player.party() // Cobblemon API
        val container = object : SimpleContainer(party.size()) {
            override fun canPlaceItem(index: Int, stack: ItemStack): Boolean {
                // Prevent placing items (can't change slots)
                return false
            }

        }// 9 slots, can increase if needed

        for ((i, poke) in party.withIndex()) {
            val item: ItemStack = PokemonItem.from(poke)
            container.setItem(i, item)
        }

        // You can now access `container` server-side:
        // e.g., when player clicks a slot (server event), you can handle it
        println("Player ${player.name.string} has Pokémon in server-side container:")
        for (i in 0 until container.containerSize) {
            val stack = container.getItem(i)
            if (!stack.isEmpty) {
                println("- ${stack.displayName.string}")
            }
        }

        // No client interaction is necessary; everything stays server-side
    }


}