package com.infinite_parkour.infinite_parkour.world;

import com.infinite_parkour.infinite_parkour.InfiniteParkour;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;

import java.util.Collections;

public class EditorEnvironment extends BaseEnvironment {
	private static final ResourceLocation STRUCT_HOLOGRAM = InfiniteParkour.loc("editor_hologram_room");

	public EditorEnvironment(ServerPlayer player) {
		super(player);
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
	public boolean onTick(EnvironmentManager manager) {
		if (super.onTick(manager)) {
			return true;
		}
		if (player.getY() <= 0.01 && player.onGround()) {
			respawn();
		}
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
