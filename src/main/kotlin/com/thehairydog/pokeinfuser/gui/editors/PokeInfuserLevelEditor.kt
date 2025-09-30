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
import net.minecraft.world.inventory.ClickType
import net.minecraft.world.inventory.MenuType
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.Items
import net.minecraft.world.item.component.ItemLore

class PokeInfuserLevelEditor(
    syncId: Int,
    private val player: ServerPlayer,
    private val pokemon: Pokemon
) : AbstractContainerMenu(MenuType.GENERIC_9x5, syncId) {

    private val container = SimpleContainer(45)
    private val costPerLevel = 20
    private var plannedChange = 0

    init {
        for (i in 0 until 45) {
            val slotX = 8 + (i % 9) * 18
            val slotY = 18 + (i / 9) * 18

            val itemStack = when (i) {
                13 -> initialiseIncrement(true)      // Add levels
                22 -> initialiseLevelInfo()          // Middle item
                31 -> initialiseIncrement(false)     // Remove levels
                40 -> PokeItemFormatter.configureBackItem() // Back button
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

    override fun clicked(slotId: Int, dragType: Int, clickType: ClickType, player: Player) {
        if (slotId in slots.indices) {
            val slot = slots[slotId]
            if (slot.container === player.inventory) {
                return  // Prevent interacting with inventory
            }
        }
        if (slotId !in slots.indices) {
            super.clicked(slotId, dragType, clickType, player)
            return
        }

        when (clickType) {
            ClickType.PICKUP -> {
                if (dragType == 0) handleSlotAction(slotId, "LEFT")
                else handleSlotAction(slotId, "RIGHT")
            }
            ClickType.QUICK_MOVE -> handleSlotAction(slotId, "SHIFT_LEFT")
            else -> super.clicked(slotId, dragType, clickType, player)
        }
    }

    private fun handleSlotAction(slotId: Int, action: String) {
        SoundUtil.playClickSound(player)
        when (slotId) {
            13 -> updateLevelChange(true, action)   // Add
            31 -> updateLevelChange(false, action)  // Subtract
            22 -> if (action == "LEFT") openConfirmPage()
            40 -> PokeInfuserOpenMenus.openEditPage(player, pokemon)
        }
    }

    private fun updateLevelChange(isAdd: Boolean, action: String) {
        val baseLevel = pokemon.level
        val maxLevel = 100
        val minLevel = 1

        val changeAmount = when (action) {
            "LEFT" -> 1
            "RIGHT" -> 10
            "SHIFT_LEFT" -> if (isAdd) maxLevel - (baseLevel + plannedChange) else (baseLevel + plannedChange) - minLevel
            else -> 0
        }

        plannedChange += if (isAdd) changeAmount else -changeAmount
        val newLevel = (baseLevel + plannedChange).coerceIn(minLevel, maxLevel)
        plannedChange = newLevel - baseLevel

        // Refresh buttons + middle
        slots[13].set(initialiseIncrement(true))
        slots[22].set(initialiseLevelInfo())
        slots[31].set(initialiseIncrement(false))

        broadcastChanges()
    }

    private fun initialiseLevelInfo(): ItemStack {
        val baseLevel = pokemon.level
        val finalLevel = (baseLevel + plannedChange).coerceIn(1, 100)

        val stack = ItemStack(Items.EXPERIENCE_BOTTLE)
        stack.set(
            DataComponents.CUSTOM_NAME,
            Component.literal("Level Editor").withStyle(
                Style.EMPTY.withColor(ColourUtil.white).withBold(true).withItalic(false)
            )
        )

        val lore = mutableListOf<Component>()
        lore.add(Component.literal("Current Level: $baseLevel").withStyle(Style.EMPTY.withColor(ColourUtil.white)))
        if (plannedChange != 0) {
            lore.add(Component.literal("Planned Level: $finalLevel").withStyle(Style.EMPTY.withColor(ColourUtil.confirmColor)))
            val totalCost = plannedChange * costPerLevel
            lore.add(Component.literal("Cost: ✦$totalCost").withStyle(Style.EMPTY.withColor(ColourUtil.essenceCurrencyColor)))
            lore.add(Component.literal("Click to confirm infusion").withStyle(Style.EMPTY.withColor(ColourUtil.white)))
        } else {
            lore.add(Component.literal("No level changes selected").withStyle(Style.EMPTY.withColor(ColourUtil.white)))
        }

        stack.set(DataComponents.LORE, ItemLore(lore))
        return stack
    }

    private fun initialiseIncrement(isAdd: Boolean): ItemStack {
        val currentLevel = pokemon.level + plannedChange
        val maxReached = (isAdd && currentLevel >= 100) || (!isAdd && currentLevel <= 1)

        val stack = ItemStack(
            when {
                maxReached && isAdd -> Items.MAGENTA_STAINED_GLASS_PANE
                maxReached && !isAdd -> Items.BLACK_STAINED_GLASS_PANE
                isAdd -> Items.GREEN_STAINED_GLASS_PANE
                else -> Items.RED_STAINED_GLASS_PANE
            }
        )

        val display = if (isAdd) "+ Level" else "- Level"
        stack.set(
            DataComponents.CUSTOM_NAME,
            Component.literal(display).withStyle(
                Style.EMPTY.withColor(
                    when {
                        maxReached && isAdd -> ColourUtil.magenta
                        maxReached && !isAdd -> ColourUtil.white
                        isAdd -> ColourUtil.confirmColor
                        else -> ColourUtil.cancelColor
                    }
                ).withItalic(false)
            )
        )

        val lore = if (maxReached) {
            listOf(Component.literal(if (isAdd) "Max level reached!" else "Min level reached!"))
        } else {
            listOf(
                Component.literal("Left click = ±1"),
                Component.literal("Right click = ±10"),
                Component.literal("Shift + Left = ${if (isAdd) "Max" else "Min"}")
            )
        }

        stack.set(DataComponents.LORE, ItemLore(lore))
        return stack
    }

    private fun openConfirmPage() {
        val totalCost = plannedChange * costPerLevel
        val previewStack = initialiseLevelInfo()

        PokeInfuserOpenMenus.openConfirmInfuse(
            pokemon,
            player,
            totalCost,
            previewStack,
            onConfirm = { pkmn, _ ->
                pkmn.level = (pkmn.level + plannedChange).coerceIn(1, 100)
                plannedChange = 0
                slots[22].set(initialiseLevelInfo())
            },
            onCancel = { _ -> PokeInfuserOpenMenus.openLevelsPage(player, pokemon) }
        )
    }

    override fun quickMoveStack(player: Player, i: Int): ItemStack = ItemStack.EMPTY
    override fun stillValid(player: Player): Boolean = true
}
