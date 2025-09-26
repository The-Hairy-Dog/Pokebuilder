package com.thehairydog.pokebuilder

import com.thehairydog.pokebuilder.commands.PokebuilderCommands
import com.thehairydog.pokebuilder.pokeessence.PokeEssenceCommand
import com.thehairydog.pokebuilder.pokeessence.PokeEssenceHandler
import net.fabricmc.api.ModInitializer
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents
import org.slf4j.LoggerFactory

object Pokebuilder : ModInitializer {
    private val logger = LoggerFactory.getLogger("pokebuilder")

	override fun onInitialize() {

		logger.info("Initialising Pokebuilder...")

        CommandRegistrationCallback.EVENT.register { dispatcher, _, _ ->
            PokebuilderCommands.register(dispatcher)
        }

        CommandRegistrationCallback.EVENT.register { dispatcher, _, _ ->
            dispatcher.register(PokeEssenceCommand.register())
        }

        // PlayerJoinEvent (or ServerPlayer constructor) -> load NBT
        ServerPlayConnectionEvents.JOIN.register { handler, _, _ ->
            println("${handler.player.name.string} joined, poke essence = ${PokeEssenceHandler.get(handler.player)}")
        }

        ServerPlayConnectionEvents.DISCONNECT.register { handler, server ->
            PokeEssenceHandler.clear(handler.player)
        }


    }
}