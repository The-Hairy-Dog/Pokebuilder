package com.thehairydog.pokebuilder.pokeessence

import net.minecraft.nbt.CompoundTag
import net.minecraft.server.level.ServerPlayer

object PokeEssenceHandler {

    // Memory cache is optional, but safe for ticks/commands
    private val playerCache = mutableMapOf<ServerPlayer, Int>()

    /** Get essence for player (from cache if exists, else 0) */
    fun get(player: ServerPlayer): Int = playerCache[player] ?: 0

    /** Add essence and update cache */
    fun add(player: ServerPlayer, amount: Int) {
        val current = get(player)
        playerCache[player] = current + amount
    }

    /** Remove essence */
    fun remove(player: ServerPlayer, amount: Int): Boolean {
        val current = get(player)
        return if (current >= amount) {
            playerCache[player] = current - amount
            true
        } else false
    }

    /** Set essence directly */
    fun set(player: ServerPlayer, amount: Int) {
        playerCache[player] = amount
    }

    /** Called from Mixin when player loads */
    fun load(player: ServerPlayer, tag: CompoundTag) {
        val value = tag.getInt("pokeessence")
        playerCache[player] = value
    }

    /** Called from Mixin when player saves */
    fun save(player: ServerPlayer, tag: CompoundTag) {
        tag.putInt("pokeessence", get(player))
    }

    /** Clear memory cache on disconnect */
    fun clear(player: ServerPlayer) {
        playerCache.remove(player)
    }
}
