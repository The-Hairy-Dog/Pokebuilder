package com.thehairydog.pokebuilder

import com.thehairydog.pokebuilder.commands.PokebuilderCommands
import com.thehairydog.pokebuilder.pokeessence.PokeEssenceCommand
import com.thehairydog.pokebuilder.pokeessence.PokeEssenceHandler
import net.fabricmc.api.ModInitializer
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents
import net.minecraft.nbt.CompoundTag
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
        ServerPlayConnectionEvents.JOIN.register { handler, sender, server ->
            val player = handler.player
            val tag = CompoundTag()
            player.addAdditionalSaveData(tag) // fills 'tag' with the player's data
            PokeEssenceHandler.load(player, tag)
        }


// PlayerQuitEvent -> clear memory
        ServerPlayConnectionEvents.DISCONNECT.register { handler, server ->
            PokeEssenceHandler.clear(handler.player)
        }

// Optional: On server tick, you could also periodically save everyone

    }
}