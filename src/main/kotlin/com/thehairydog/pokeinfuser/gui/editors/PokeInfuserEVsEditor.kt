package com.thehairydog.pokeinfuser.gui.editors

import com.cobblemon.mod.common.CobblemonItems
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
import net.minecraft.util.Unit
import net.minecraft.world.SimpleContainer
import net.minecraft.world.entity.player.Player
import net.minecraft.world.inventory.AbstractContainerMenu
import net.minecraft.world.inventory.ClickType
import net.minecraft.world.inventory.MenuType
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.component.ItemLore
import net.minecraft.world.item.Items

class PokeInfuserEVsEditor(
    syncId: Int,
    private val player: ServerPlayer,
    private val pokemon: Pokemon
) : AbstractContainerMenu(MenuType.GENERIC_9x5, syncId) {

    private val container = SimpleContainer(45)
    private val costPerEV = 1
    private var checkOutList: MutableMap<Stats, Int> = mutableMapOf()

    // Slot mapping
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
                // Main EV items
                19 -> initialiseEVInfo(Stats.HP)
                20 -> initialiseEVInfo(Stats.ATTACK)
                21 -> initialiseEVInfo(Stats.SPECIAL_ATTACK)
                22 -> initialiseNetherStar()
                23 -> initialiseEVInfo(Stats.DEFENCE)
                24 -> initialiseEVInfo(Stats.SPECIAL_DEFENCE)
                25 -> initialiseEVInfo(Stats.SPEED)
                // Increment panes
                in incrementSlotMap.keys -> initialiseIncrements(isAdd = i in addSlots, stats = incrementSlotMap[i]!!)
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
                addSlot(LockedSlot(playerInventory, index, 8 + col * 18, 84 + row * 18))
            }
        }

        for (col in 0 until 9) {
            addSlot(LockedSlot(playerInventory, col, 8 + col * 18, 142))
        }

    }


    override fun clicked(slotId: Int, dragType: Int, clickType: ClickType, player: Player) {

        if (slotId in slots.indices) {
            val slot = slots[slotId]
            if (slot.container === player.inventory) {
                return  // block all clicks on player inventory
            }
        }

        if (slotId !in slots.indices) {
            super.clicked(slotId, dragType, clickType, player)
            return
        }

        when (clickType) {
            ClickType.PICKUP -> if (dragType == 0) {
                handleSlotAction(slotId, "LEFT")
            } else {
                handleSlotAction(slotId, "RIGHT")
            }
            ClickType.QUICK_MOVE -> {
                handleSlotAction(slotId, "SHIFT_LEFT")
                // CANCEL default shift-click behavior to prevent duplication
                return
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
                updateEVInfo(stat, isAdd, action)
            }
        }
    }

    private fun getTotalEV(stat: Stats) = (pokemon.evs[stat] ?: 0) + (checkOutList[stat] ?: 0)
    private fun getTotalEVs() = Stats.entries.toTypedArray().sumOf { getTotalEV(it) }

    private fun safeEVAdd(stat: Stats, amount: Int): Int {
        val currentEV = getTotalEV(stat)
        val totalEVs = getTotalEVs()
        val newEV = (currentEV + amount).coerceIn(0, 252)
        return (510 - totalEVs + currentEV).coerceAtMost(newEV)
    }

    private fun updateEVInfo(stat: Stats, isAdd: Boolean, action: String) {
        val evChange = when (action) {
            "LEFT" -> if (isAdd) 1 else 1
            "RIGHT" -> if (isAdd) 10 else 10
            "SHIFT_LEFT" -> if (isAdd) 252 - getTotalEV(stat) else getTotalEV(stat)
            else -> 0
        }

        val newEV = if (isAdd) safeEVAdd(stat, evChange) else (getTotalEV(stat) - evChange).coerceAtLeast(0)
        checkOutList[stat] = newEV - (pokemon.evs[stat] ?: 0)

        // Update increment slots
        incrementSlotMap.forEach { (slotIndex, s) ->
            slots[slotIndex].set(initialiseIncrements(slotIndex in addSlots, s))
        }

        // Update EV display slots
        val evSlots = mapOf(
            19 to Stats.HP, 20 to Stats.ATTACK, 21 to Stats.SPECIAL_ATTACK,
            23 to Stats.DEFENCE, 24 to Stats.SPECIAL_DEFENCE, 25 to Stats.SPEED
        )
        evSlots.forEach { (slotIndex, s) -> slots[slotIndex].set(initialiseEVInfo(s)) }

        // Update confirm button
        slots[22].set(initialiseNetherStar())

        broadcastChanges()
    }

    fun initialiseEVInfo(stats: Stats): ItemStack {
        val evValue = getTotalEV(stats)
        val itemStack = when (stats) {
            Stats.HP -> ItemStack(CobblemonItems.HP_UP)
            Stats.ATTACK -> ItemStack(CobblemonItems.PROTEIN)
            Stats.SPECIAL_ATTACK -> ItemStack(CobblemonItems.CALCIUM)
            Stats.DEFENCE -> ItemStack(CobblemonItems.IRON)
            Stats.SPECIAL_DEFENCE -> ItemStack(CobblemonItems.ZINC)
            Stats.SPEED -> ItemStack(CobblemonItems.CARBOS)
            else -> ItemStack(Items.BOOK)
        }

        // Hide additional tooltips
        itemStack.set(DataComponents.HIDE_ADDITIONAL_TOOLTIP, Unit.INSTANCE)

        val displayName = when (stats) {
            Stats.HP -> Component.literal("HP EV").withStyle(Style.EMPTY.withColor(ColourUtil.hpColor).withItalic(false))
            Stats.ATTACK -> Component.literal("Attack EV").withStyle(Style.EMPTY.withColor(ColourUtil.attackColor).withItalic(false))
            Stats.DEFENCE -> Component.literal("Defense EV").withStyle(Style.EMPTY.withColor(ColourUtil.defenseColor).withItalic(false))
            Stats.SPECIAL_ATTACK -> Component.literal("Sp. Atk EV").withStyle(Style.EMPTY.withColor(ColourUtil.spAttackColor).withItalic(false))
            Stats.SPECIAL_DEFENCE -> Component.literal("Sp. Def EV").withStyle(Style.EMPTY.withColor(ColourUtil.spDefenseColor).withItalic(false))
            Stats.SPEED -> Component.literal("Speed EV").withStyle(Style.EMPTY.withColor(ColourUtil.speedColor).withItalic(false))
            else -> Component.literal("")
        }

        itemStack.set(DataComponents.CUSTOM_NAME, displayName)
        itemStack.set(DataComponents.LORE, ItemLore(listOf(
            Component.literal(evValue.toString()).withStyle(Style.EMPTY.withColor(ColourUtil.white).withItalic(false))
        )))

        return itemStack
    }


    fun initialiseNetherStar(): ItemStack {
        val itemStack = ItemStack(Items.NETHER_STAR)
        itemStack.set(
            DataComponents.CUSTOM_NAME,
            Component.literal("Confirm EV infusion:").withStyle(Style.EMPTY.withColor(ColourUtil.essenceCurrencyColor).withBold(true).withItalic(false))
        )

        val lore = mutableListOf<Component>()
        val totalCost = checkOutList.entries.sumOf { (_, amount) -> amount * costPerEV }

        if (checkOutList.isEmpty() || totalCost == 0) {
            lore.add(Component.literal("Select EV changes to list infusion(s) here").withStyle(Style.EMPTY.withColor(ColourUtil.white).withItalic(false)))
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
                        Component.literal("${statName.first}: $amount EV").withStyle(Style.EMPTY.withColor(statName.second).withItalic(false))
                            .append(Component.literal("✦${amount * costPerEV}").withStyle(Style.EMPTY.withColor(ColourUtil.essenceCurrencyColor).withBold(true).withItalic(false)))
                    )
                }
            }

            lore.add(Component.literal(" "))
            lore.add(Component.literal("Total cost: ").withStyle(Style.EMPTY.withColor(ColourUtil.white))
                .append(Component.literal("✦$totalCost").withStyle(Style.EMPTY.withColor(ColourUtil.essenceCurrencyColor).withBold(true))))
        }

        itemStack.set(DataComponents.LORE, ItemLore(lore))
        return itemStack
    }

    fun initialiseIncrements(isAdd: Boolean, stats: Stats): ItemStack {
        val currentEV = getTotalEV(stats)
        val totalCurrentEV = getTotalEVs()
        val maxReached = (isAdd && (currentEV >= 252 || totalCurrentEV >= 510)) || (!isAdd && currentEV <= 0)

        val finalStack = when {
            maxReached && isAdd -> ItemStack(Items.MAGENTA_STAINED_GLASS_PANE)
            maxReached && !isAdd -> ItemStack(Items.BLACK_STAINED_GLASS_PANE)
            else -> ItemStack(if (isAdd) Items.GREEN_STAINED_GLASS_PANE else Items.RED_STAINED_GLASS_PANE)
        }

        val displayName = Component.literal(
            when (stats) {
                Stats.HP -> if (isAdd) "+ HP EV" else "- HP EV"
                Stats.ATTACK -> if (isAdd) "+ Atk EV" else "- Atk EV"
                Stats.SPECIAL_ATTACK -> if (isAdd) "+ Sp. Atk EV" else "- Sp. Atk EV"
                Stats.DEFENCE -> if (isAdd) "+ Def EV" else "- Def EV"
                Stats.SPECIAL_DEFENCE -> if (isAdd) "+ Sp. Def EV" else "- Sp. Def EV"
                Stats.SPEED -> if (isAdd) "+ Spd EV" else "- Spd EV"
                else -> ""
            }
        ).withStyle(
            Style.EMPTY.withColor(
                when {
                    maxReached && isAdd -> ColourUtil.magenta
                    maxReached && !isAdd -> ColourUtil.white
                    isAdd -> ColourUtil.confirmColor
                    else -> ColourUtil.cancelColor
                }
            ).withItalic(false)
        )

        val lore = if (maxReached) {
            listOf(Component.literal(if (isAdd) "Max EV reached!" else "Min EV reached!").withStyle(Style.EMPTY.withColor(if (isAdd) ColourUtil.magenta else ColourUtil.white).withItalic(false)))
        } else {
            listOf(
                Component.literal("${if (isAdd) "Add" else "Take"} 1 EV: ").withStyle(Style.EMPTY.withColor(ColourUtil.white)).append(Component.literal("✦${costPerEV}").withStyle(Style.EMPTY.withColor(ColourUtil.essenceCurrencyColor))),
                Component.literal("${if (isAdd) "Add" else "Take"} 10 EVs: ").withStyle(Style.EMPTY.withColor(ColourUtil.white)).append(Component.literal("✦${10 * costPerEV}").withStyle(Style.EMPTY.withColor(ColourUtil.essenceCurrencyColor))),
                Component.literal("${if (isAdd) "Add" else "Take"} max EV: ").withStyle(Style.EMPTY.withColor(ColourUtil.white)).append(Component.literal("✦${(252 - currentEV) * costPerEV}").withStyle(Style.EMPTY.withColor(ColourUtil.essenceCurrencyColor)))
            )
        }

        finalStack.set(DataComponents.CUSTOM_NAME, displayName)
        finalStack.set(DataComponents.LORE, ItemLore(lore))
        return finalStack
    }


    private fun updateInfuseList() {
        val totalCost = checkOutList.entries.sumOf { (_, amount) -> amount * costPerEV }
        val netherStar = initialiseNetherStar()

        PokeInfuserOpenMenus.openConfirmInfuse(
            pokemon,
            player,
            totalCost,
            netherStar,
            onConfirm = { pkmn, _ ->
                checkOutList.forEach { (stat, change) ->
                    val current = pkmn.evs[stat] ?: 0
                    pkmn.evs[stat] = (current + change).coerceIn(0, 252)
                }
                checkOutList.clear()
                Stats.entries.forEach { s -> updateEVInfo(s, true, "LEFT") } // refresh UI
            },
            onCancel = { _ -> PokeInfuserOpenMenus.openEVsPage(player, pokemon) }
        )
    }

    override fun quickMoveStack(player: Player, i: Int): ItemStack = ItemStack.EMPTY
    override fun stillValid(player: Player): Boolean = true
}
