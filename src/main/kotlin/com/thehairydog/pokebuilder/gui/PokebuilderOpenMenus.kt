package com.thehairydog.pokebuilder.gui

import com.cobblemon.mod.common.item.PokemonItem
import com.cobblemon.mod.common.pokemon.Pokemon
import com.thehairydog.pokebuilder.gui.editors.PokeBuilderNatureEditor
import com.thehairydog.pokebuilder.util.ColourUtil
import com.thehairydog.pokebuilder.util.ColourUtil.essenceColor
import com.thehairydog.pokebuilder.util.ColourUtil.pokeColor
import com.thehairydog.pokebuilder.util.ColourUtil.white
import com.thehairydog.pokebuilder.util.PokeItemFormatter
import net.minecraft.network.chat.Component
import net.minecraft.network.chat.Style
import net.minecraft.network.chat.TextColor
import net.minecraft.server.level.ServerPlayer
import net.minecraft.world.MenuProvider
import net.minecraft.world.entity.player.Inventory
import net.minecraft.world.entity.player.Player
import net.minecraft.world.inventory.AbstractContainerMenu
import net.minecraft.world.item.ItemStack

object PokebuilderOpenMenus {

    val pokeBuilderMenuTitle = Component.literal("Poké")
        .withStyle(Style.EMPTY.withColor(pokeColor).withBold(true).withItalic(false))
        .append(
            Component.literal(" Builder")
                .withStyle(Style.EMPTY.withColor(essenceColor).withBold(true).withItalic(false))
        )


    fun openMainPage(player: ServerPlayer) {
        player.openMenu(object : MenuProvider {
            override fun getDisplayName() = pokeBuilderMenuTitle
            override fun createMenu(syncId: Int, inventory: Inventory, playerEntity: Player): AbstractContainerMenu {
                return PokebuilderMainMenu(syncId, player)
            }
        })
    }

    fun openEditPage(player: ServerPlayer, pokemon: Pokemon) {
        player.openMenu(object : MenuProvider {
            val pokeBuilderEditorTitle = Component.literal("Poké")
                .withStyle(Style.EMPTY.withColor(pokeColor).withBold(true).withItalic(false))
                .append(
                    Component.literal(" Builder")
                        .withStyle(Style.EMPTY.withColor(essenceColor).withBold(true).withItalic(false))
                )
            override fun getDisplayName(): Component {
                val pokemonName = Component.literal(pokemon.nickname?.string ?: pokemon.species.name)
                    .withStyle(Style.EMPTY.withColor(TextColor.fromRgb(0xDCDCDC)).withBold(true)) // example golden color

                return pokeBuilderEditorTitle
                    .append(Component.literal(" | ")) // separator in default color
                    .append(pokemonName)
            }

            override fun createMenu(syncId: Int, inventory: Inventory, playerEntity: Player): AbstractContainerMenu {
                return PokebuilderEditMenu(syncId, player, pokemon)
            }
        })
    }

    fun openNaturePage(player: ServerPlayer, pokemon: Pokemon) {
        player.openMenu(object : MenuProvider {

            override fun getDisplayName(): Component {
                // Nature name
                val pokemonName = Component.translatable(pokemon.nature.displayName)
                    .withStyle(Style.EMPTY.withColor(white).withBold(true))

                // Boosted / lowered stats
                val boosted = pokemon.nature.increasedStat
                val lowered = pokemon.nature.decreasedStat

                val natureStats: Component = if (boosted != null && lowered != null) {
                    Component.literal(" (+")
                        .append(Component.literal(PokeItemFormatter.statShortName(boosted)).withStyle(Style.EMPTY.withColor(
                            ColourUtil.boostColor)))
                        .append(Component.literal(" / -"))
                        .append(Component.literal(PokeItemFormatter.statShortName(lowered)).withStyle(Style.EMPTY.withColor(
                            ColourUtil.lowerColor)))
                        .append(Component.literal(")"))
                } else {
                    Component.literal(" (neutral)").withStyle(Style.EMPTY)
                }

                return (pokemonName).append(natureStats) // add boosted/lowered info
            }

            override fun createMenu(syncId: Int, inventory: Inventory, playerEntity: Player): AbstractContainerMenu {
                return PokeBuilderNatureEditor(syncId, player, pokemon)
            }
        })
    }

    fun openIVsPage() {}

    fun openEVsPage() {}

    fun openMovesPage() {}

    fun openLevelsPage() {}

    fun openGenderPage() {}

    fun openShinyPage() {}

    fun openAbilityPage() {}

    fun openConfirmInfuse(
        pokemon: Pokemon,
        player: ServerPlayer,
        cost: Int,
        itemStack: ItemStack,
        onConfirm: (Pokemon, ServerPlayer) -> Unit,
        onCancel: (ServerPlayer) -> Unit
    ) {
        player.openMenu(object : MenuProvider {
            override fun getDisplayName(): Component {
                return Component.literal("Confirm Infusement").withStyle(Style.EMPTY.withBold(true))
            }

            override fun createMenu(
                syncId: Int,
                inventory: Inventory,
                playerEntity: Player
            ): AbstractContainerMenu {
                return PokeBuilderConfirmInfusement(syncId, player, pokemon, cost, itemStack, onConfirm, onCancel)
            }
        })
    }

}
