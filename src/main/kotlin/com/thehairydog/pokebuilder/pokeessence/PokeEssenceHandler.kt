package com.thehairydog.pokebuilder.pokeessence

import net.minecraft.server.level.ServerPlayer
import net.minecraft.server.level.ServerLevel

object PokeEssenceHandler {

    /** Get essence for player (from world saved data) */
    fun get(player: ServerPlayer): Int {
        val data = PokeEssenceData.get(player.serverLevel() as ServerLevel)
        return data.get(player.uuid)
    }

    /** Set essence for player */
    fun set(player: ServerPlayer, amount: Int) {
        val data = PokeEssenceData.get(player.serverLevel() as ServerLevel)
        data.set(player.uuid, amount)
    }

    /** Add essence for player */
    fun add(player: ServerPlayer, amount: Int) {
        val data = PokeEssenceData.get(player.serverLevel() as ServerLevel)
        data.add(player.uuid, amount)
    }

    /** Remove essence for player */
    fun remove(player: ServerPlayer, amount: Int): Boolean {
        val data = PokeEssenceData.get(player.serverLevel() as ServerLevel)
        return data.remove(player.uuid, amount)
    }

    /** Clear memory cache is now unnecessary */
    fun clear(player: ServerPlayer) {
        // No-op since we rely entirely on SavedData
    }
}
