package com.thehairydog.pokeinfuser.gui.editors

import com.cobblemon.mod.common.pokemon.Pokemon
import net.minecraft.server.level.ServerPlayer
import net.minecraft.world.entity.player.Player
import net.minecraft.world.inventory.AbstractContainerMenu
import net.minecraft.world.inventory.MenuType
import net.minecraft.world.item.ItemStack

class PokeInfuserMovesEditor(syncId : Int, player: ServerPlayer, pokemon: Pokemon)
    : AbstractContainerMenu(MenuType.GENERIC_9x5, syncId) {

    init {

    }

    override fun quickMoveStack(player: Player, i: Int): ItemStack = ItemStack.EMPTY
    override fun stillValid(player: Player): Boolean = false

}