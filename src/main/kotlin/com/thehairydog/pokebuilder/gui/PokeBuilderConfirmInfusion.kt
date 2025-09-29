package com.thehairydog.pokebuilder.gui

import ClickableSlot
import com.cobblemon.mod.common.pokemon.Pokemon
import com.thehairydog.pokebuilder.gui.PokebuilderOpenMenus.openEditPage
import com.thehairydog.pokebuilder.gui.slotUtil.LockedSlot
import com.thehairydog.pokebuilder.pokeessence.PokeEssenceHandler
import com.thehairydog.pokebuilder.util.ColourUtil
import com.thehairydog.pokebuilder.util.ColourUtil.essenceCurrencyColor
import com.thehairydog.pokebuilder.util.ColourUtil.white
import net.minecraft.core.component.DataComponents
import net.minecraft.network.chat.Component
import net.minecraft.network.chat.Style
import net.minecraft.network.chat.TextColor
import net.minecraft.server.level.ServerPlayer
import net.minecraft.world.SimpleContainer
import net.minecraft.world.entity.player.Player
import net.minecraft.world.inventory.AbstractContainerMenu
import net.minecraft.world.inventory.MenuType
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.Items
import net.minecraft.world.item.component.ItemLore

class PokeBuilderConfirmInfusion(
    syncId: Int,
    private val player: ServerPlayer,
    private val pokemon: Pokemon,
    private val cost: Int,
    private val itemStack: ItemStack,
    private val onConfirm: (Pokemon, ServerPlayer) -> Unit,
    private val onCancel: (ServerPlayer) -> Unit
) : AbstractContainerMenu(MenuType.GENERIC_9x3, syncId) {

    val container = SimpleContainer(27)


    init {
        setupSlots()
    }

    private fun setupSlots() {
        for (i in 0 until 27) {
            val slotX = 8 + (i % 9) * 18
            val slotY = 18 + (i / 9) * 18

            val stack = when (i) {
                11 -> createConfirmItem()
                13 -> itemStack
                15 -> createCancelItem()
                else -> ItemStack.EMPTY
            }

            val slot = if (stack.isEmpty) {
                LockedSlot(container, i, slotX, slotY)
            } else {
                ClickableSlot(container, i, slotX, slotY) {
                    when (i) {
                        11 -> tryInfuse()
                        15 -> cancel()
                    }
                }
            }

            container.setItem(i, stack)
            addSlot(slot)
        }
    }

    private fun createConfirmItem(): ItemStack {
        val stack = ItemStack(Items.LIME_CONCRETE)
        val balance = PokeEssenceHandler.get(player)
        val canAfford = balance >= cost

        stack.set(DataComponents.CUSTOM_NAME, Component.literal("Confirm Infusion")
            .withStyle(Style.EMPTY.withColor(if (canAfford) ColourUtil.confirmColor else ColourUtil.cancelColor)))

        val lore = listOf(
            Component.literal("Cost: ").withStyle(Style.EMPTY.withColor(white))
                .append(Component.literal("✦$cost").withStyle(Style.EMPTY.withColor(essenceCurrencyColor))),
            Component.literal("Balance: ").withStyle(Style.EMPTY.withColor(white))
                .append(Component.literal("✦$balance").withStyle(Style.EMPTY.withColor(essenceCurrencyColor))),
            Component.literal(if (canAfford) "Click to Infuse" else "You cannot afford this")
                .withStyle(Style.EMPTY.withColor(white).withItalic(true))
        )

        stack.set(DataComponents.LORE, ItemLore(lore))
        return stack
    }

    private fun createCancelItem(): ItemStack {
        val stack = ItemStack(Items.RED_CONCRETE)
        stack.set(DataComponents.CUSTOM_NAME, Component.literal("Cancel")
            .withStyle(Style.EMPTY.withColor(ColourUtil.cancelColor)))
        val lore = listOf(Component.literal("Click to cancel").withStyle(Style.EMPTY.withColor(white).withItalic(true)))
        stack.set(DataComponents.LORE, ItemLore(lore))
        return stack
    }

    fun tryInfuse() {
        if (PokeEssenceHandler.remove(player, cost)) {
            onConfirm(pokemon, player)
            player.sendSystemMessage(Component.literal("Infusion successful!"))
            openEditPage(player, pokemon)
        } else {
            player.sendSystemMessage(Component.literal("Not enough Essence!"))
        }
    }

    fun cancel() {
        onCancel(player)
    }

    override fun quickMoveStack(player: Player, index: Int): ItemStack? = ItemStack.EMPTY
    override fun stillValid(player: Player): Boolean = true
}
