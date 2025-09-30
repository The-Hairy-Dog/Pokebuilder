package com.thehairydog.pokeinfuser.gui.editors

import com.cobblemon.mod.common.pokemon.Pokemon
import com.thehairydog.pokeinfuser.gui.PokeInfuserOpenMenus
import com.thehairydog.pokeinfuser.gui.slotUtil.LockedSlot
import com.thehairydog.pokeinfuser.util.ColourUtil
import com.thehairydog.pokeinfuser.util.PokeItemFormatter
import com.thehairydog.pokeinfuser.util.SoundUtil
import net.minecraft.core.component.DataComponents
import net.minecraft.network.chat.Component
import net.minecraft.network.chat.Style
import net.minecraft.server.level.ServerPlayer
import net.minecraft.world.SimpleContainer
import net.minecraft.world.entity.player.Player
import net.minecraft.world.inventory.AbstractContainerMenu
import net.minecraft.world.inventory.MenuType
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.Items
import net.minecraft.world.item.component.ItemLore

class PokeInfuserShinyEditor(
    syncId: Int,
    private val player: ServerPlayer,
    private val pokemon: Pokemon
) : AbstractContainerMenu(MenuType.GENERIC_9x5, syncId) {

    private val container = SimpleContainer(45)
    private val shinyCost = 500
    private val revertCost = 100

    init {
        for (i in 0 until 45) {
            val slotX = 8 + (i % 9) * 18
            val slotY = 18 + (i / 9) * 18

            val itemStack = when (i) {
                22 -> initialiseShinyItem()
                40 -> PokeItemFormatter.configureBackItem()
                else -> ItemStack.EMPTY
            }

            val slot = LockedSlot(container, i, slotX, slotY)
            container.setItem(i, itemStack)
            addSlot(slot)
        }

        val playerInventory = player.inventory
        for (row in 0 until 3) {
            for (col in 0 until 9) {
                val index = col + row * 9 + 9
                addSlot(LockedSlot(playerInventory, index, 8 + col * 18, 140 + row * 18))
            }
        }

        for (col in 0 until 9) {
            addSlot(LockedSlot(playerInventory, col, 8 + col * 18, 198))
        }
    }

    private fun handleClick(slotId: Int) {
        SoundUtil.playClickSound(player)
        when (slotId) {
            22 -> openConfirmShiny()
            40 -> PokeInfuserOpenMenus.openEditPage(player, pokemon)
        }
    }

    private fun initialiseShinyItem(): ItemStack {
        val stack = ItemStack(Items.NETHER_STAR)

        val name = if (pokemon.shiny) "Revert Shiny" else "Make Shiny"
        val cost = if (pokemon.shiny) revertCost else shinyCost

        stack.set(
            DataComponents.CUSTOM_NAME,
            Component.literal(name).withStyle(
                Style.EMPTY.withColor(ColourUtil.confirmColor).withBold(true).withItalic(false)
            )
        )

        val lore = mutableListOf<Component>()
        lore.add(Component.literal("Current Shiny: ${if (pokemon.shiny) "Yes" else "No"}").withStyle(Style.EMPTY.withColor(ColourUtil.white)))
        lore.add(Component.literal("Cost: ✦$cost").withStyle(Style.EMPTY.withColor(ColourUtil.essenceCurrencyColor).withBold(true)))
        lore.add(Component.literal("Click to ${if (pokemon.shiny) "revert" else "apply"} shiny").withStyle(Style.EMPTY.withColor(ColourUtil.white)))

        stack.set(DataComponents.LORE, ItemLore(lore))
        return stack
    }

    private fun openConfirmShiny() {
        val isCurrentlyShiny = pokemon.shiny
        val cost = if (isCurrentlyShiny) revertCost else shinyCost
        val previewStack = initialiseShinyItem()

        PokeInfuserOpenMenus.openConfirmInfuse(
            pokemon,
            player,
            cost,
            previewStack,
            onConfirm = { pkmn, _ ->
                pkmn.shiny = !pkmn.shiny
                slots[22].set(initialiseShinyItem()) // Refresh the Nether Star
            },
            onCancel = { _ -> PokeInfuserOpenMenus.openShinyPage(player, pokemon) }
        )
    }

    override fun quickMoveStack(player: Player, i: Int) = ItemStack.EMPTY
    override fun stillValid(player: Player) = true
}
