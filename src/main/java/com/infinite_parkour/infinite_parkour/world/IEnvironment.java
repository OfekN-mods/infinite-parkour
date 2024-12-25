package com.infinite_parkour.infinite_parkour.world;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import org.jetbrains.annotations.Nullable;

public interface IEnvironment {
	default void onStart(EnvironmentManager manager) {}
	default boolean onTick(EnvironmentManager manager) { return false; }
	default void onEnd(EnvironmentManager manager) {}
	default InteractionResult onUseEntity(EnvironmentManager manager, Player player, InteractionHand interactionHand, Entity entity, @Nullable EntityHitResult entityHitResult) { return InteractionResult.PASS; }
	default InteractionResult onUseItem(EnvironmentManager manager, Player player, InteractionHand interactionHand) { return InteractionResult.PASS; }
	default InteractionResult onUseBlock(EnvironmentManager manager, Player player, InteractionHand interactionHand, BlockHitResult blockHitResult) { return InteractionResult.PASS; }
	default InteractionResult onAttackBlock(EnvironmentManager manager, Player player, InteractionHand interactionHand, BlockPos blockPos, Direction direction) { return InteractionResult.PASS; }
	default boolean onBreakBlock(EnvironmentManager manager, Player player, BlockPos blockPos, BlockState state, @Nullable BlockEntity blockEntity) { return true; }
}
