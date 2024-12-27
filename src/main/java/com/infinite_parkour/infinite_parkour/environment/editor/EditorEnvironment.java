package com.infinite_parkour.infinite_parkour.environment.editor;

import com.infinite_parkour.infinite_parkour.InfiniteParkour;
import com.infinite_parkour.infinite_parkour.environment.SinglePlayerEnvironment;
import com.infinite_parkour.infinite_parkour.IPKUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;

public class EditorEnvironment extends SinglePlayerEnvironment {
	private static final ResourceLocation STRUCT_HOLOGRAM = InfiniteParkour.loc("editor_hologram_room");
	private final EditorItemsManager items;
	private final EditorCanvas canvas;

	public EditorEnvironment(ServerPlayer player) {
		super(player);
		this.items = new EditorItemsManager(player);
		this.canvas = new EditorCanvas(player);

		BlockState blockBot = Blocks.BLACK_CONCRETE.defaultBlockState();
		BlockState blockTop = Blocks.LIGHT_BLUE_CONCRETE.defaultBlockState();
		BlockState blockSid = Blocks.WHITE_CONCRETE.defaultBlockState();
		IPKUtils.fill(level, blockSid, -1, 0, 0, -1, 63, 63); // x=-1
		IPKUtils.fill(level, blockSid, 64, 0, 0, 64, 63, 63); // x=64
		IPKUtils.fill(level, blockBot, 0, -1, 0, 63, -1, 63); // y=-1
		IPKUtils.fill(level, blockTop, 0, 64, 0, 63, 64, 63); // y=64
		IPKUtils.fill(level, blockSid, 0, 0, -1, 63, 63, -1); // z=-1
		IPKUtils.fill(level, blockSid, 0, 0, 64, 63, 63, 64); // z=64
		IPKUtils.placeStructure(level, new BlockPos(16, 31, -33), STRUCT_HOLOGRAM);
		respawn();
	}

	@Override
	public void onTick() {
		super.onTick();
		if (isDeleted()) {
			return;
		}

		giveAbilities();
		canvas.tick(level);

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
	public InteractionResult onUseBlock(Player player, InteractionHand interactionHand, BlockHitResult blockHitResult) {
		return canvas.useBlock(level, interactionHand, blockHitResult);
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
		player.getAttributes().getInstance(Attributes.BLOCK_BREAK_SPEED).setBaseValue(0);
		player.getAttributes().getInstance(Attributes.BLOCK_INTERACTION_RANGE).setBaseValue(10);
	}

	private void respawn() {
		player.teleportTo(level, 31.5, 32.0, -4.5, Collections.emptySet(), 0, 0, true);
	}
}
