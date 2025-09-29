package com.thehairydog.pokebuilder.gui.editors

import ClickableSlot
import com.cobblemon.mod.common.api.pokemon.stats.Stats
import com.cobblemon.mod.common.pokemon.Pokemon
import com.thehairydog.pokebuilder.gui.PokebuilderOpenMenus
import com.thehairydog.pokebuilder.gui.slotUtil.LockedSlot
import com.thehairydog.pokebuilder.util.ColourUtil
import com.thehairydog.pokebuilder.util.PokeItemFormatter
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

class PokeBuilderIVsEditor(
    syncId: Int,
    private val player: ServerPlayer,
    private val pokemon: Pokemon
) : AbstractContainerMenu(MenuType.GENERIC_9x5, syncId) {

    private val container = SimpleContainer(45)
    private val costPerIV = 10

    private var checkOutList: Map<Stats, Int> = mapOf()

    init {
        for (i in 0 until 45) {
            val slotX = 8 + (i % 9) * 18
            val slotY = 18 + (i / 9) * 18

            val itemStack = when (i) {
                // Main
                19 -> initialiseIVInfo(Stats.HP, pokemon)
                20 -> initialiseIVInfo(Stats.ATTACK, pokemon)
                21 -> initialiseIVInfo(Stats.SPECIAL_ATTACK, pokemon)
                22 -> initialiseNetherStar()
                23 -> initialiseIVInfo(Stats.DEFENCE, pokemon)
                24 -> initialiseIVInfo(Stats.SPECIAL_DEFENCE, pokemon)
                25 -> initialiseIVInfo(Stats.SPEED, pokemon)
                // Panes
                10 -> initialiseIncrements(ItemStack(Items.GREEN_STAINED_GLASS_PANE), Stats.HP)
                11 -> initialiseIncrements(ItemStack(Items.GREEN_STAINED_GLASS_PANE), Stats.ATTACK)
                12 -> initialiseIncrements(ItemStack(Items.GREEN_STAINED_GLASS_PANE), Stats.SPECIAL_ATTACK)
                14 -> initialiseIncrements(ItemStack(Items.GREEN_STAINED_GLASS_PANE), Stats.DEFENCE)
                15 -> initialiseIncrements(ItemStack(Items.GREEN_STAINED_GLASS_PANE), Stats.SPECIAL_DEFENCE)
                16 -> initialiseIncrements(ItemStack(Items.GREEN_STAINED_GLASS_PANE), Stats.SPEED)
                28 -> initialiseIncrements(ItemStack(Items.RED_STAINED_GLASS_PANE), Stats.HP)
                29 -> initialiseIncrements(ItemStack(Items.RED_STAINED_GLASS_PANE), Stats.ATTACK)
                30 -> initialiseIncrements(ItemStack(Items.RED_STAINED_GLASS_PANE), Stats.SPECIAL_ATTACK)
                32 -> initialiseIncrements(ItemStack(Items.RED_STAINED_GLASS_PANE), Stats.DEFENCE)
                33 -> initialiseIncrements(ItemStack(Items.RED_STAINED_GLASS_PANE), Stats.SPECIAL_DEFENCE)
                34 -> initialiseIncrements(ItemStack(Items.RED_STAINED_GLASS_PANE), Stats.SPEED)
                // Back
                31 -> PokeItemFormatter.configureBackItem()
                else -> ItemStack.EMPTY
            }

            val slot = LockedSlot(container, i, slotX, slotY)
            container.setItem(i, itemStack)
            addSlot(slot)
        }
    }

    override fun clicked(slotId: Int, dragType: Int, clickType: ClickType, player: Player) {
        if (slotId < 0 || slotId >= slots.size) {
            super.clicked(slotId, dragType, clickType, player)
            return
        }

        when (clickType) {
            ClickType.PICKUP -> {
                if (dragType == 0) {
                    handleSlotAction(slotId, "LEFT")
                } else if (dragType == 1) {
                    handleSlotAction(slotId, "RIGHT")
                }
            }
            ClickType.QUICK_MOVE -> {
                handleSlotAction(slotId, "SHIFT_LEFT")
            }
            else -> super.clicked(slotId, dragType, clickType, player)
        }
    }

    private fun handleSlotAction(slotId: Int, action: String) {
        when (slotId) {
            22 -> {
                if (action == "LEFT") updateInfuseList()
            }

            10, 28 -> updateIVInfo(slotId, action, Stats.HP)

            11, 29 -> updateIVInfo(slotId, action, Stats.ATTACK)

            12, 30 -> updateIVInfo(slotId, action, Stats.SPECIAL_ATTACK)

            14, 32 -> updateIVInfo(slotId, action, Stats.DEFENCE)

            15, 33 -> updateIVInfo(slotId, action, Stats.SPECIAL_DEFENCE)

            16, 34 -> updateIVInfo(slotId, action, Stats.SPEED)

            31 -> PokebuilderOpenMenus.openEditPage(player, pokemon)
        }
    }


    fun initialiseIVInfo(stats: Stats, pokemon: Pokemon): ItemStack {
        val itemStack = ItemStack(Items.BOOK)
        val displayName = when (stats) {
            Stats.HP -> Component.literal("HP IV").withStyle(Style.EMPTY.withColor(ColourUtil.hpColor))
            Stats.ATTACK -> Component.literal("Attack IV").withStyle(Style.EMPTY.withColor(ColourUtil.attackColor))
            Stats.DEFENCE -> Component.literal("Defense IV").withStyle(Style.EMPTY.withColor(ColourUtil.defenseColor))
            Stats.SPECIAL_ATTACK -> Component.literal("Sp. Atk IV").withStyle(Style.EMPTY.withColor(ColourUtil.spAttackColor))
            Stats.SPECIAL_DEFENCE -> Component.literal("Sp. Def IV").withStyle(Style.EMPTY.withColor(ColourUtil.spDefenseColor))
            Stats.SPEED -> Component.literal("Speed IV").withStyle(Style.EMPTY.withColor(ColourUtil.speedColor))
            else -> Component.literal("")
        }
        itemStack.set(DataComponents.CUSTOM_NAME, displayName)

        val loreComponent = Component.literal(pokemon.ivs[stats].toString())
            .withStyle(Style.EMPTY.withColor(ColourUtil.white))

        itemStack.set(DataComponents.LORE, ItemLore(listOf(loreComponent)))
        return itemStack
    }

    fun initialiseNetherStar(): ItemStack {
        val itemStack = ItemStack(Items.NETHER_STAR)
        itemStack.set(
            DataComponents.CUSTOM_NAME,
            Component.literal("Confirm Infusion:")
                .withStyle(Style.EMPTY.withColor(ColourUtil.essenceCurrencyColor).withBold(true).withItalic(false))
        )
        val lore: List<Component> = listOf(
            Component.literal("Select IV changes to display here for infusion")
                .withStyle(Style.EMPTY.withColor(ColourUtil.placeholderColor).withItalic(true))
        )
        itemStack.set(DataComponents.LORE, ItemLore(lore))
        return itemStack
    }

    fun initialiseIncrements(itemStack: ItemStack, stats: Stats): ItemStack {
        val baseIV = pokemon.ivs[stats] ?: 0
        val checkoutChange = checkOutList[stats] ?: 0
        val currentIV = baseIV + checkoutChange

        val isAdd = itemStack.item == Items.GREEN_STAINED_GLASS_PANE
        val maxReached = (isAdd && currentIV >= 31) || (!isAdd && currentIV <= 0)

        // Determine final pane color
        val finalStack = when {
            maxReached && isAdd -> ItemStack(Items.MAGENTA_STAINED_GLASS_PANE)
            maxReached && !isAdd -> ItemStack(Items.BLACK_STAINED_GLASS_PANE)
            else -> itemStack
        }

        // Build display name
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

        // Calculate max add/take values
        val maxAdd1 = if (isAdd) minOf(1, 31 - currentIV) else minOf(1, currentIV)
        val maxAdd5 = if (isAdd) minOf(5, 31 - currentIV) else minOf(5, currentIV)
        val maxAdd10 = if (isAdd) minOf(10, 31 - currentIV) else minOf(10, currentIV)

        // Build lore
        val lore: List<Component> = if (maxReached) {
            listOf(
                Component.literal(if (isAdd) "Max IV reached!" else "Min IV reached!")
                    .withStyle(
                        Style.EMPTY.withColor(if (isAdd) ColourUtil.magenta else ColourUtil.white).withItalic(true)
                    )
            )
        } else {
            listOf(
                Component.literal("${if (isAdd) "Add" else "Take"} $maxAdd1 IV: ")
                    .withStyle(Style.EMPTY.withColor(ColourUtil.white).withItalic(false))
                    .append("✦${costPerIV * maxAdd1}")
                    .withStyle(Style.EMPTY.withColor(ColourUtil.essenceCurrencyColor)),
                Component.literal("${if (isAdd) "Add" else "Take"} $maxAdd5 IVs: ")
                    .withStyle(Style.EMPTY.withColor(ColourUtil.white).withItalic(false))
                    .append("✦${costPerIV * maxAdd5}")
                    .withStyle(Style.EMPTY.withColor(ColourUtil.essenceCurrencyColor)),
                Component.literal("${if (isAdd) "Add" else "Take"} $maxAdd10 IVs: ")
                    .withStyle(Style.EMPTY.withColor(ColourUtil.white).withItalic(false))
                    .append("✦${costPerIV * maxAdd10}")
                    .withStyle(Style.EMPTY.withColor(ColourUtil.essenceCurrencyColor))
            )
        }

        finalStack.set(DataComponents.CUSTOM_NAME, displayName)
        finalStack.set(DataComponents.LORE, ItemLore(lore))
        return finalStack
    }



    fun updateIVInfo(slot: Int, action: String, stat: Stats) {
        // Determine how much to add/subtract
        val ivChange = when (action) {
            "LEFT" -> 1
            "RIGHT" -> 5
            "SHIFT_LEFT" -> 10
            else -> 0
        }

        val currentIV = (pokemon.ivs[stat] ?: 0) + (checkOutList[stat] ?: 0)
        val newIV = when (slot) {
            10,11,12,14,15,16 -> minOf(currentIV + ivChange, 31) // Green panes
            28,29,30,32,33,34 -> maxOf(currentIV - ivChange, 0)    // Red panes
            else -> currentIV
        }

        // Update checkout map
        checkOutList = checkOutList.toMutableMap().apply { put(stat, newIV - (pokemon.ivs[stat] ?: 0)) }

        // Refresh **all increment panes** to reflect the updated state
        val incrementSlots = mapOf(
            10 to Stats.HP, 11 to Stats.ATTACK, 12 to Stats.SPECIAL_ATTACK,
            14 to Stats.DEFENCE, 15 to Stats.SPECIAL_DEFENCE, 16 to Stats.SPEED,
            28 to Stats.HP, 29 to Stats.ATTACK, 30 to Stats.SPECIAL_ATTACK,
            32 to Stats.DEFENCE, 33 to Stats.SPECIAL_DEFENCE, 34 to Stats.SPEED
        )

        incrementSlots.forEach { (slotIndex, s) ->
            val originalItem = slots[slotIndex].item
            val updatedItem = initialiseIncrements(originalItem, s)
            slots[slotIndex].set(updatedItem)
        }

        // Update the Nether Star slot to reflect checkout
        val netherStarSlot = slots[22]
        val netherStarItem = netherStarSlot.item
        val lore = mutableListOf<Component>()
        checkOutList.forEach { (s, amount) ->
            if (amount != 0) {
                lore.add(Component.literal("${s.name}: $amount IVs")
                    .withStyle(Style.EMPTY.withColor(ColourUtil.essenceCurrencyColor)))
            }
        }
        if (lore.isEmpty()) {
            lore.add(Component.literal("Select IV changes to display here for infusion")
                .withStyle(Style.EMPTY.withColor(ColourUtil.placeholderColor).withItalic(true)))
        }
        netherStarItem.set(DataComponents.LORE, ItemLore(lore))

        // Refresh GUI
        broadcastChanges()
    }




    fun updateInfuseList() {
        //confirm page
    }

    override fun quickMoveStack(player: Player, i: Int): ItemStack = ItemStack.EMPTY
    override fun stillValid(player: Player): Boolean = true
}
