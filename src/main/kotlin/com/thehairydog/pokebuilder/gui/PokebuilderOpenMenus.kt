package com.thehairydog.pokebuilder.gui

import com.cobblemon.mod.common.item.PokemonItem
import com.cobblemon.mod.common.pokemon.Pokemon
import com.thehairydog.pokebuilder.util.ColourUtil.essenceColor
import com.thehairydog.pokebuilder.util.ColourUtil.pokeColor
import net.minecraft.network.chat.Component
import net.minecraft.network.chat.Style
import net.minecraft.server.level.ServerPlayer
import net.minecraft.world.MenuProvider
import net.minecraft.world.entity.player.Inventory
import net.minecraft.world.entity.player.Player
import net.minecraft.world.inventory.AbstractContainerMenu

object PokebuilderOpenMenus {

    val pokeBuilderTitle = Component.literal("Poké")
        .withStyle(Style.EMPTY.withColor(pokeColor).withBold(true).withItalic(false))
        .append(
            Component.literal(" Builder")
                .withStyle(Style.EMPTY.withColor(essenceColor).withBold(true).withItalic(false))
        )

    fun openMainPage(player: ServerPlayer) {
        player.openMenu(object : MenuProvider {
            override fun getDisplayName() = pokeBuilderTitle
            override fun createMenu(syncId: Int, inventory: Inventory, playerEntity: Player): AbstractContainerMenu {
                return PokebuilderMainMenu(syncId, player)
            }
        })
    }



    fun openEditPage(player: ServerPlayer, pokemon: Pokemon, pokemonItem: PokemonItem) {
        player.openMenu(object : MenuProvider {
            override fun getDisplayName() = pokeBuilderTitle.append(" | Building: ${pokemonItem.getName(PokemonItem.from(pokemon))}")
            override fun createMenu(syncId: Int, inventory: Inventory, playerEntity: Player): AbstractContainerMenu {
                return PokebuilderEditMenu(syncId, player, pokemon)
            }
        })
    }
}
