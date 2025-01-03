package com.infinite_parkour.infinite_parkour.environment.editor;

import com.infinite_parkour.infinite_parkour.IPKUtils;
import com.infinite_parkour.infinite_parkour.environment.SinglePlayerEnvironment;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;

public class EditorEnvironment extends SinglePlayerEnvironment {
	private final EditorItemsManager items;
	private final EditorCanvas canvas;
	private final EditorHolograms holograms;
	private final EditorPanel panel;

	public EditorEnvironment(ServerPlayer player) {
		super(player);
		this.items = new EditorItemsManager(player);
		this.canvas = new EditorCanvas(level);
		this.holograms = new EditorHolograms(level, canvas);
		this.panel = new EditorPanel(this, holograms);
		respawn();
	}

	@Override
	public void onTick() {
		super.onTick();
		if (isDeleted()) {
			return;
		}

		giveAbilities();
		canvas.tick();
		holograms.tick();

		if (player.getY() <= 0.01 && player.onGround()) {
			respawn();
		}
		items.giveBundles();
	}

	@Override
	public InteractionResult onUseItem(Player player, InteractionHand interactionHand) {
		return InteractionResult.PASS;
	}

	@Override
	public InteractionResult onUseBlock(Player player, InteractionHand hand, BlockHitResult blockHitResult) {
		if (hand == InteractionHand.MAIN_HAND) {
			if (holograms.onUseBlock(player, blockHitResult.getBlockPos())) {
				return InteractionResult.SUCCESS;
			}
		}
		return canvas.useBlock((ServerPlayer)player, hand, blockHitResult);
	}

	@Override
	public InteractionResult onAttackBlock(Player player, InteractionHand interactionHand, BlockPos blockPos, Direction direction) {
		return InteractionResult.PASS;
	}

	@Override
	public boolean onBreakBlock(Player player, BlockPos blockPos, BlockState state, @Nullable BlockEntity blockEntity) {
		return false;
	}

	private void giveAbilities() {
		player.getAbilities().mayfly = true;
		player.getAbilities().instabuild = true;
		player.onUpdateAbilities();
		IPKUtils.setAttribute(player, Attributes.BLOCK_BREAK_SPEED, 0);
		IPKUtils.setAttribute(player, Attributes.BLOCK_INTERACTION_RANGE, 10);
	}

	private void respawn() {
		player.teleportTo(level, 31.5, 32.0, -4.5, Collections.emptySet(), 0, 0, true);
	}
}
