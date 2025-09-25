package com.thehairydog.pokebuilder.gui

import com.cobblemon.mod.common.item.PokemonItem
import com.cobblemon.mod.common.util.party
import net.minecraft.server.level.ServerPlayer
import net.minecraft.world.entity.player.Player
import net.minecraft.world.inventory.AbstractContainerMenu
import net.minecraft.world.inventory.Slot
import net.minecraft.world.inventory.ClickType
import net.minecraft.world.item.ItemStack
import net.minecraft.world.level.block.Blocks
import net.minecraft.world.SimpleContainer
import net.minecraft.world.inventory.MenuType

class PokebuilderContainer(
    syncId: Int,
    serverPlayer: ServerPlayer
) : AbstractContainerMenu(MenuType.GENERIC_9x3, syncId) {

    private val container = SimpleContainer(27) // 9x3 grid

    init {
        val party = serverPlayer.party()

        val row = 1
        val rowStart = row * 9

        val slotPositions = listOf(1, 2, 3, 5, 6, 7).map { rowStart + it }

        for (i in 0 until 6) {
            val slotIndex = slotPositions[i]
            val poke = party.get(i)

            if (poke != null) {
                container.setItem(slotIndex, PokemonItem.from(poke))
            } else {
                container.setItem(slotIndex, ItemStack(Blocks.BARRIER))
            }
        }

        for (i in 0 until 27) {
            addSlot(object : Slot(container, i, 8 + (i % 9) * 18, 18 + (i / 9) * 18) {
                override fun mayPlace(stack: ItemStack) = false
                override fun mayPickup(player: Player) = false
            })
        }
    }

    override fun quickMoveStack(player: Player, index: Int): ItemStack = ItemStack.EMPTY

    override fun clicked(slotId: Int, dragType: Int, clickType: ClickType, player: Player) {
        // do nothing
    }

    override fun stillValid(player: Player) = true
}

