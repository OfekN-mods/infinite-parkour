package com.infinite_parkour.infinite_parkour.environment.editor;

import com.infinite_parkour.infinite_parkour.data.JumpBlockData;
import com.infinite_parkour.infinite_parkour.data.JumpData;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.particles.TrailParticleOption;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.TrapDoorBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;

import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

public class EditorCanvas {
	private final ServerPlayer player;
	private int breakCooldown = 0;
	private final Set<Long> trails = new HashSet<>();
	private final Set<Integer> blocks = new HashSet<>();
	private BlockPos trailFirstPos = null;

	public EditorCanvas(ServerPlayer player) {
		this.player = player;
	}

	public JumpData save(ServerLevel level, JumpData base) {
		BlockPos.MutableBlockPos pos = new BlockPos.MutableBlockPos();
		List<JumpBlockData> resultBlocks = blocks.stream().map(posInt -> {
			intToPos(posInt, pos);
			var state = level.getBlockState(pos);
			return new JumpBlockData(posInt, state);
		}).toList();
		List<Long> resultTrails = trails.stream().toList();
		return new JumpData(resultBlocks, resultTrails, base.weight());
	}

	public void clear(ServerLevel level) {
		BlockPos.MutableBlockPos pos = new BlockPos.MutableBlockPos();
		for (int posInt : blocks) {
			intToPos(posInt, pos);
			level.setBlockAndUpdate(pos, Blocks.AIR.defaultBlockState());
		}
		blocks.clear();
		trails.clear();
		trailFirstPos = null;
	}

	public void load(ServerLevel level, JumpData jumpData) {
		clear(level);
		BlockPos.MutableBlockPos pos = new BlockPos.MutableBlockPos();
		for (JumpBlockData block : jumpData.blocks()) {
			intToPos(block.pos(), pos);
			level.setBlock(pos, block.state(), 2);
			blocks.add(block.pos());
		}
		trails.addAll(jumpData.trails());
	}

	public final void tick(ServerLevel level) {
		breakBlock(level);
		if (trailFirstPos != null) {
			if (blocks.contains(posToInt(trailFirstPos))) {
				double x = trailFirstPos.getX() + 0.5;
				double y = trailFirstPos.getY() + 1.5;
				double z = trailFirstPos.getZ() + 0.5;
				level.sendParticles(ParticleTypes.WITCH, x, y, z, 1, 0, 0, 0, 0);
			} else {
				trailFirstPos = null;
			}
		}
		Iterator<Long> trailIterator = trails.iterator();
		while (trailIterator.hasNext()) {
			long trail = trailIterator.next();
			if (!blocks.contains((int)(trail & 0x3FFFF)) || !blocks.contains((int)(trail >> 18))) {
				trailIterator.remove();
				continue;
			}
			double x0 = (trail & 0x3F) + 0.5;
			double y0 = ((trail >> 6) & 0x3F) + 0.5;
			double z0 = ((trail >> 12) & 0x3F) + 0.5;
			double x1 = ((trail >> 18) & 0x3F) + 0.5;
			double y1 = ((trail >> 24) & 0x3F) + 0.5;
			double z1 = ((trail >> 30) & 0x3F) + 0.5;
			double dx = x1 - x0;
			double dy = y1 - y0;
			double dz = z1 - z0;
			double d = Math.sqrt(dx * dx + dy * dy + dz * dz);
			level.sendParticles(
					new TrailParticleOption(new Vec3(x1, y1, z1), 0xCC334C, (int)(d * 5)),
					x0, y0, z0, 10, 0, 0, 0, 0.1
			);
		}
	}

	private void breakBlock(ServerLevel level) {
		if (breakCooldown > 0) {
			breakCooldown--;
			return;
		}
		if (player.gameMode.isDestroyingBlock) {
			BlockPos pos = player.gameMode.destroyPos;
			if (isInCanvas(pos)) {
				level.setBlockAndUpdate(pos, Blocks.AIR.defaultBlockState());
				blocks.remove(posToInt(pos));
				breakCooldown = 4;
			}
		}
	}

	private static int posToInt(BlockPos pos) {
		return pos.getX() | pos.getY() << 6 | pos.getZ() << 12;
	}

	private void intToPos(int posInt, BlockPos.MutableBlockPos pos) {
		pos.setX(posInt & 0x3F);
		pos.setY((posInt >> 6) & 0x3F);
		pos.setZ((posInt >> 12) & 0x3F);
	}

	public InteractionResult useBlock(ServerLevel level, InteractionHand interactionHand, BlockHitResult blockHitResult) {
		BlockPos pos = blockHitResult.getBlockPos();
		BlockState blockState = level.getBlockState(pos);
		Block block = blockState.getBlock();
		ItemStack stack = player.getItemInHand(interactionHand);
		boolean pressesShift = player.getLastClientInput().shift();

		if (!(pressesShift || stack.isEmpty())) {
			if (block == Blocks.IRON_TRAPDOOR) {
				boolean isOpen = blockState.getValue(TrapDoorBlock.OPEN);
				level.setBlockAndUpdate(pos, blockState.setValue(TrapDoorBlock.OPEN, !isOpen));
				level.playSound(null, pos, SoundEvents.IRON_TRAPDOOR_OPEN, SoundSource.BLOCKS, 1.0f, 1.0f);
				return InteractionResult.SUCCESS;
			}
		}
		if (ItemStack.isSameItemSameComponents(stack, EditorItem.TRAIL.stack)) {
			if (!isInCanvas(pos)) {
				return InteractionResult.FAIL;
			}
			if (trailFirstPos == null) {
				trailFirstPos = pos;
			} else {
				if (!trailFirstPos.equals(pos)) {
					int p0 = posToInt(trailFirstPos);
					int p1 = posToInt(pos);
					long trail = (long)p1 << 18 | p0;
					long trailRev = (long)p0 << 18 | p1;
					trails.remove(trailRev);
					if (!trails.add(trail)) {
						trails.remove(trail);
					}
				}
				trailFirstPos = null;
			}
			return InteractionResult.SUCCESS;
		}

		BlockPos placePos = blockHitResult.getBlockPos().relative(blockHitResult.getDirection());
		if (!isInCanvas(placePos)) {
			return InteractionResult.FAIL;
		}
		blocks.add(posToInt(placePos));
		return InteractionResult.PASS;
	}

	private static boolean isInCanvas(BlockPos pos) {
		int x = pos.getX();
		int y = pos.getY();
		int z = pos.getZ();
		return 0 <= x && x < 64 && 0 <= y && y < 64 && 0 <= z && z < 64;
	}
}
