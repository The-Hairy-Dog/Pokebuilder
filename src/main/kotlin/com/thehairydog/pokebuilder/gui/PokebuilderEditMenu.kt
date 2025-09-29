package com.thehairydog.pokebuilder.gui

import ClickableSlot
import com.cobblemon.mod.common.pokemon.Pokemon
import com.thehairydog.pokebuilder.gui.slotUtil.LockedSlot
import com.thehairydog.pokebuilder.util.PokeItemFormatter
import com.thehairydog.pokebuilder.util.PokeItemFormatter.configurePokemonForPokebuilder
import net.minecraft.server.level.ServerPlayer
import net.minecraft.world.SimpleContainer
import net.minecraft.world.entity.player.Player
import net.minecraft.world.inventory.AbstractContainerMenu
import net.minecraft.world.inventory.MenuType
import net.minecraft.world.item.ItemStack

class PokebuilderEditMenu(
    syncId: Int,
    player: ServerPlayer,
    pokemon: Pokemon
) : AbstractContainerMenu(MenuType.GENERIC_9x5, syncId) {

    private val container = SimpleContainer(9 * 5) // 9x5 grid

    init {

        for (i in 0 until 45) {
            val slotX = 8 + (i % 9) * 18
            val slotY = 18 + (i / 9) * 18

            val itemStack = when (i) {
                22 -> PokeItemFormatter.configurePokemonItemForEditor(configurePokemonForPokebuilder(pokemon), pokemon)
                20 -> PokeItemFormatter.configureIVsEditor(pokemon)
                24 -> PokeItemFormatter.configureGenderEditor(pokemon)
                11 -> PokeItemFormatter.configureNatureEditor(pokemon)
                29 -> PokeItemFormatter.configureEVsEditor(pokemon)
                15 -> PokeItemFormatter.configureLevelEditor(pokemon)
                33 -> PokeItemFormatter.configureAbilityEditor(pokemon)
                19 -> PokeItemFormatter.configureMovesEditor(pokemon)
                25 -> PokeItemFormatter.configureShinyEditor(pokemon)
                40 -> PokeItemFormatter.configureBackItem()
                else -> ItemStack.EMPTY
            }

            val slot = if (itemStack.isEmpty) {
                LockedSlot(container, i, slotX, slotY) // or a normal slot
            } else {
                ClickableSlot(container, i, slotX, slotY) {
                    when (i) {
                        20 -> PokebuilderOpenMenus.openIVsPage(player, pokemon)
                        24 -> PokebuilderOpenMenus.openGenderPage()
                        11 -> PokebuilderOpenMenus.openNaturePage(player, pokemon)
                        29 -> PokebuilderOpenMenus.openEVsPage()
                        15 -> PokebuilderOpenMenus.openLevelsPage()
                        33 -> PokebuilderOpenMenus.openAbilityPage()
                        19 -> PokebuilderOpenMenus.openMovesPage()
                        25 -> PokebuilderOpenMenus.openShinyPage()
                        40 -> PokebuilderOpenMenus.openMainPage(player)
                    }
                }
            }

            container.setItem(i, itemStack)
            addSlot(slot)
        }


        val playerInventory = player.inventory
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
    override fun stillValid(player: Player): Boolean = true
}
