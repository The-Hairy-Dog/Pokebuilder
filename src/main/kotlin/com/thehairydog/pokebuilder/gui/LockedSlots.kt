package com.thehairydog.pokebuilder.gui

import net.minecraft.world.Container
import net.minecraft.world.entity.player.Player
import net.minecraft.world.inventory.Slot
import net.minecraft.world.item.ItemStack

class LockedSlot(container: Container, index: Int, x: Int, y: Int)
    : Slot(container, index, x, y) {
    override fun mayPlace(stack: ItemStack) = false
    override fun mayPickup(player: Player) = false
}
