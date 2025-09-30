package com.thehairydog.pokeinfuser.util

import com.cobblemon.mod.common.CobblemonItems
import com.cobblemon.mod.common.api.moves.MoveSet
import com.cobblemon.mod.common.api.pokemon.moves.Learnset
import com.cobblemon.mod.common.api.pokemon.stats.Stat
import com.cobblemon.mod.common.api.pokemon.stats.Stats
import com.cobblemon.mod.common.item.CobblemonItem
import com.cobblemon.mod.common.item.PokemonItem
import com.cobblemon.mod.common.item.interactive.MintItem
import com.cobblemon.mod.common.pokemon.Gender
import com.cobblemon.mod.common.pokemon.Nature
import com.cobblemon.mod.common.pokemon.Pokemon
import com.thehairydog.pokeinfuser.pokeessence.PokeEssenceHandler
import com.thehairydog.pokeinfuser.util.ColourUtil.boostColor
import com.thehairydog.pokeinfuser.util.ColourUtil.essenceColor
import com.thehairydog.pokeinfuser.util.ColourUtil.femaleColor
import com.thehairydog.pokeinfuser.util.ColourUtil.genderlessColor
import com.thehairydog.pokeinfuser.util.ColourUtil.lowerColor
import com.thehairydog.pokeinfuser.util.ColourUtil.maleColor
import com.thehairydog.pokeinfuser.util.ColourUtil.pokeColor
import com.thehairydog.pokeinfuser.util.ColourUtil.typeColors
import com.thehairydog.pokeinfuser.util.ColourUtil.white
import net.minecraft.core.component.DataComponents
import net.minecraft.network.chat.Component
import net.minecraft.network.chat.Style
import net.minecraft.network.chat.TextColor
import net.minecraft.server.level.ServerPlayer
import net.minecraft.util.Unit
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.Items
import net.minecraft.world.item.component.ItemLore
import net.minecraft.world.level.block.Blocks
import kotlin.math.ceil

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
            Component.literal("Click to edit: ").withStyle(Style.EMPTY.withBold(true).withColor(TextColor.fromRgb(0xC7383F)).withItalic(false))
                .append(it).withStyle(Style.EMPTY.withColor(white).withBold(true).withItalic(false))
        }
        pokeItem.set(DataComponents.CUSTOM_NAME, displayName)

        // Boosted / lowered stats
        val boosted = pokemon.nature.increasedStat
        val lowered = pokemon.nature.decreasedStat

        fun statColor(stat: Stats, pokemon: Pokemon): TextColor {
            return when {
                stat == boosted -> boostColor
                stat == lowered -> lowerColor
                pokemon.ivs[stat] == 31 -> TextColor.fromRgb(0xC6A939) // golden
                else -> TextColor.fromRgb(0xFFFFFF) // neutral white or any fallback
            }
        }


        // Gender color
        val genderColor = when(pokemon.gender) {
            Gender.MALE -> maleColor
            Gender.FEMALE -> femaleColor
            Gender.GENDERLESS -> genderlessColor
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
                .withStyle(Style.EMPTY.withColor(statColor(Stats.HP, pokemon)).withItalic(false)),
            Component.literal("Atk       | ${pokemon.ivs[Stats.ATTACK]} | ${pokemon.evs[Stats.ATTACK]}")
                .withStyle(Style.EMPTY.withColor(statColor(Stats.ATTACK, pokemon)).withItalic(false)),
            Component.literal("Def       | ${pokemon.ivs[Stats.DEFENCE]} | ${pokemon.evs[Stats.DEFENCE]}")
                .withStyle(Style.EMPTY.withColor(statColor(Stats.DEFENCE, pokemon)).withItalic(false)),
            Component.literal("Sp. A     | ${pokemon.ivs[Stats.SPECIAL_ATTACK]} | ${pokemon.evs[Stats.SPECIAL_ATTACK]}")
                .withStyle(Style.EMPTY.withColor(statColor(Stats.SPECIAL_ATTACK, pokemon)).withItalic(false)),
            Component.literal("Sp. D     | ${pokemon.ivs[Stats.SPECIAL_DEFENCE]} | ${pokemon.evs[Stats.SPECIAL_DEFENCE]}")
                .withStyle(Style.EMPTY.withColor(statColor(Stats.SPECIAL_DEFENCE, pokemon)).withItalic(false)),
            Component.literal("Spd       | ${pokemon.ivs[Stats.SPEED]} | ${pokemon.evs[Stats.SPEED]}")
                .withStyle(Style.EMPTY.withColor(statColor(Stats.SPEED, pokemon)).withItalic(false))
        )
        pokeItem.set(DataComponents.LORE, ItemLore(pokeStatsLore))
        return pokeItem
    }

    fun configurePokeEssence(item: CobblemonItem, player : ServerPlayer): ItemStack {
        val stack = ItemStack(item)

        // build the display name: "Poké" (red, bold) + "Essence" (white-ish, bold)
        val displayName = Component.literal("Poké")
            .withStyle(Style.EMPTY.withColor(white).withItalic(false).withBold(true))
            .append(
                Component.literal("Essence")
                    .withStyle(Style.EMPTY.withColor(essenceColor).withItalic(false).withBold(true))
            )

        // apply name to the ItemStack
        stack.set(DataComponents.CUSTOM_NAME, displayName)

        // example balance lore line (make sure to append a Component, not a raw String)
        val pokeEssenceBalance = PokeEssenceHandler.get(player)
        val pokeEssenceLore: List<Component> = listOf(
            Component.literal(""),
            Component.literal("Bal: ✦").withStyle(Style.EMPTY.withColor(essenceColor).withItalic(false))
                .append(Component.literal(pokeEssenceBalance.toString()).withStyle(Style.EMPTY.withColor(essenceColor).withItalic(false)))
        )

        stack.set(DataComponents.LORE, ItemLore(pokeEssenceLore))
        return stack
    }

    fun configurePokemonItemForEditor(itemStack: ItemStack, pokemon: Pokemon): ItemStack {
        val displayName = Component.literal(pokemon.nickname?.string ?: pokemon.species.name).withStyle(Style.EMPTY.withColor(white).withItalic(false).withBold(true))
        itemStack.set(DataComponents.CUSTOM_NAME, displayName)
        return itemStack
    }

    fun configureIVsEditor(pokemon : Pokemon) : ItemStack {

        val hpIV = pokemon.ivs[Stats.HP] ?: 0
        val atkIV = pokemon.ivs[Stats.ATTACK] ?: 0
        val spatkIV = pokemon.ivs[Stats.SPECIAL_ATTACK] ?: 0
        val defIV = pokemon.ivs[Stats.DEFENCE] ?: 0
        val spdefIV = pokemon.ivs[Stats.SPECIAL_DEFENCE] ?: 0
        val spdIV = pokemon.ivs[Stats.SPEED] ?: 0

        val percentageIVTotal = ceil(((hpIV + atkIV + spatkIV + defIV + spdefIV + spdIV).toDouble() / (31.0 * 6)) * 100).toInt()

        val completeIVColour = if (percentageIVTotal == 100) {
            TextColor.fromRgb(0xC6A939)
        } else { white }

        val enchantmentBook = ItemStack(Items.ENCHANTED_BOOK)
        enchantmentBook.set(
            DataComponents.CUSTOM_NAME,
            Component.literal("IVs")
                .withStyle(Style.EMPTY.withItalic(false))
                .append(" | $percentageIVTotal%")
                .withStyle(Style.EMPTY.withItalic(false).withColor(completeIVColour))
        )

        fun ivComponent(value: Int): Component {
            val color = if (value == 31) TextColor.fromRgb(0xC6A939) else TextColor.fromRgb(0xFFFFFF)
            return Component.literal("$value")
                .withStyle(Style.EMPTY.withItalic(false).withColor(color))
        }

        val ivLoreList: List<Component> = listOf(
            Component.literal("HP | ATK | SP A | DEF | SP D | SPD")
                .withStyle(Style.EMPTY.withItalic(false).withColor(TextColor.fromRgb(0xFFFFFF))),

            Component.literal(" ")
                .append(ivComponent(hpIV)).append("    ")
                .append(ivComponent(atkIV)).append("    ")
                .append(ivComponent(spatkIV)).append("     ")
                .append(ivComponent(defIV)).append("     ")
                .append(ivComponent(spdefIV)).append("   ")
                .append(ivComponent(spdIV)),

            Component.literal(""),
            Component.literal("Click to edit").withStyle(Style.EMPTY.withColor(white).withItalic(true))
        )


        enchantmentBook.set(DataComponents.LORE, ItemLore(ivLoreList))
        return enchantmentBook
    }

    fun configureGenderEditor(pokemon: Pokemon) : ItemStack {

        val genderColor = when (pokemon.gender) {
            Gender.MALE -> TextColor.fromRgb(0x4A90E2)
            Gender.FEMALE -> TextColor.fromRgb(0xFF69B4)
            Gender.GENDERLESS -> TextColor.fromRgb(0xD3D3D3)
        }

        val itemStack = when (pokemon.gender) {
            Gender.MALE -> ItemStack(CobblemonItems.BLUE_APRICORN_SEED)
            Gender.FEMALE -> ItemStack(CobblemonItems.PINK_APRICORN_SEED)
            Gender.GENDERLESS -> ItemStack(CobblemonItems.WHITE_APRICORN_SEED)
        }

        itemStack.set(
            DataComponents.CUSTOM_NAME,
            Component.literal("Gender: ").withStyle(Style.EMPTY.withItalic(false))
                .append(
                    Component.literal(pokemon.gender.name)
                        .withStyle(Style.EMPTY.withItalic(false).withColor(genderColor))
                )
        )

        val genderItemLore: List<Component> = listOf(
            Component.literal(""),
            Component.literal("Click to edit").withStyle(Style.EMPTY.withColor(white).withItalic(true))
        )

        itemStack.set(DataComponents.LORE, ItemLore(genderItemLore))
        return itemStack
    }

    fun configureNatureEditor(pokemon: Pokemon): ItemStack {
        val itemStack = ItemStack(CobblemonItems.GREEN_MINT_LEAF)

        // Get boosted / lowered stats
        val boosted = pokemon.nature.increasedStat
        val lowered = pokemon.nature.decreasedStat

        // Construct the stat part
        val natureStats: Component = if (boosted != null && lowered != null) {
            Component.literal(" (+")
                .append(Component.literal(statShortName(boosted)).withStyle(Style.EMPTY.withColor(boostColor).withItalic(false)))
                .append(Component.literal(" / -"))
                .append(Component.literal(statShortName(lowered)).withStyle(Style.EMPTY.withColor(lowerColor).withItalic(false)))
                .append(Component.literal(")"))
        } else {
            Component.literal(" (neutral)").withStyle(Style.EMPTY.withItalic(false))
        }

        // Full display name: "Nature: [nature] (+Stat / -Stat)"
        val displayName = Component.literal("Nature: ")
            .withStyle(Style.EMPTY.withColor(white).withItalic(false))
            .append(Component.translatable(pokemon.nature.displayName).withStyle(Style.EMPTY.withColor(white).withItalic(false)))
            .append(natureStats)

        itemStack.set(DataComponents.CUSTOM_NAME, displayName)

        // Optional: add lore
        val lore: List<Component> = listOf(
            Component.literal(""),
            Component.literal("Click to edit").withStyle(Style.EMPTY.withColor(white).withItalic(true))
        )
        itemStack.set(DataComponents.LORE, ItemLore(lore))
        itemStack.set(DataComponents.HIDE_ADDITIONAL_TOOLTIP, Unit.INSTANCE)
        return itemStack
    }

    fun configureEVsEditor(pokemon: Pokemon): ItemStack {
        // Get each EV
        val hpEV = pokemon.evs[Stats.HP] ?: 0
        val atkEV = pokemon.evs[Stats.ATTACK] ?: 0
        val spatkEV = pokemon.evs[Stats.SPECIAL_ATTACK] ?: 0
        val defEV = pokemon.evs[Stats.DEFENCE] ?: 0
        val spdefEV = pokemon.evs[Stats.SPECIAL_DEFENCE] ?: 0
        val spdEV = pokemon.evs[Stats.SPEED] ?: 0

        val totalEV = hpEV + atkEV + spatkEV + defEV + spdefEV + spdEV

        // Determine color: gold if fully trained, else white
        val statusColor = if (totalEV == 510) TextColor.fromRgb(0xC6A939) else white
        val statusText = if (totalEV == 510) "Battle-Ready" else "$totalEV / 510"

        // Base item: enchanted book
        val evBook = ItemStack(CobblemonItems.CARBOS)

        // Custom name: EVs | [status]
        val displayName = Component.literal("EVs")
            .withStyle(Style.EMPTY.withItalic(false))
            .append(" | $statusText")
            .withStyle(Style.EMPTY.withItalic(false).withColor(statusColor))
        evBook.set(DataComponents.CUSTOM_NAME, displayName)

        // Helper for each EV component
        fun evComponent(value: Int): Component {
            val color = if (value == 252) TextColor.fromRgb(0xC6A939) else TextColor.fromRgb(0xFFFFFF)
            return Component.literal("$value").withStyle(Style.EMPTY.withItalic(false).withColor(color))
        }

        // Lore: show each stat's EV
        val evLore: List<Component> = listOf(
            Component.literal("HP | ATK | SP A | DEF | SP D | SPD").withStyle(Style.EMPTY.withItalic(false).withColor(white)),
            Component.literal(" ")
                .append(evComponent(hpEV)).append("    ")
                .append(evComponent(atkEV)).append("    ")
                .append(evComponent(spatkEV)).append("     ")
                .append(evComponent(defEV)).append("     ")
                .append(evComponent(spdefEV)).append("   ")
                .append(evComponent(spdEV)),
            Component.literal(""),
            Component.literal("Click to edit").withStyle(Style.EMPTY.withItalic(true).withColor(white))
        )

        evBook.set(DataComponents.LORE, ItemLore(evLore))
        evBook.set(DataComponents.HIDE_ADDITIONAL_TOOLTIP, Unit.INSTANCE)
        return evBook
    }

    fun configureLevelEditor(pokemon: Pokemon): ItemStack {
        val itemStack = ItemStack(CobblemonItems.EXPERIENCE_CANDY_XL)

        val level = pokemon.level

        // Determine color based on level
        val levelColor: TextColor = when {
            level == 100 -> TextColor.fromRgb(0xC6A939) // gold for max level
            level >= 80 -> TextColor.fromRgb(0xF44336)  // red
            level >= 50 -> TextColor.fromRgb(0xFF9800)  // orange
            level >= 20 -> TextColor.fromRgb(0xFFEB3B)  // yellow
            else -> TextColor.fromRgb(0x4CAF50)         // green
        }

        // Set display name: "Level: [level]" with color
        val displayName = Component.literal("Level: ")
            .withStyle(Style.EMPTY.withItalic(false))
            .append(Component.literal("$level").withStyle(Style.EMPTY.withColor(levelColor)))

        itemStack.set(DataComponents.CUSTOM_NAME, displayName)

        // Optional lore
        val lore: List<Component> = listOf(
            Component.literal(""),
            Component.literal("Click to edit").withStyle(Style.EMPTY.withColor(white).withItalic(true))
        )
        itemStack.set(DataComponents.LORE, ItemLore(lore))
        itemStack.set(DataComponents.HIDE_ADDITIONAL_TOOLTIP, Unit.INSTANCE)
        return itemStack
    }

    fun configureShinyEditor(pokemon: Pokemon): ItemStack {
        // Use a gem-like item to represent shiny toggle
        val itemStack = ItemStack(Items.NETHER_STAR)

        // Determine color based on shiny status
        val shinyColor: TextColor = if (pokemon.shiny) {
            TextColor.fromRgb(0xFFD700) // Gold for shiny
        } else {
            TextColor.fromRgb(0xC0C0C0) // Silver for normal
        }

        // Display name
        val displayName = Component.literal("Shiny: ")
            .withStyle(Style.EMPTY.withItalic(false).withColor(white))
            .append(
                Component.literal(if (pokemon.shiny) "Yes" else "No")
                    .withStyle(Style.EMPTY.withColor(shinyColor))
            )

        itemStack.set(DataComponents.CUSTOM_NAME, displayName)

        // Lore
        val lore: List<Component> = listOf(
            Component.literal(""),
            Component.literal("Click to edit").withStyle(Style.EMPTY.withColor(white).withItalic(true))
        )
        itemStack.set(DataComponents.LORE, ItemLore(lore))
        itemStack.set(DataComponents.HIDE_ADDITIONAL_TOOLTIP, Unit.INSTANCE)
        return itemStack
    }

    fun configureAbilityEditor(pokemon: Pokemon): ItemStack {
        val itemStack = ItemStack(Items.TOTEM_OF_UNDYING)

        val displayName = Component.literal("Ability: ")
            .withStyle(Style.EMPTY.withItalic(false).withColor(white))
            .append(
                Component.translatable(pokemon.ability.displayName)
                    .withStyle(Style.EMPTY.withColor(pokeColor))
            )

        itemStack.set(DataComponents.CUSTOM_NAME, displayName)

        // Lore: could include effect description or click hint
        val lore: List<Component> = listOf(
            Component.literal(""),
            Component.literal("Click to edit")
                .withStyle(Style.EMPTY.withColor(white).withItalic(true))
        )
        itemStack.set(DataComponents.LORE, ItemLore(lore))

        return itemStack
    }

    fun configureMovesEditor(pokemon: Pokemon): ItemStack {
        val itemStack = ItemStack(CobblemonItems.DUBIOUS_DISC) // placeholder item

        // Current moves
        val moveSet: MoveSet = pokemon.moveSet
        val learnedMoves = moveSet.getMoves().filterNotNull()

        // Total learnable moves from Learnset
        val learnSet: Learnset = pokemon.species.moves
        val totalMoves = (
                learnSet.levelUpMoves.values.flatten() +
                        learnSet.eggMoves +
                        learnSet.tmMoves +
                        learnSet.tutorMoves +
                        learnSet.evolutionMoves +
                        learnSet.formChangeMoves
                ).distinct().size

        // Display name
        val displayName = Component.literal("Moves learned: ")
            .withStyle(Style.EMPTY.withItalic(false))
            .append(
                Component.literal("${learnedMoves.size} / $totalMoves")
                    .withStyle(Style.EMPTY.withColor(pokeColor))
            )
        itemStack.set(DataComponents.CUSTOM_NAME, displayName)

        // Lore: list each learned move with a dot
        val movesLore: List<Component> = if (learnedMoves.isNotEmpty()) {
            learnedMoves.map { move ->
                Component.literal("• ").withStyle(Style.EMPTY.withColor(TextColor.fromRgb(0xC5C5C5)).withItalic(false))
                    .append(Component.translatable(move.name)
                        .withStyle(Style.EMPTY.withColor(white).withItalic(false)))
            }
        } else {
            listOf(
                Component.literal("No moves learned")
                    .withStyle(Style.EMPTY.withColor(white).withItalic(true))
            )
        }

        // Interactive hint
        val finalLore = movesLore + Component.literal("") + Component.literal("Click to edit")
            .withStyle(Style.EMPTY.withColor(white).withItalic(true))

        itemStack.set(DataComponents.LORE, ItemLore(finalLore))
        itemStack.set(DataComponents.HIDE_ADDITIONAL_TOOLTIP, Unit.INSTANCE)
        return itemStack
    }

    fun configureNatureItem(cobblemonItem: MintItem, nature : Nature) : ItemStack {
        val itemStack = ItemStack(cobblemonItem)

        itemStack.set(DataComponents.CUSTOM_NAME, Component.translatable(nature.displayName).withStyle(Style.EMPTY.withItalic(false)))

        val boosted = nature.increasedStat
        val lowered = nature.decreasedStat

        val natureStats: Component = if (boosted != null && lowered != null) {
            Component.literal("(+").withStyle(Style.EMPTY.withColor(white).withItalic(false))
                .append(Component.literal(statShortName(boosted)).withStyle(Style.EMPTY.withColor(
                    boostColor).withItalic(false)))
                .append(Component.literal(" / -").withStyle(Style.EMPTY.withColor(white).withItalic(false)))
                .append(Component.literal(statShortName(lowered)).withStyle(Style.EMPTY.withColor(
                    lowerColor).withItalic(false)))
                .append(Component.literal(")").withStyle(Style.EMPTY.withColor(white).withItalic(false)))
        } else {
            Component.literal("(neutral)").withStyle(Style.EMPTY.withColor(white).withItalic(false))
        }
        val itemLore: List<Component> = listOf(
            natureStats,
            Component.literal(""),
            Component.literal("Required Essence: ").withStyle(Style.EMPTY.withColor(white).withItalic(false))
                .append("✦20").withStyle(Style.EMPTY.withColor(essenceColor))
        )

        itemStack.set(DataComponents.LORE, ItemLore(itemLore))
        itemStack.set(DataComponents.HIDE_ADDITIONAL_TOOLTIP, Unit.INSTANCE)
        return itemStack

    }

    fun configureBackItem() : ItemStack {
        val itemStack = ItemStack(Blocks.BARRIER)
        itemStack.set(DataComponents.CUSTOM_NAME, Component.literal("Back").withStyle(Style.EMPTY.withColor(0xE5271A).withItalic(false)))
        return itemStack
    }

}