package com.thehairydog.pokeinfuser.gui

import ClickableSlot
import com.cobblemon.mod.common.pokemon.Pokemon
import com.thehairydog.pokeinfuser.gui.slotUtil.LockedSlot
import com.thehairydog.pokeinfuser.util.PokeItemFormatter
import com.thehairydog.pokeinfuser.util.PokeItemFormatter.configurePokemonForPokebuilder
import com.thehairydog.pokeinfuser.util.SoundUtil
import net.minecraft.server.level.ServerPlayer
import net.minecraft.world.SimpleContainer
import net.minecraft.world.entity.player.Player
import net.minecraft.world.inventory.AbstractContainerMenu
import net.minecraft.world.inventory.MenuType
import net.minecraft.world.item.ItemStack

class PokeInfuserEditMenu(
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
                LockedSlot(container, i, slotX, slotY)
            } else {
                ClickableSlot(container, i, slotX, slotY) {
                    when (i) {
                        40 -> {
                            SoundUtil.playBackSound(player)
                            PokeInfuserOpenMenus.openMainPage(player)
                        }
                        else -> {
                            SoundUtil.playClickSound(player)

                            when (i) {
                                20 -> PokeInfuserOpenMenus.openIVsPage(player, pokemon)
                                24 -> PokeInfuserOpenMenus.openGenderPage()
                                11 -> PokeInfuserOpenMenus.openNaturePage(player, pokemon)
                                29 -> PokeInfuserOpenMenus.openEVsPage(player, pokemon)
                                15 -> PokeInfuserOpenMenus.openLevelsPage()
                                33 -> PokeInfuserOpenMenus.openAbilityPage()
                                19 -> PokeInfuserOpenMenus.openMovesPage()
                                25 -> PokeInfuserOpenMenus.openShinyPage()
                            }
                        }
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
