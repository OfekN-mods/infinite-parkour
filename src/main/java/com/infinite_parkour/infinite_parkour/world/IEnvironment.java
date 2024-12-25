package com.infinite_parkour.infinite_parkour.world;

import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.EntityHitResult;
import org.jetbrains.annotations.Nullable;

public interface IEnvironment {
	default void onStart(EnvironmentManager manager) {}
	default void onTick(EnvironmentManager manager) {}
	default void onEnd(EnvironmentManager manager) {}
	default InteractionResult onInteract(EnvironmentManager manager, Player player, InteractionHand interactionHand, Entity entity, @Nullable EntityHitResult entityHitResult) { return InteractionResult.PASS; }
}
