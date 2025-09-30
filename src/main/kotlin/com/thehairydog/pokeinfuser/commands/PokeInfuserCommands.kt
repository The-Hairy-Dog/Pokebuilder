package com.thehairydog.pokeinfuser.commands

import com.mojang.brigadier.CommandDispatcher
import com.thehairydog.pokeinfuser.gui.PokeInfuserOpenMenus
import net.minecraft.commands.CommandSourceStack
import net.minecraft.commands.Commands

object PokeInfuserCommands {

    fun register(dispatcher: CommandDispatcher<CommandSourceStack>) {
        dispatcher.register(
            Commands.literal("pokeinfuser")
                .executes { context ->
                    val player = context.source.player ?: return@executes 0
                    PokeInfuserOpenMenus.openMainPage(player)
                    1
                }
        )
    }
}
