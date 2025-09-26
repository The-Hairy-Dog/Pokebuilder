package com.thehairydog.pokebuilder.pokeessence

import net.minecraft.core.HolderLookup
import net.minecraft.nbt.CompoundTag
import net.minecraft.server.level.ServerLevel
import net.minecraft.util.datafix.DataFixTypes
import net.minecraft.world.level.saveddata.SavedData
import java.util.*

private const val KEY = "pokebuilder_pokeessence"

class PokeEssenceData : SavedData() {

    // Store essence per player UUID
    private val essenceMap: MutableMap<UUID, Int> = mutableMapOf()

    /** Get essence for a player */
    fun get(uuid: UUID) = essenceMap[uuid] ?: 0

    /** Set essence for a player */
    fun set(uuid: UUID, amount: Int) {
        essenceMap[uuid] = amount
        setDirty()
    }

    /** Add essence */
    fun add(uuid: UUID, amount: Int) {
        essenceMap[uuid] = get(uuid) + amount
        setDirty()
    }

    /** Remove essence */
    fun remove(uuid: UUID, amount: Int): Boolean {
        val current = get(uuid)
        return if (current >= amount) {
            essenceMap[uuid] = current - amount
            setDirty()
            true
        } else false
    }

    /** Save to NBT */
    override fun save(compoundTag: CompoundTag, provider: HolderLookup.Provider): CompoundTag {
        val mapTag = CompoundTag()
        essenceMap.forEach { (uuid, value) ->
            mapTag.putInt(uuid.toString(), value)
        }
        compoundTag.put("essenceMap", mapTag)
        return compoundTag
    }

    companion object {

        /** Create instance from NBT */
        fun fromNbt(tag: CompoundTag, provider: HolderLookup.Provider): PokeEssenceData {
            val data = PokeEssenceData()
            val mapTag = tag.getCompound("essenceMap")
            mapTag.allKeys.forEach { key ->
                data.essenceMap[UUID.fromString(key)] = mapTag.getInt(key)
            }
            return data
        }

        /** Retrieve or create PokeEssenceData for a world */
        fun get(world: ServerLevel): PokeEssenceData {
            return world.dataStorage.computeIfAbsent(
                SavedData.Factory(
                    { PokeEssenceData() },
                    { tag: CompoundTag, provider: HolderLookup.Provider ->
                        PokeEssenceData.fromNbt(tag, provider)
                    },
                    DataFixTypes.LEVEL
                ),
                KEY
            )
        }


    }
}
