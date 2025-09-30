package com.thehairydog.pokeinfuser.gui.editors

import com.cobblemon.mod.common.CobblemonItems
import com.cobblemon.mod.common.pokemon.Gender
import com.cobblemon.mod.common.pokemon.Pokemon
import com.thehairydog.pokeinfuser.gui.PokeInfuserOpenMenus
import com.thehairydog.pokeinfuser.gui.slotUtil.LockedSlot
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
import net.minecraft.world.item.component.ItemLore
import net.minecraft.world.item.Items

class PokeInfuserGenderEditor(
    syncId: Int,
    private val player: ServerPlayer,
    private val pokemon: Pokemon
) : AbstractContainerMenu(MenuType.GENERIC_9x5, syncId) {

    private val container = SimpleContainer(45)

    init {
        for (i in 0 until 45) {
            val slotX = 8 + (i % 9) * 18
            val slotY = 18 + (i / 9) * 18

            val itemStack = when (i) {
                19 -> initialiseGenderItem(Gender.MALE)
                21 -> initialiseGenderItem(Gender.FEMALE)
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
            19 -> openConfirmGender(Gender.MALE)
            21 -> openConfirmGender(Gender.FEMALE)
            40 -> PokeInfuserOpenMenus.openEditPage(player, pokemon)
        }
    }

    private fun initialiseGenderItem(gender: Gender): ItemStack {
        val item = ItemStack(
            when (gender) {
                Gender.MALE -> CobblemonItems.BLUE_APRICORN_SEED
                Gender.FEMALE -> CobblemonItems.PINK_APRICORN_SEED
                else -> Items.BARRIER
            }
        )

        item.set(
            DataComponents.CUSTOM_NAME,
            Component.literal(gender.name.replaceFirstChar { it.uppercase() }).withStyle(
                Style.EMPTY.withBold(true)
            )
        )

        item.set(
            DataComponents.LORE,
            ItemLore(
                listOf(
                    Component.literal("Select to change ${pokemon.nickname ?: pokemon.species.name}'s gender to ${gender.name.lowercase()}")
                )
            )
        )

        return item
    }

    private fun openConfirmGender(selectedGender: Gender) {
        val previewItem = initialiseGenderItem(selectedGender)
        PokeInfuserOpenMenus.openConfirmInfuse(
            pokemon,
            player,
            30, // no essence cost for gender change
            previewItem,
            onConfirm = { pkmn, _ -> pkmn.gender = selectedGender },
            onCancel = { _ -> PokeInfuserOpenMenus.openGenderPage(player, pokemon) }
        )
    }

    override fun quickMoveStack(player: Player, i: Int) = ItemStack.EMPTY
    override fun stillValid(player: Player) = true
}
