package com.thehairydog.pokebuilder.pokeessence

import net.minecraft.nbt.CompoundTag
import net.minecraft.server.level.ServerPlayer

object PokeEssenceHandler {

    private const val TAG_KEY = "pokeessence"
    private val playerEssence = mutableMapOf<ServerPlayer, Int>()

    /** Get current essence */
    fun get(player: ServerPlayer): Int {
        return playerEssence[player] ?: 0
    }

    /** Add essence and save immediately */
    fun add(player: ServerPlayer, amount: Int) {
        val current = get(player)
        val newTotal = current + amount
        playerEssence[player] = newTotal
        save(player) // Save immediately to NBT
    }

    /** Remove essence if enough exists, returns success */
    fun remove(player: ServerPlayer, amount: Int): Boolean {
        val current = get(player)
        return if (current >= amount) {
            val newTotal = current - amount
            playerEssence[player] = newTotal
            save(player)
            true
        } else {
            false
        }
    }

    /** Set essence directly */
    fun set(player: ServerPlayer, amount: Int) {
        playerEssence[player] = amount
        save(player)
    }

    /** Save current value into player's NBT */
    private fun save(player: ServerPlayer) {
        val tag = CompoundTag()
        tag.putInt(TAG_KEY, get(player))
        player.addAdditionalSaveData(tag)
    }

    /** Load from NBT on player join / load */
    fun load(player: ServerPlayer, nbt: CompoundTag) {
        val value = nbt.getInt(TAG_KEY)
        playerEssence[player] = value
    }

    /** Clear memory when player disconnects */
    fun clear(player: ServerPlayer) {
        playerEssence.remove(player)
    }

}
