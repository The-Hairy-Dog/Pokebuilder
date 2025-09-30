package com.thehairydog.pokeinfuser.util

import net.minecraft.network.chat.TextColor

object ColourUtil {

    // Main PokeInfuser theme
    val mainInfusionColour: TextColor = TextColor.fromRgb(0x1CE1FF)       // Electric cyan
    val secondaryInfusionColour: TextColor = TextColor.fromRgb(0xB19EFF)  // Lavender accent
    val plainTextInfusionColour: TextColor = TextColor.fromRgb(0xFFFFFF)  // Neutral white

    // Generic / base colors
    val white: TextColor = TextColor.fromRgb(0xFFFFFF)
    val confirmColor: TextColor = TextColor.fromRgb(0x26C543)   // Green confirm
    val cancelColor: TextColor = TextColor.fromRgb(0xFF5555)    // Red cancel

    // Gender colors
    val maleColor: TextColor = TextColor.fromRgb(0x4A90E2)      // Deep blue
    val femaleColor: TextColor = TextColor.fromRgb(0xFF69B4)    // Pink
    val genderlessColor: TextColor = TextColor.fromRgb(0xC0C0C0) // Silver

    // Stat colors (minimalistic)
    val hpColor: TextColor = TextColor.fromRgb(0xFF6B81)        // Soft red
    val attackColor: TextColor = TextColor.fromRgb(0xFFA500)    // Orange
    val defenseColor: TextColor = TextColor.fromRgb(0xD3D3D3)   // Light gray
    val spAttackColor: TextColor = TextColor.fromRgb(0x87CEFA)  // Sky blue
    val spDefenseColor: TextColor = TextColor.fromRgb(0x90EE90) // Pastel green
    val speedColor: TextColor = TextColor.fromRgb(0xFFFACD)     // Light yellow
    val gold: TextColor = TextColor.fromRgb(0xC6A939)

    // Boost / Lower
    val boostColor: TextColor = TextColor.fromRgb(0x26C543)     // Green boost
    val lowerColor: TextColor = TextColor.fromRgb(0xFF5555)     // Red lower

    // Essence / currency
    val essenceColor: TextColor = mainInfusionColour            // Use cyan for essence
    val essenceCurrencyColor: TextColor = mainInfusionColour

    // Pokémon base color
    val pokeColor: TextColor = TextColor.fromRgb(0xEE1515)      // Classic red

    // Magenta for rare highlighting
    val magenta: TextColor = TextColor.fromRgb(0xC80176)

    // Type colors map
    val typeColors: Map<String, TextColor> = mapOf(
        "normal" to TextColor.fromRgb(0xA8A77A),
        "fire" to TextColor.fromRgb(0xEE8130),
        "water" to TextColor.fromRgb(0x6390F0),
        "electric" to TextColor.fromRgb(0xF7D02C),
        "grass" to TextColor.fromRgb(0x7AC74C),
        "ice" to TextColor.fromRgb(0x96D9D6),
        "fighting" to TextColor.fromRgb(0xC22E28),
        "poison" to TextColor.fromRgb(0xA33EA1),
        "ground" to TextColor.fromRgb(0xE2BF65),
        "flying" to TextColor.fromRgb(0xA98FF3),
        "psychic" to TextColor.fromRgb(0xF95587),
        "bug" to TextColor.fromRgb(0xA6B91A),
        "rock" to TextColor.fromRgb(0xB6A136),
        "ghost" to TextColor.fromRgb(0x735797),
        "dragon" to TextColor.fromRgb(0x6F35FC),
        "dark" to TextColor.fromRgb(0x705746),
        "steel" to TextColor.fromRgb(0xB7B7CE),
        "fairy" to TextColor.fromRgb(0xD685AD)
    )
}
