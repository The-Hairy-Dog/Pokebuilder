package com.thehairydog.pokebuilder.gui

import com.cobblemon.mod.common.CobblemonItems
import com.cobblemon.mod.common.item.PokemonItem
import com.cobblemon.mod.common.util.party
import com.thehairydog.pokebuilder.util.PokeItemFormatter.configurePokeEssence
import com.thehairydog.pokebuilder.util.PokeItemFormatter.configurePokemonForPokebuilder
import net.minecraft.server.level.ServerPlayer
import net.minecraft.world.SimpleContainer
import net.minecraft.world.entity.player.Player
import net.minecraft.world.inventory.AbstractContainerMenu
import net.minecraft.world.inventory.ClickType
import net.minecraft.world.inventory.MenuType
import net.minecraft.world.item.ItemStack
import net.minecraft.world.level.block.Blocks

class PokebuilderMainMenu(
    syncId: Int,
    serverPlayer: ServerPlayer
) : AbstractContainerMenu(MenuType.GENERIC_9x3, syncId) {

    private val container = SimpleContainer(27) // 9x3 grid

    init {
        val party = serverPlayer.party()
        val row = 1
        val rowStart = row * 9
        val slotPositions = listOf(1, 2, 3, 5, 6, 7).map { rowStart + it }

        // Fill container items
        for (i in 0 until 27) {
            val itemStack = when(i) {
                in 0..8 -> ItemStack(Blocks.WHITE_STAINED_GLASS_PANE)
                9,17 -> ItemStack(Blocks.BLACK_STAINED_GLASS_PANE)
                13 -> configurePokeEssence(CobblemonItems.RELIC_COIN, serverPlayer)
                in slotPositions -> {
                    val pokeIndex = slotPositions.indexOf(i)
                    val poke = party.get(pokeIndex)
                    poke?.let { configurePokemonForPokebuilder(it) } ?: ItemStack(Blocks.BARRIER)
                }
                else -> ItemStack(Blocks.WHITE_STAINED_GLASS_PANE)
            }

            container.setItem(i, itemStack)
            addSlot(LockedSlot(container, i, 8 + (i % 9) * 18, 18 + (i / 9) * 18))
        }

        // Add player inventory & hotbar slots (locked)
        val playerInventory = serverPlayer.inventory
        for(rowIndex in 0 until 3) {
            for(col in 0 until 9) {
                val index = col + rowIndex * 9 + 9
                addSlot(LockedSlot(playerInventory, index, 8 + col * 18, 84 + rowIndex * 18))
            }
        }
        for(col in 0 until 9) {
            addSlot(LockedSlot(playerInventory, col, 8 + col * 18, 142))
        }
    }

    override fun quickMoveStack(player: Player, index: Int): ItemStack? = ItemStack.EMPTY
    override fun stillValid(player: Player) = true

    override fun clicked(slotId: Int, dragType: Int, clickType: ClickType, player: Player) {

        val stack = container.getItem(slotId)
        val pokemonItem = stack.item as? PokemonItem ?: return
        val pokemon = pokemonItem.asPokemon(stack) ?: return
        if (clickType != ClickType.PICKUP) return

        (player as? ServerPlayer)?.let {
            PokebuilderOpenMenus.openEditPage(it, pokemon, pokemonItem)
        }
    }



}
