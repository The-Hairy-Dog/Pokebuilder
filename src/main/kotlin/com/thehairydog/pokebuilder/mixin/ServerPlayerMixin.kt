package com.thehairydog.pokebuilder.mixin

import com.thehairydog.pokebuilder.pokeessence.PokeEssenceHandler
import net.minecraft.nbt.CompoundTag
import net.minecraft.server.level.ServerPlayer
import org.spongepowered.asm.mixin.Mixin
import org.spongepowered.asm.mixin.injection.At
import org.spongepowered.asm.mixin.injection.Inject
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo

@Mixin(ServerPlayer::class)
class ServerPlayerMixin {

    @Inject(
        method = ["readAdditionalSaveData"], at = [At("RETURN")]
    )
    private fun ServerPlayer.readAdditionalSaveData(tag: CompoundTag, ci: CallbackInfo) {
        PokeEssenceHandler.load(this, tag)
    }

    @Inject(
        method = ["addAdditionalSaveData"], at = [At("RETURN")]
    )
    private fun ServerPlayer.addAdditionalSaveData(tag: CompoundTag, ci: CallbackInfo) {
        PokeEssenceHandler.save(this, tag)
    }

}



