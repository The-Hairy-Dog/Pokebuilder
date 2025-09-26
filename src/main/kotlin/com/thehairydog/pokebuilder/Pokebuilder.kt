package com.thehairydog.pokebuilder

import com.thehairydog.pokebuilder.commands.PokebuilderCommands
import com.thehairydog.pokebuilder.pokeessence.PokeEssenceCommand
import com.thehairydog.pokebuilder.pokeessence.PokeEssenceData
import net.fabricmc.api.ModInitializer
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents
import org.slf4j.LoggerFactory

object Pokebuilder : ModInitializer {
    private val logger = LoggerFactory.getLogger("pokebuilder")

    override fun onInitialize() {
        logger.info("Initialising Pokebuilder...")

        // Register custom commands
        CommandRegistrationCallback.EVENT.register { dispatcher, _, _ ->
            PokebuilderCommands.register(dispatcher)
            dispatcher.register(PokeEssenceCommand.register())
        }

        // On player join, send them their current PokéEssence
        ServerPlayConnectionEvents.JOIN.register { handler, _, _ ->
            val player = handler.player
            val world = player.serverLevel()
            val essenceData = PokeEssenceData.get(world)
            val essence = essenceData.get(player.uuid)
            player.sendSystemMessage(
                net.minecraft.network.chat.Component.literal("Welcome! You have $essence PokéEssence.")
            )
        }

        // No need for memory cache cleanup on disconnect with persistent storage
        ServerPlayConnectionEvents.DISCONNECT.register { handler, _ ->
            // Optional: could log if needed
            logger.info("${handler.player.name.string} disconnected.")
        }
    }
}
