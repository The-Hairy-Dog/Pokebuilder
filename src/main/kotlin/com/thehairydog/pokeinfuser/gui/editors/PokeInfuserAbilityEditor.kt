package com.thehairydog.pokeinfuser.gui.editors

import com.cobblemon.mod.common.api.Priority
import com.cobblemon.mod.common.api.item.ability.AbilityChanger
import com.cobblemon.mod.common.api.abilities.AbilityTemplate
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
import net.minecraft.world.inventory.ClickType
import net.minecraft.world.inventory.MenuType
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.Items
import net.minecraft.world.item.component.ItemLore

class PokeInfuserAbilityEditor(
    syncId: Int,
    private val player: ServerPlayer,
    private val pokemon: Pokemon
) : AbstractContainerMenu(MenuType.GENERIC_9x5, syncId) {

    private val container = SimpleContainer(45)
    private val standardAbilityCost = 100
    private val hiddenAbilityCost = 300

    private var selectedAbility: Pair<AbilityTemplate, Priority>? = null
    private var selectedCost = 0

    private val standardAbilities = AbilityChanger.COMMON_ABILITY.queryPossible(pokemon).toList()
    private val hiddenAbility = AbilityChanger.HIDDEN_ABILITY.queryPossible(pokemon).firstOrNull()

    init {
        for (i in 0 until 45) {
            val slotX = 8 + (i % 9) * 18
            val slotY = 18 + (i / 9) * 18

            val itemStack = when {
                i in 10..16 && i - 10 < standardAbilities.size -> initialiseAbilityItem(standardAbilities[i - 10], false)
                i == 22 -> hiddenAbility?.let { initialiseAbilityItem(it, true) } ?: ItemStack.EMPTY
                i == 40 -> PokeItemFormatter.configureBackItem()
                else -> ItemStack.EMPTY
            }

            val slot = LockedSlot(container, i, slotX, slotY)
            container.setItem(i, itemStack)
            addSlot(slot)
        }

        // Player inventory
        val playerInventory = player.inventory
        for (row in 0 until 3) {
            for (col in 0 until 9) {
                val index = col + row * 9 + 9
                addSlot(LockedSlot(playerInventory, index, 8 + col * 18, 140 + row * 18))
            }
        }
        for (col in 0 until 9) addSlot(LockedSlot(playerInventory, col, 8 + col * 18, 198))
    }

    override fun clicked(slotId: Int, dragType: Int, clickType: ClickType, player: Player) {
        if (slotId in slots.indices && slots[slotId].container === player.inventory) return

        when (clickType) {
            ClickType.PICKUP -> handleSlotAction(slotId, if (dragType == 0) "LEFT" else "RIGHT")
            ClickType.QUICK_MOVE -> handleSlotAction(slotId, "SHIFT_LEFT")
            else -> super.clicked(slotId, dragType, clickType, player)
        }
    }

    private fun handleSlotAction(slotId: Int, action: String) {
        SoundUtil.playClickSound(player)

        when (slotId) {
            in 10..16 if slotId - 10 < standardAbilities.size -> openConfirm(standardAbilities[slotId - 10], false)
            22 -> hiddenAbility?.let { openConfirm(it, true) }
            40 -> PokeInfuserOpenMenus.openEditPage(player, pokemon)
        }
    }

    private fun openConfirm(abilityPair: Pair<AbilityTemplate, Priority>, hidden: Boolean) {
        selectedAbility = abilityPair
        selectedCost = if (hidden) hiddenAbilityCost else standardAbilityCost

        val previewItem = initialiseAbilityItem(abilityPair, hidden)

        PokeInfuserOpenMenus.openConfirmInfuse(
            pokemon,
            player,
            selectedCost,
            previewItem,
            onConfirm = { pkmn, _ ->
                val changer = if (hidden) AbilityChanger.HIDDEN_ABILITY else AbilityChanger.COMMON_ABILITY
                // Perform the change using Cobblemon API
                changer.performChange(pkmn)
            },
            onCancel = { _ -> PokeInfuserOpenMenus.openAbilityPage(player, pokemon) }
        )
    }

    private fun initialiseAbilityItem(pair: Pair<AbilityTemplate, Priority>, hidden: Boolean): ItemStack {
        val stack = if (hidden) ItemStack(Items.NETHER_STAR) else ItemStack(Items.BLAZE_POWDER)

        stack.set(
            DataComponents.CUSTOM_NAME,
            Component.literal(pair.first.name).withStyle(
                Style.EMPTY.withColor(if (hidden) ColourUtil.essenceCurrencyColor else ColourUtil.confirmColor)
                    .withBold(true)
                    .withItalic(false)
            )
        )

        val lore = mutableListOf<Component>()
        lore.add(Component.literal("Click to ${if (hidden) "unlock hidden" else "set"} ability").withStyle(Style.EMPTY.withColor(ColourUtil.white)))
        lore.add(Component.literal("Cost: ✦$selectedCost").withStyle(Style.EMPTY.withColor(ColourUtil.essenceCurrencyColor)))
        stack.set(DataComponents.LORE, ItemLore(lore))
        return stack
    }

    override fun quickMoveStack(player: Player, i: Int): ItemStack = ItemStack.EMPTY
    override fun stillValid(player: Player): Boolean = true
}
