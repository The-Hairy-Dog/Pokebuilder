package com.thehairydog.pokebuilder.commands

import com.thehairydog.pokebuilder.gui.PokebuilderContainer
import com.mojang.brigadier.CommandDispatcher
import net.minecraft.commands.CommandSourceStack
import net.minecraft.commands.Commands
import net.minecraft.network.chat.Component
import net.minecraft.world.MenuProvider
import net.minecraft.world.entity.player.Inventory
import net.minecraft.world.entity.player.Player
import net.minecraft.world.inventory.AbstractContainerMenu

object PokebuilderCommands {

    fun register(dispatcher: CommandDispatcher<CommandSourceStack>) {
        dispatcher.register(
            Commands.literal("pokebuilder")
                .executes { context ->
                    val player = context.source.player ?: return@executes 0

                    player.openMenu(object : MenuProvider {
                        override fun getDisplayName() = Component.literal("Poké Builder")
                        override fun createMenu(syncId: Int, inventory: Inventory, playerEntity: Player): AbstractContainerMenu {
                            return PokebuilderContainer(syncId, player)
                        }
                    })
                    1
                }
        )
    }
}
