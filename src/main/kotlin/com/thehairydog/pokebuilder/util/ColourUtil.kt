package com.thehairydog.pokebuilder.util

import net.minecraft.network.chat.TextColor

object ColourUtil {

    // Base colors (constants)
    val pokeColor: TextColor = TextColor.fromRgb(0xEE1515)   // Pokémon red
    val essenceColor: TextColor = TextColor.fromRgb(0xF8F8FF) // ghost white
    val white: TextColor = TextColor.fromRgb(0xFFFFFF)
    val boostColor: TextColor = TextColor.fromRgb(0xFF5555)
    val lowerColor: TextColor = TextColor.fromRgb(0x5555FF)
    val maleColor: TextColor = TextColor.fromRgb(0x87CEFA)
    val femaleColor: TextColor = TextColor.fromRgb(0xFFB6C1)
    val genderlessColor: TextColor = white

    // Type colors map (initialized once)
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
