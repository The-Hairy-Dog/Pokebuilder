package com.thehairydog.pokebuilder.gui

import com.cobblemon.mod.common.item.PokemonItem
import com.cobblemon.mod.common.pokemon.Pokemon
import net.minecraft.server.level.ServerPlayer
import net.minecraft.world.SimpleContainer
import net.minecraft.world.entity.player.Player
import net.minecraft.world.inventory.AbstractContainerMenu
import net.minecraft.world.inventory.MenuType
import net.minecraft.world.inventory.Slot
import net.minecraft.world.item.ItemStack

class PokebuilderEditMenu(
    syncId: Int,
    player: ServerPlayer,
    pokemon: Pokemon
) : AbstractContainerMenu(MenuType.GENERIC_9x5, syncId) {

    private val container = SimpleContainer(9 * 5) // 9x5 grid

    init {
        // Convert PokemonItem to ItemStack
        val stack: ItemStack = PokemonItem.from(pokemon)

        // Add it to slot 0 (slot index is zero-based)
        container.setItem(22, stack)
        addSlot(Slot(container, 0, 8, 18)) // x=8, y=18 is just an example position

        // Add the rest of your container slots as needed...
    }

    override fun quickMoveStack(player: Player, index: Int): ItemStack? = ItemStack.EMPTY
    override fun stillValid(player: Player): Boolean = true
}
