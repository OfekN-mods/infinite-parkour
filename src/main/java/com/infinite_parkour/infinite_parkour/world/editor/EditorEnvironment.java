package com.infinite_parkour.infinite_parkour.world.editor;

import com.infinite_parkour.infinite_parkour.InfiniteParkour;
import com.infinite_parkour.infinite_parkour.world.BaseEnvironment;
import com.infinite_parkour.infinite_parkour.world.EnvironmentManager;
import com.infinite_parkour.infinite_parkour.world.IPKUtils;
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

public class EditorEnvironment extends BaseEnvironment {
	private static final ResourceLocation STRUCT_HOLOGRAM = InfiniteParkour.loc("editor_hologram_room");
	private final EditorItemsManager items;
	private final EditorCanvas canvas;

	public EditorEnvironment(ServerPlayer player) {
		super(player);
		this.items = new EditorItemsManager(player);
		this.canvas = new EditorCanvas(player);
	}

	@Override
	public void onStart(EnvironmentManager manager) {
		super.onStart(manager);
		BlockState blockBlack = Blocks.BLACK_CONCRETE.defaultBlockState();
		BlockState blockBlue = Blocks.LIGHT_BLUE_CONCRETE.defaultBlockState();
		BlockState blockWhite = Blocks.WHITE_CONCRETE.defaultBlockState();
		fill(blockWhite, -1, 0, 0, -1, 63, 63); // x=-1
		fill(blockWhite, 64, 0, 0, 64, 63, 63); // x=64
		fill(blockBlack, 0, -1, 0, 63, -1, 63); // y=-1
		fill(blockBlue, 0, 64, 0, 63, 64, 63);  // y=64
		fill(blockWhite, 0, 0, -1, 63, 63, -1); // z=-1
		fill(blockWhite, 0, 0, 64, 63, 63, 64); // z=64
		IPKUtils.placeStructure(level, new BlockPos(16, 31, -33), STRUCT_HOLOGRAM);
		respawn();
	}

	@Override
	public InteractionResult onUseItem(EnvironmentManager manager, Player player, InteractionHand interactionHand) {
		return InteractionResult.PASS;
	}

	@Override
	public InteractionResult onUseBlock(EnvironmentManager manager, Player player, InteractionHand interactionHand, BlockHitResult blockHitResult) {
		return canvas.useBlock(level, interactionHand, blockHitResult);
	}

	@Override
	public InteractionResult onAttackBlock(EnvironmentManager manager, Player player, InteractionHand interactionHand, BlockPos blockPos, Direction direction) {
		return InteractionResult.PASS;
	}

	@Override
	public boolean onTick(EnvironmentManager manager) {
		if (super.onTick(manager)) {
			return true;
		}

		giveAbilities();
		canvas.tick(level);

		if (player.getY() <= 0.01 && player.onGround()) {
			respawn();
		}
		items.giveBundles();
		return false;
	}

	private void giveAbilities() {
		player.getAbilities().mayfly = true;
		player.getAbilities().instabuild = true;
		player.onUpdateAbilities();
		player.getAttributes().getInstance(Attributes.BLOCK_BREAK_SPEED).setBaseValue(0);
		player.getAttributes().getInstance(Attributes.BLOCK_INTERACTION_RANGE).setBaseValue(10);
	}

	@Override
	public boolean onBreakBlock(EnvironmentManager manager, Player player, BlockPos blockPos, BlockState state, @Nullable BlockEntity blockEntity) {
		return false;
	}

	private void respawn() {
		player.teleportTo(level, 31.5, 32.0, -4.5, Collections.emptySet(), 0, 0, true);
	}

	private void fill(BlockState state, int x0, int y0, int z0, int x1, int y1, int z1) {
		BlockPos.MutableBlockPos pos = new BlockPos.MutableBlockPos(0, 0, 0);
		for (int y = y0; y <= y1; y++) {
			pos.setY(y);
			for (int x = x0; x <= x1; x++) {
				pos.setX(x);
				for (int z = z0; z <= z1; z++) {
					pos.setZ(z);
					level.setBlock(pos, state, 0);
				}
			}
		}
	}
}
