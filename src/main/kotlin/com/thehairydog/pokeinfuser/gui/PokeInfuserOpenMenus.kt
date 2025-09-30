package com.thehairydog.pokeinfuser.gui

import com.cobblemon.mod.common.pokemon.Pokemon
import com.thehairydog.pokeinfuser.gui.editors.PokeInfuserAbilityEditor
import com.thehairydog.pokeinfuser.gui.editors.PokeInfuserEVsEditor
import com.thehairydog.pokeinfuser.gui.editors.PokeInfuserGenderEditor
import com.thehairydog.pokeinfuser.gui.editors.PokeInfuserIVsEditor
import com.thehairydog.pokeinfuser.gui.editors.PokeInfuserLevelEditor
import com.thehairydog.pokeinfuser.gui.editors.PokeInfuserNatureEditor
import com.thehairydog.pokeinfuser.gui.editors.PokeInfuserShinyEditor
import com.thehairydog.pokeinfuser.util.ColourUtil
import com.thehairydog.pokeinfuser.util.ColourUtil.essenceColor
import com.thehairydog.pokeinfuser.util.ColourUtil.mainInfusionColour
import com.thehairydog.pokeinfuser.util.ColourUtil.pokeColor
import com.thehairydog.pokeinfuser.util.ColourUtil.white
import com.thehairydog.pokeinfuser.util.PokeItemFormatter
import net.minecraft.network.chat.Component
import net.minecraft.network.chat.Style
import net.minecraft.network.chat.TextColor
import net.minecraft.server.level.ServerPlayer
import net.minecraft.world.MenuProvider
import net.minecraft.world.entity.player.Inventory
import net.minecraft.world.entity.player.Player
import net.minecraft.world.inventory.AbstractContainerMenu
import net.minecraft.world.item.ItemStack

object PokeInfuserOpenMenus {

    val pokeBuilderMenuTitle: Component = Component.literal("Poké")
        .withStyle(Style.EMPTY.withColor(white).withBold(true))
        .append(
            Component.literal(" Infuser")
                .withStyle(Style.EMPTY.withColor(essenceColor).withBold(true))
        )

    fun openMainPage(player: ServerPlayer) {
        player.openMenu(object : MenuProvider {
            override fun getDisplayName() = pokeBuilderMenuTitle
            override fun createMenu(syncId: Int, inventory: Inventory, playerEntity: Player): AbstractContainerMenu {
                return PokeInfuserMainMenu(syncId, player)
            }
        })
    }

    fun openEditPage(player: ServerPlayer, pokemon: Pokemon) {
        player.openMenu(object : MenuProvider {
            val title = Component.literal("Poké")
                .withStyle(Style.EMPTY.withColor(white).withBold(true))
                .append(Component.literal(" Infuser")
                    .withStyle(Style.EMPTY.withColor(mainInfusionColour).withBold(true)))

            override fun getDisplayName(): Component {
                val pokemonName = Component.literal(pokemon.nickname?.string ?: pokemon.species.name)
                    .withStyle(Style.EMPTY.withColor(ColourUtil.secondaryInfusionColour).withBold(true))

                return title.append(Component.literal(" | ")).append(pokemonName)
            }

            override fun createMenu(syncId: Int, inventory: Inventory, playerEntity: Player): AbstractContainerMenu {
                return PokeInfuserEditMenu(syncId, player, pokemon)
            }
        })
    }

    fun openNaturePage(player: ServerPlayer, pokemon: Pokemon) {
        player.openMenu(object : MenuProvider {
            override fun getDisplayName(): Component {
                val natureName = Component.translatable(pokemon.nature.displayName)
                    .withStyle(Style.EMPTY.withColor(mainInfusionColour).withBold(true))

                val boosted = pokemon.nature.increasedStat
                val lowered = pokemon.nature.decreasedStat

                val natureStats: Component = if (boosted != null && lowered != null) {
                    Component.literal(" (+")
                        .append(Component.literal(PokeItemFormatter.statShortName(boosted))
                            .withStyle(Style.EMPTY.withColor(ColourUtil.boostColor)))
                        .append(Component.literal(" / -"))
                        .append(Component.literal(PokeItemFormatter.statShortName(lowered))
                            .withStyle(Style.EMPTY.withColor(ColourUtil.lowerColor)))
                        .append(Component.literal(")"))
                } else {
                    Component.literal(" (neutral)")
                        .withStyle(Style.EMPTY.withColor(white))
                }

                return natureName.append(natureStats)
            }

            override fun createMenu(syncId: Int, inventory: Inventory, playerEntity: Player): AbstractContainerMenu {
                return PokeInfuserNatureEditor(syncId, player, pokemon)
            }
        })
    }

    fun openIVsPage(player: ServerPlayer, pokemon: Pokemon) {
        player.openMenu(object : MenuProvider {
            override fun getDisplayName(): Component {
                return Component.literal("Poké ")
                    .withStyle(Style.EMPTY.withColor(white).withBold(true))
                    .append(
                        Component.literal("Infuser")
                            .withStyle(Style.EMPTY.withColor(mainInfusionColour).withBold(true))
                    )
                    .append(
                        Component.literal(" | ")
                            .withStyle(Style.EMPTY.withColor(white).withBold(true))
                    )
                    .append(
                        Component.literal("IVs")
                            .withStyle(Style.EMPTY.withColor(ColourUtil.secondaryInfusionColour).withBold(true))
                    )
            }


            override fun createMenu(syncId: Int, inventory: Inventory, playerEntity: Player): AbstractContainerMenu {
                return PokeInfuserIVsEditor(syncId, player, pokemon)
            }
        })
    }

