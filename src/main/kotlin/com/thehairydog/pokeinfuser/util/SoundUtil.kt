package com.thehairydog.pokeinfuser.util

import net.minecraft.server.level.ServerPlayer
import net.minecraft.sounds.SoundEvents
import net.minecraft.sounds.SoundSource

object SoundUtil {

    fun playPurchaseSound(player: ServerPlayer) {
        player.level().playSound(
            /* player to hear it */ null,
            /* position */ player.blockPosition(),
            SoundEvents.PLAYER_LEVELUP,
            SoundSource.PLAYERS,
            1.0f,
            1.0f
        )
    }

    fun playCancelSound(player: ServerPlayer) {
        player.level().playSound(
            null,
            player.blockPosition(),
            SoundEvents.VILLAGER_HURT,
            SoundSource.PLAYERS,
            1.0f,
            1.0f
        )
    }

    fun playClickSound(player: ServerPlayer) {
        player.level().playSound(
            null,
            player.blockPosition(),
            SoundEvents.UI_BUTTON_CLICK.value(),
            SoundSource.PLAYERS,
            1.2f,
            1.2f
        )
    }

    fun playBackSound(player: ServerPlayer) {
        player.level().playSound(
            null,
            player.blockPosition(),
            SoundEvents.BOOK_PAGE_TURN,
            SoundSource.PLAYERS,
            1.0f,
            1.0f
        )
    }
}
