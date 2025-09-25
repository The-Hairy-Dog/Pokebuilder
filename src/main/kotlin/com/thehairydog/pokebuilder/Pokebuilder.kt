package com.thehairydog.pokebuilder

import com.thehairydog.pokebuilder.commands.PokebuilderCommands
import net.fabricmc.api.ModInitializer
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback
import org.slf4j.LoggerFactory

object Pokebuilder : ModInitializer {
    private val logger = LoggerFactory.getLogger("pokebuilder")

	override fun onInitialize() {

		logger.info("Initialising Pokebuilder...")

        CommandRegistrationCallback.EVENT.register { dispatcher, _, _ ->
            PokebuilderCommands.register(dispatcher)
        }
	}
}