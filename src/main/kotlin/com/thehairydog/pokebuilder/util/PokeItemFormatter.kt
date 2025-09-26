package com.thehairydog.pokebuilder.util

import com.cobblemon.mod.common.api.pokemon.stats.Stat
import com.cobblemon.mod.common.api.pokemon.stats.Stats
import com.cobblemon.mod.common.item.CobblemonItem
import com.cobblemon.mod.common.item.PokemonItem
import com.cobblemon.mod.common.pokemon.Gender
import com.cobblemon.mod.common.pokemon.Pokemon
import com.thehairydog.pokebuilder.pokeessence.PokeEssenceHandler
import com.thehairydog.pokebuilder.util.ColourUtil.boostColor
import com.thehairydog.pokebuilder.util.ColourUtil.essenceColor
import com.thehairydog.pokebuilder.util.ColourUtil.femaleColor
import com.thehairydog.pokebuilder.util.ColourUtil.genderlessColor
import com.thehairydog.pokebuilder.util.ColourUtil.lowerColor
import com.thehairydog.pokebuilder.util.ColourUtil.maleColor
import com.thehairydog.pokebuilder.util.ColourUtil.pokeColor
import com.thehairydog.pokebuilder.util.ColourUtil.typeColors
import com.thehairydog.pokebuilder.util.ColourUtil.white
import net.minecraft.core.component.DataComponents
import net.minecraft.network.chat.Component
import net.minecraft.network.chat.Style
import net.minecraft.network.chat.TextColor
import net.minecraft.server.level.ServerPlayer
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.component.ItemLore

object PokeItemFormatter {

    fun statShortName(stat: Stat?): String = when(stat) {
        Stats.HP -> "HP"
        Stats.ATTACK -> "Atk"
        Stats.DEFENCE -> "Def"
        Stats.SPECIAL_ATTACK -> "Sp. A"
        Stats.SPECIAL_DEFENCE -> "Sp. D"
        Stats.SPEED -> "Spd"
        else -> ""
    }

