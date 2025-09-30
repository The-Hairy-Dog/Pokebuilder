package com.thehairydog.pokeinfuser.gui.editors

import ClickableSlot
import com.cobblemon.mod.common.CobblemonItems
import com.cobblemon.mod.common.api.pokemon.Natures
import com.cobblemon.mod.common.pokemon.Pokemon
import com.thehairydog.pokeinfuser.gui.PokeInfuserOpenMenus
import com.thehairydog.pokeinfuser.gui.slotUtil.LockedSlot
import com.thehairydog.pokeinfuser.util.PokeItemFormatter
import com.thehairydog.pokeinfuser.util.SoundUtil
import net.minecraft.network.chat.Component
import net.minecraft.server.level.ServerPlayer
import net.minecraft.world.SimpleContainer
import net.minecraft.world.entity.player.Player
import net.minecraft.world.inventory.AbstractContainerMenu
import net.minecraft.world.inventory.ClickType
import net.minecraft.world.inventory.MenuType
import net.minecraft.world.item.ItemStack

class PokeInfuserNatureEditor(
    syncId: Int,
    private val player: ServerPlayer,
    private val pokemon: Pokemon
) : AbstractContainerMenu(MenuType.GENERIC_9x5, syncId) {

    private val container = SimpleContainer(45)

    // Map of natures to their mint items
    private val natureItems = mapOf(
        // Neutral natures
        Natures.BASHFUL to CobblemonItems.SERIOUS_MINT,
        Natures.DOCILE to CobblemonItems.SERIOUS_MINT,
        Natures.HARDY to CobblemonItems.SERIOUS_MINT,
        Natures.QUIRKY to CobblemonItems.SERIOUS_MINT,
        Natures.SERIOUS to CobblemonItems.SERIOUS_MINT,
        // Attack+
        Natures.ADAMANT to CobblemonItems.ADAMANT_MINT,
        Natures.BRAVE to CobblemonItems.BRAVE_MINT,
        Natures.LONELY to CobblemonItems.LONELY_MINT,
        Natures.NAUGHTY to CobblemonItems.NAUGHTY_MINT,
        // Defense+
        Natures.BOLD to CobblemonItems.BOLD_MINT,
        Natures.IMPISH to CobblemonItems.IMPISH_MINT,
        Natures.LAX to CobblemonItems.LAX_MINT,
        Natures.RELAXED to CobblemonItems.RELAXED_MINT,
        // Sp. Atk+
        Natures.MODEST to CobblemonItems.MODEST_MINT,
        Natures.MILD to CobblemonItems.MILD_MINT,
        Natures.QUIET to CobblemonItems.QUIET_MINT,
        Natures.RASH to CobblemonItems.RASH_MINT,
        // Sp. Def+
        Natures.CALM to CobblemonItems.CALM_MINT,
        Natures.CAREFUL to CobblemonItems.CAREFUL_MINT,
        Natures.GENTLE to CobblemonItems.GENTLE_MINT,
        Natures.SASSY to CobblemonItems.SASSY_MINT,
        // Speed+
        Natures.TIMID to CobblemonItems.TIMID_MINT,
        Natures.HASTY to CobblemonItems.HASTY_MINT,
        Natures.JOLLY to CobblemonItems.JOLLY_MINT,
        Natures.NAIVE to CobblemonItems.NAIVE_MINT
    )

    // Predefined layout for 9x5 chest
    private val natureLayout = listOf(
        listOf(Natures.ADAMANT, Natures.BOLD, Natures.MODEST, Natures.CALM, Natures.TIMID),
        listOf(Natures.BRAVE, Natures.IMPISH, Natures.MILD, Natures.CAREFUL, Natures.HASTY),
        listOf(Natures.LONELY, Natures.LAX, Natures.QUIET, Natures.GENTLE, Natures.JOLLY),
        listOf(Natures.NAUGHTY, Natures.RELAXED, Natures.RASH, Natures.SASSY, Natures.NAIVE),
        listOf(Natures.BASHFUL, Natures.DOCILE, Natures.HARDY, Natures.QUIRKY, Natures.SERIOUS) // Neutral
    )

    init {
        // Map of slots to their nature
        val slotToNature = mapOf(
            // Attack+
            9 to Natures.ADAMANT,
            10 to Natures.BRAVE,
            11 to Natures.LONELY,
            12 to Natures.NAUGHTY,
            // Sp. Attack+
            18 to Natures.MODEST,
            19 to Natures.MILD,
            20 to Natures.QUIET,
            21 to Natures.RASH,
            // Defense+
            27 to Natures.BOLD,
            28 to Natures.IMPISH,
            29 to Natures.LAX,
            30 to Natures.RELAXED,
            // Sp. Defense+
            14 to Natures.CALM,
            15 to Natures.CAREFUL,
            16 to Natures.GENTLE,
            17 to Natures.SASSY,
            // Speed+
            23 to Natures.TIMID,
            24 to Natures.HASTY,
            25 to Natures.JOLLY,
            26 to Natures.NAIVE,
            // Neutral
            22 to Natures.BASHFUL,
            32 to Natures.DOCILE,
            33 to Natures.HARDY,
            34 to Natures.QUIRKY,
            35 to Natures.SERIOUS
        )

        // Add nature slots
        var internalIndex = 0
        for ((slotIndex, nature) in slotToNature) {
            val slotX = 8 + (slotIndex % 9) * 18
            val slotY = 18 + (slotIndex / 9) * 18
            val itemStack = PokeItemFormatter.configureNatureItem(natureItems[nature]!!, nature)

            val slot = ClickableSlot(container, internalIndex, slotX, slotY) {
                SoundUtil.playClickSound(player)
                PokeInfuserOpenMenus.openConfirmInfuse(
                    pokemon, player, 20, itemStack,
                    onConfirm = { poke, ply ->
                        poke.nature = nature
                        ply.sendSystemMessage(
                            Component.literal(
                                "${poke.nickname?.string ?: poke.species.name} is now ${
                                    nature.name.toString().replaceFirstChar { it.uppercase() }
                                }!"
                            )
                        )
                    },
                    onCancel = { PokeInfuserOpenMenus.openNaturePage(player, pokemon) }
                )
            }

            container.setItem(slotIndex, itemStack) // slotIndex determines GUI position
            addSlot(slot)
            internalIndex++ // increment internal index
        }


        val backSlotIndex = 40
        val backX = 8 + (backSlotIndex % 9) * 18
        val backY = 18 + (backSlotIndex / 9) * 18
        val backItem = PokeItemFormatter.configureBackItem()
        val backSlot = ClickableSlot(container, internalIndex, backX, backY) {
            SoundUtil.playBackSound(player)
            PokeInfuserOpenMenus.openEditPage(player, pokemon)
        }
        container.setItem(backSlotIndex, backItem)
        addSlot(backSlot)

        // Lock player main inventory (3 rows of 9)
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

    override fun quickMoveStack(player: Player, index: Int): ItemStack = ItemStack.EMPTY
    override fun stillValid(player: Player): Boolean = true
    override fun clicked(slotId: Int, dragType: Int, clickType: ClickType, player: Player) {
        if (slotId in slots.indices) {
            val slot = slots[slotId]
            if (slot.container === player.inventory) {
                return  // block all clicks in player inventory
            }
        }

        super.clicked(slotId, dragType, clickType, player)
    }

}
