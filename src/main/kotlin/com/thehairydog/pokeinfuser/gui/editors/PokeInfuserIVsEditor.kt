package com.thehairydog.pokeinfuser.gui.editors

import com.cobblemon.mod.common.api.pokemon.stats.Stats
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

class PokeInfuserIVsEditor(
    syncId: Int,
    private val player: ServerPlayer,
    private val pokemon: Pokemon
) : AbstractContainerMenu(MenuType.GENERIC_9x5, syncId) {

    private val container = SimpleContainer(45)
    private val costPerIV = 10
    private var checkOutList: MutableMap<Stats, Int> = mutableMapOf()

    // Slot mapping for increment/decrement buttons
    private val incrementSlotMap = mapOf(
        10 to Stats.HP, 11 to Stats.ATTACK, 12 to Stats.SPECIAL_ATTACK,
        14 to Stats.DEFENCE, 15 to Stats.SPECIAL_DEFENCE, 16 to Stats.SPEED,
        28 to Stats.HP, 29 to Stats.ATTACK, 30 to Stats.SPECIAL_ATTACK,
        32 to Stats.DEFENCE, 33 to Stats.SPECIAL_DEFENCE, 34 to Stats.SPEED
    )
    private val addSlots = setOf(10, 11, 12, 14, 15, 16)

    init {
        for (i in 0 until 45) {
            val slotX = 8 + (i % 9) * 18
            val slotY = 18 + (i / 9) * 18

            val itemStack = when (i) {
                // IV display slots
                19 -> initialiseIVInfo(Stats.HP)
                20 -> initialiseIVInfo(Stats.ATTACK)
                21 -> initialiseIVInfo(Stats.SPECIAL_ATTACK)
                22 -> initialiseNetherStar()
                23 -> initialiseIVInfo(Stats.DEFENCE)
                24 -> initialiseIVInfo(Stats.SPECIAL_DEFENCE)
                25 -> initialiseIVInfo(Stats.SPEED)
                // Increment/decrement buttons
                in incrementSlotMap.keys -> initialiseIncrements(i in addSlots, incrementSlotMap[i]!!)
                // Back button
                31 -> PokeItemFormatter.configureBackItem()
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
                return  // Block all clicks on player inventory
            }
        }
        if (slotId !in slots.indices) {
            super.clicked(slotId, dragType, clickType, player)
            return
        }

        when (clickType) {
            ClickType.PICKUP -> {
                if (dragType == 0) handleSlotAction(slotId, "LEFT")   // left click
                else handleSlotAction(slotId, "RIGHT")                // right click
            }
            ClickType.QUICK_MOVE -> {
                handleSlotAction(slotId, "SHIFT_LEFT")               // shift click
                return  // CANCEL default shift-click to prevent duplication
            }
            else -> super.clicked(slotId, dragType, clickType, player)
        }
    }

    private fun handleSlotAction(slotId: Int, action: String) {
        SoundUtil.playClickSound(player)

        when (slotId) {
            22 -> if (action == "LEFT") updateInfuseList()
            31 -> PokeInfuserOpenMenus.openEditPage(player, pokemon)
            in incrementSlotMap.keys -> {
                val stat = incrementSlotMap[slotId]!!
                val isAdd = slotId in addSlots
                updateIVInfo(stat, isAdd, action)
            }
        }
    }

    private fun getTotalIV(stat: Stats) = (pokemon.ivs[stat] ?: 0) + (checkOutList[stat] ?: 0)

    private fun updateIVInfo(stat: Stats, isAdd: Boolean, action: String) {
        val ivChange = when (action) {
            "LEFT" -> if (isAdd) 1 else 1
            "RIGHT" -> if (isAdd) 5 else 5
            "SHIFT_LEFT" -> if (isAdd) 31 else 31 // Shift instantly maxes/mines
            else -> 0
        }

        val currentIV = getTotalIV(stat)
        val newIV = if (isAdd) (currentIV + ivChange).coerceAtMost(31) else (currentIV - ivChange).coerceAtLeast(0)
        checkOutList[stat] = newIV - (pokemon.ivs[stat] ?: 0)

        // Refresh increment slots
        incrementSlotMap.forEach { (slotIndex, s) ->
            slots[slotIndex].set(initialiseIncrements(slotIndex in addSlots, s))
        }

        // Refresh IV display slots
        val ivSlots = mapOf(
            19 to Stats.HP, 20 to Stats.ATTACK, 21 to Stats.SPECIAL_ATTACK,
            23 to Stats.DEFENCE, 24 to Stats.SPECIAL_DEFENCE, 25 to Stats.SPEED
        )
        ivSlots.forEach { (slotIndex, s) -> slots[slotIndex].set(initialiseIVInfo(s)) }

        // Refresh Nether Star
        slots[22].set(initialiseNetherStar())

        broadcastChanges()
    }

    fun initialiseIVInfo(stats: Stats): ItemStack {
        val ivValue = getTotalIV(stats)
        val itemStack = if (ivValue >= 31) ItemStack(Items.ENCHANTED_BOOK) else ItemStack(Items.BOOK)

        val displayName = when (stats) {
            Stats.HP -> Component.literal("HP IV").withStyle(Style.EMPTY.withColor(ColourUtil.hpColor).withItalic(false))
            Stats.ATTACK -> Component.literal("Attack IV").withStyle(Style.EMPTY.withColor(ColourUtil.attackColor).withItalic(false))
            Stats.DEFENCE -> Component.literal("Defense IV").withStyle(Style.EMPTY.withColor(ColourUtil.defenseColor).withItalic(false))
            Stats.SPECIAL_ATTACK -> Component.literal("Sp. Atk IV").withStyle(Style.EMPTY.withColor(ColourUtil.spAttackColor).withItalic(false))
            Stats.SPECIAL_DEFENCE -> Component.literal("Sp. Def IV").withStyle(Style.EMPTY.withColor(ColourUtil.spDefenseColor).withItalic(false))
            Stats.SPEED -> Component.literal("Speed IV").withStyle(Style.EMPTY.withColor(ColourUtil.speedColor).withItalic(false))
            else -> Component.literal("")
        }

        itemStack.set(DataComponents.CUSTOM_NAME, displayName)
        itemStack.set(DataComponents.LORE, ItemLore(listOf(Component.literal(ivValue.toString()).withStyle(Style.EMPTY.withColor(ColourUtil.white).withItalic(false)))))
        return itemStack
    }

    fun initialiseNetherStar(): ItemStack {
        val itemStack = ItemStack(Items.NETHER_STAR)
        itemStack.set(
            DataComponents.CUSTOM_NAME,
            Component.literal("Confirm IV infusion:").withStyle(
                Style.EMPTY.withColor(ColourUtil.essenceCurrencyColor).withBold(true).withItalic(false)
            )
        )

        val lore = mutableListOf<Component>()
        val totalCost = checkOutList.entries.sumOf { (_, amount) -> amount * costPerIV }

        if (checkOutList.isEmpty() || totalCost == 0) {
            lore.add(Component.literal("Select IV changes to list infusion(s) here").withStyle(Style.EMPTY.withColor(ColourUtil.white).withItalic(false)))
        } else {
            checkOutList.forEach { (stat, amount) ->
                if (amount != 0) {
                    val statName = when (stat) {
                        Stats.HP -> "HP" to ColourUtil.hpColor
                        Stats.ATTACK -> "Attack" to ColourUtil.attackColor
                        Stats.DEFENCE -> "Defense" to ColourUtil.defenseColor
                        Stats.SPECIAL_ATTACK -> "Sp. Atk" to ColourUtil.spAttackColor
                        Stats.SPECIAL_DEFENCE -> "Sp. Def" to ColourUtil.spDefenseColor
                        Stats.SPEED -> "Speed" to ColourUtil.speedColor
                        else -> stat.name to ColourUtil.white
                    }

                    lore.add(
                        Component.literal("${statName.first}: $amount IV ").withStyle(Style.EMPTY.withColor(statName.second).withItalic(false))
                            .append(Component.literal("✦${amount * costPerIV}").withStyle(Style.EMPTY.withColor(ColourUtil.essenceCurrencyColor).withBold(true).withItalic(false)))
                    )
                }
            }

            lore.add(Component.literal(" "))
            lore.add(Component.literal("Total cost: ").withStyle(Style.EMPTY.withColor(ColourUtil.white)).append(
                Component.literal("✦$totalCost").withStyle(Style.EMPTY.withColor(ColourUtil.essenceCurrencyColor).withBold(true))
            ))
        }

        itemStack.set(DataComponents.LORE, ItemLore(lore))
        return itemStack
    }

    fun initialiseIncrements(isAdd: Boolean, stats: Stats): ItemStack {
        val currentIV = getTotalIV(stats)
        val maxReached = (isAdd && currentIV >= 31) || (!isAdd && currentIV <= 0)

        val finalStack = when {
            maxReached && isAdd -> ItemStack(Items.MAGENTA_STAINED_GLASS_PANE)
            maxReached && !isAdd -> ItemStack(Items.BLACK_STAINED_GLASS_PANE)
            else -> ItemStack(if (isAdd) Items.GREEN_STAINED_GLASS_PANE else Items.RED_STAINED_GLASS_PANE)
        }

        val displayName = when (stats) {
            Stats.HP -> Component.literal(if (isAdd) "+ HP IV" else "- HP IV")
            Stats.ATTACK -> Component.literal(if (isAdd) "+ Atk IV" else "- Atk IV")
            Stats.SPECIAL_ATTACK -> Component.literal(if (isAdd) "+ Sp. Atk IV" else "- Sp. Atk IV")
            Stats.DEFENCE -> Component.literal(if (isAdd) "+ Def IV" else "- Def IV")
            Stats.SPECIAL_DEFENCE -> Component.literal(if (isAdd) "+ Sp. Def IV" else "- Sp. Def IV")
            Stats.SPEED -> Component.literal(if (isAdd) "+ Spd IV" else "- Spd IV")
            else -> Component.literal("")
        }.withStyle(
            Style.EMPTY.withColor(
                when {
                    maxReached && isAdd -> ColourUtil.magenta
                    maxReached && !isAdd -> ColourUtil.white
                    isAdd -> ColourUtil.confirmColor
                    else -> ColourUtil.cancelColor
                }
            ).withItalic(false)
        )

        val maxAdd1 = if (isAdd) minOf(1, 31 - currentIV) else minOf(1, currentIV)
        val maxAdd5 = if (isAdd) minOf(5, 31 - currentIV) else minOf(5, currentIV)
        val maxAdd10 = if (isAdd) minOf(10, 31 - currentIV) else minOf(10, currentIV)

        val lore = if (maxReached) {
            listOf(Component.literal(if (isAdd) "Max IV reached!" else "Min IV reached!").withStyle(Style.EMPTY.withColor(if (isAdd) ColourUtil.magenta else ColourUtil.white).withItalic(false)))
        } else {
            listOf(
                Component.literal("${if (isAdd) "Add" else "Take"} $maxAdd1 IV: ").withStyle(Style.EMPTY.withColor(ColourUtil.white).withItalic(false))
                    .append(Component.literal("✦${costPerIV * maxAdd1}").withStyle(Style.EMPTY.withColor(ColourUtil.essenceCurrencyColor))),
                Component.literal("${if (isAdd) "Add" else "Take"} $maxAdd5 IVs: ").withStyle(Style.EMPTY.withColor(ColourUtil.white).withItalic(false))
                    .append(Component.literal("✦${costPerIV * maxAdd5}").withStyle(Style.EMPTY.withColor(ColourUtil.essenceCurrencyColor))),
                Component.literal("${if (isAdd) "Add" else "Take"} $maxAdd10 IVs: ").withStyle(Style.EMPTY.withColor(ColourUtil.white).withItalic(false))
                    .append(Component.literal("✦${costPerIV * maxAdd10}").withStyle(Style.EMPTY.withColor(ColourUtil.essenceCurrencyColor)))
            )
        }

        finalStack.set(DataComponents.CUSTOM_NAME, displayName)
        finalStack.set(DataComponents.LORE, ItemLore(lore))
        return finalStack
    }

    private fun updateInfuseList() {
        val totalCost = checkOutList.entries.sumOf { (_, amount) -> amount * costPerIV }
        val netherStar = initialiseNetherStar()

        PokeInfuserOpenMenus.openConfirmInfuse(
            pokemon,
            player,
            totalCost,
            netherStar,
            onConfirm = { pkmn, _ ->
                checkOutList.forEach { (stat, change) ->
                    val current = pkmn.ivs[stat] ?: 0
                    pkmn.ivs[stat] = (current + change).coerceIn(0, 31)
                }
                checkOutList.clear()
                // Refresh editor
                Stats.entries.forEach { s -> updateIVInfo(s, true, "LEFT") }
            },
            onCancel = { _ -> PokeInfuserOpenMenus.openIVsPage(player, pokemon) }
        )
    }

    override fun quickMoveStack(player: Player, i: Int): ItemStack = ItemStack.EMPTY
    override fun stillValid(player: Player): Boolean = true
}
