package com.thehairydog.pokebuilder.commands

import com.mojang.brigadier.CommandDispatcher
import com.thehairydog.pokebuilder.gui.PokebuilderOpenMenus
import net.minecraft.commands.CommandSourceStack
import net.minecraft.commands.Commands

object PokebuilderCommands {

    fun register(dispatcher: CommandDispatcher<CommandSourceStack>) {
        dispatcher.register(
            Commands.literal("pokebuilder")
                .executes { context ->
                    val player = context.source.player ?: return@executes 0
                    PokebuilderOpenMenus.openMainPage(player)
                    1
                }
        )
    }
}