    fun configurePokemonForPokebuilder(pokemon: Pokemon): ItemStack {
        val pokeItem: ItemStack = PokemonItem.from(pokemon)

        // Display name bold
        val displayName = (pokemon.nickname?.string ?: pokemon.species.name).let {
            Component.literal(it).withStyle(Style.EMPTY.withBold(true).withColor(white).withItalic(false))
        }
        pokeItem.set(DataComponents.CUSTOM_NAME, displayName)

        // Boosted / lowered stats
        val boosted = pokemon.nature.increasedStat
        val lowered = pokemon.nature.decreasedStat

        fun statColor(stat: Stats): TextColor = when(stat) {
            boosted -> boostColor
            lowered -> lowerColor
            else -> white
        }

        // Gender color
        val genderColor = when(pokemon.gender) {
            Gender.MALE -> maleColor
            Gender.FEMALE -> femaleColor
            else -> genderlessColor
        }

        // Nature text (+/- or neutral)
        val natureText: Component = if (boosted != null && lowered != null) {
            Component.literal(" (")
                .append(Component.literal("+${statShortName(boosted)}")
                    .withStyle(Style.EMPTY.withColor(boostColor).withItalic(false)))
                .append(Component.literal(", "))
                .append(Component.literal("-${statShortName(lowered)}")
                    .withStyle(Style.EMPTY.withColor(lowerColor).withItalic(false)))
                .append(Component.literal(")"))
        } else {
            Component.literal(" (neutral)").withStyle(Style.EMPTY.withItalic(false))
        }

        // Types display (manual append)
        val typesList = pokemon.types.toList()  // convert iterable to a List
        var typesDisplay = Component.empty()

        typesList.forEachIndexed { index, type ->
            typesDisplay = typesDisplay.append(
                Component.literal(type.name)
                    .withStyle(Style.EMPTY.withColor(typeColors[type.name] ?: white).withItalic(false))
            )

            if (index < typesList.size - 1) {  // now safe to use .size
                typesDisplay = typesDisplay.append(
                    Component.literal(" / ").withStyle(Style.EMPTY.withColor(white).withItalic(false))
                )
            }
        }

        // Full lore
        val pokeStatsLore: List<Component> = listOf(
            Component.literal("Gender: ").withStyle(Style.EMPTY.withColor(white).withItalic(false))
                .append(Component.literal(pokemon.gender.name)
                    .withStyle(Style.EMPTY.withColor(genderColor).withItalic(false))),
            Component.literal("Level: ${pokemon.level}").withStyle(Style.EMPTY.withColor(white).withItalic(false)),
            Component.literal("Nature: ").withStyle(Style.EMPTY.withColor(white).withItalic(false))
                .append(Component.translatable(pokemon.nature.displayName).withStyle(Style.EMPTY.withColor(white).withItalic(false)))
                .append(natureText),
            Component.literal("Ability: ").withStyle(Style.EMPTY.withColor(white).withItalic(false))
                .append(Component.translatable(pokemon.ability.displayName).withStyle(Style.EMPTY.withColor(white).withItalic(false))),
            Component.literal("Type(s): ").withStyle(Style.EMPTY.withColor(white).withItalic(false))
                .append(typesDisplay),
            Component.literal("Stats     | IVs | EVs").withStyle(Style.EMPTY.withColor(white).withItalic(false).withUnderlined(true)),
            Component.literal("HP        | ${pokemon.ivs[Stats.HP]} | ${pokemon.evs[Stats.HP]}")
                .withStyle(Style.EMPTY.withColor(statColor(Stats.HP)).withItalic(false)),
            Component.literal("Atk       | ${pokemon.ivs[Stats.ATTACK]} | ${pokemon.evs[Stats.ATTACK]}")
                .withStyle(Style.EMPTY.withColor(statColor(Stats.ATTACK)).withItalic(false)),
            Component.literal("Def       | ${pokemon.ivs[Stats.DEFENCE]} | ${pokemon.evs[Stats.DEFENCE]}")
                .withStyle(Style.EMPTY.withColor(statColor(Stats.DEFENCE)).withItalic(false)),
            Component.literal("Sp. A     | ${pokemon.ivs[Stats.SPECIAL_ATTACK]} | ${pokemon.evs[Stats.SPECIAL_ATTACK]}")
                .withStyle(Style.EMPTY.withColor(statColor(Stats.SPECIAL_ATTACK)).withItalic(false)),
            Component.literal("Sp. D     | ${pokemon.ivs[Stats.SPECIAL_DEFENCE]} | ${pokemon.evs[Stats.SPECIAL_DEFENCE]}")
                .withStyle(Style.EMPTY.withColor(statColor(Stats.SPECIAL_DEFENCE)).withItalic(false)),
            Component.literal("Spd       | ${pokemon.ivs[Stats.SPEED]} | ${pokemon.evs[Stats.SPEED]}")
                .withStyle(Style.EMPTY.withColor(statColor(Stats.SPEED)).withItalic(false))
        )

        pokeItem.set(DataComponents.LORE, ItemLore(pokeStatsLore))
        return pokeItem
    }

    fun configurePokeEssence(item: CobblemonItem, player : ServerPlayer): ItemStack {
        val stack = ItemStack(item)

        // build the display name: "Poké" (red, bold) + "Essence" (white-ish, bold)
        val displayName = Component.literal("Poké")
            .withStyle(Style.EMPTY.withColor(pokeColor).withItalic(false))
            .append(
                Component.literal("Essence")
                    .withStyle(Style.EMPTY.withColor(essenceColor).withItalic(false))
            )

        // apply name to the ItemStack
        stack.set(DataComponents.CUSTOM_NAME, displayName)

        // example balance lore line (make sure to append a Component, not a raw String)
        val pokeEssenceBalance = PokeEssenceHandler.get(player)
        val pokeEssenceLore: List<Component> = listOf(
            Component.literal("Balance: ").withStyle(Style.EMPTY.withColor(essenceColor).withItalic(false))
                .append(Component.literal(pokeEssenceBalance.toString()).withStyle(Style.EMPTY.withColor(essenceColor).withItalic(false)))
        )

        stack.set(DataComponents.LORE, ItemLore(pokeEssenceLore))

        return stack
    }

}