    fun openEVsPage(player: ServerPlayer, pokemon: Pokemon) {
        player.openMenu(object : MenuProvider {
            override fun getDisplayName(): Component {
                return Component.literal("Poké")
                    .withStyle(Style.EMPTY.withColor(white).withBold(true))
                    .append(
                        Component.literal("Infuser")
                            .withStyle(Style.EMPTY.withColor(mainInfusionColour).withBold(true))
                    )
                    .append(
                        Component.literal(" | ")
                            .withStyle(Style.EMPTY.withColor(white).withBold(true))
                    )
                    .append(
                        Component.literal("EVs")
                            .withStyle(Style.EMPTY.withColor(ColourUtil.secondaryInfusionColour).withBold(true))
                    )
            }


            override fun createMenu(syncId: Int, inventory: Inventory, playerEntity: Player): AbstractContainerMenu {
                return PokeInfuserEVsEditor(syncId, player, pokemon)
            }
        })
    }

    fun openMovesPage() {}

    fun openLevelsPage(player: ServerPlayer, pokemon: Pokemon) {
        player.openMenu(object : MenuProvider {
            override fun getDisplayName(): Component {
                return Component.literal("Poké")
                    .withStyle(Style.EMPTY.withColor(white).withBold(true))
                    .append(
                        Component.literal("Infuser")
                            .withStyle(Style.EMPTY.withColor(mainInfusionColour).withBold(true))
                    )
                    .append(
                        Component.literal(" | ")
                            .withStyle(Style.EMPTY.withColor(white).withBold(true))
                    )
                    .append(
                        Component.literal("Levels")
                            .withStyle(Style.EMPTY.withColor(ColourUtil.secondaryInfusionColour).withBold(true))
                    )
            }

            override fun createMenu(syncId: Int, inventory: Inventory, playerEntity: Player): AbstractContainerMenu {
                return PokeInfuserLevelEditor(syncId, player, pokemon)
            }
        })
    }

    fun openGenderPage(player: ServerPlayer, pokemon: Pokemon) {
        player.openMenu(object : MenuProvider {
            override fun getDisplayName(): Component {
                return Component.literal("Poké")
                    .withStyle(Style.EMPTY.withColor(white).withBold(true))
                    .append(
                        Component.literal("Infuser")
                            .withStyle(Style.EMPTY.withColor(mainInfusionColour).withBold(true))
                    )
                    .append(
                        Component.literal(" | ")
                            .withStyle(Style.EMPTY.withColor(white).withBold(true))
                    )
                    .append(
                        Component.literal("Gender")
                            .withStyle(Style.EMPTY.withColor(ColourUtil.secondaryInfusionColour).withBold(true))
                    )
            }

            override fun createMenu(syncId: Int, inventory: Inventory, playerEntity: Player): AbstractContainerMenu {
                return PokeInfuserGenderEditor(syncId, player, pokemon)
            }
        })
    }

    fun openShinyPage(player: ServerPlayer, pokemon: Pokemon) {
        player.openMenu(object : MenuProvider {
            override fun getDisplayName(): Component {
                return Component.literal("Poké")
                    .withStyle(Style.EMPTY.withColor(white).withBold(true))
                    .append(
                        Component.literal("Infuser")
                            .withStyle(Style.EMPTY.withColor(mainInfusionColour).withBold(true))
                    )
                    .append(
                        Component.literal(" | ")
                            .withStyle(Style.EMPTY.withColor(white).withBold(true))
                    )
                    .append(
                        Component.literal("Shiny")
                            .withStyle(Style.EMPTY.withColor(ColourUtil.gold).withBold(true))
                    )
            }

            override fun createMenu(syncId: Int, inventory: Inventory, playerEntity: Player): AbstractContainerMenu {
                return PokeInfuserShinyEditor(syncId, player, pokemon)
            }
        })
    }

    fun openAbilityPage(player: ServerPlayer, pokemon: Pokemon) {
        player.openMenu(object : MenuProvider {
            override fun getDisplayName(): Component {
                return Component.literal("Poké")
                    .withStyle(Style.EMPTY.withColor(white).withBold(true))
                    .append(
                        Component.literal("Infuser")
                            .withStyle(Style.EMPTY.withColor(mainInfusionColour).withBold(true))
                    )
                    .append(
                        Component.literal(" | ")
                            .withStyle(Style.EMPTY.withColor(white).withBold(true))
                    )
                    .append(
                        Component.literal("Abilities")
                            .withStyle(Style.EMPTY.withColor(ColourUtil.pokeColor).withBold(true))
                    )
            }

            override fun createMenu(syncId: Int, inventory: Inventory, playerEntity: Player): AbstractContainerMenu {
                return PokeInfuserAbilityEditor(syncId, player, pokemon)
            }
        })
    }

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
                return Component.literal("Confirm Infusion")
                    .withStyle(Style.EMPTY.withColor(mainInfusionColour).withBold(true))
            }

            override fun createMenu(syncId: Int, inventory: Inventory, playerEntity: Player): AbstractContainerMenu {
                return PokeInfuserConfirmInfusion(syncId, player, pokemon, cost, itemStack, onConfirm, onCancel)
            }
        })
    }
}
