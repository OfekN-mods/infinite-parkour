package com.infinite_parkour.infinite_parkour.mixin;

import com.infinite_parkour.infinite_parkour.world.IPKLevels;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ServerPlayer.class)
public abstract class ServerPlayerMixin {
	@Inject(method = "readAdditionalSaveData", at = @At("HEAD"), cancellable = true)
	private void onReadAdditionalSaveData(CompoundTag compoundTag, CallbackInfo ci) {
		ServerPlayer player = (ServerPlayer) (Object) this;
		IPKLevels.teleportLobbyInit(player, compoundTag);
		ci.cancel();
	}

	@Inject(method = "readAdditionalSaveData", at = @At("HEAD"), cancellable = true)
	private void onAddAdditionalSaveData(CompoundTag compoundTag, CallbackInfo ci) {
		ci.cancel();
	}
}