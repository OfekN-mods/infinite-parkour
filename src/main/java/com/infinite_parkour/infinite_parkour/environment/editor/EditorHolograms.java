package com.infinite_parkour.infinite_parkour.environment.editor;

import com.infinite_parkour.infinite_parkour.IPKUtils;
import com.infinite_parkour.infinite_parkour.InfiniteParkour;
import com.infinite_parkour.infinite_parkour.data.JumpData;
import com.infinite_parkour.infinite_parkour.data.JumpPack;
import com.mojang.math.Transformation;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Display;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.Blocks;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector3f;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class EditorHolograms {
	private static final ResourceLocation STRUCT_HOLOGRAM = InfiniteParkour.loc("editor_hologram_room");
	private static final int[] POS_X = {18, 22, 26, 36, 40, 44};
	private static final int POS_Y = 33;
	private static final int[] POS_Z = {-20, -16, -12, -8, -4};
	private static final int COUNT = POS_X.length * POS_Z.length;
	private final ServerLevel level;
	private final List<JumpData> jumps = new ArrayList<>();
	private final List<UUID> hologramBases = new ArrayList<>();
	private final EditorCanvas canvas;
	private int page = 0;
	private int loadingI = 0;
	
	public EditorHolograms(ServerLevel level, EditorCanvas canvas) {
		this.level = level;
		this.canvas = canvas;

		IPKUtils.placeStructure(level, new BlockPos(16, 31, -33), STRUCT_HOLOGRAM);

		for (int x : POS_X) {
			for (int z : POS_Z) {
				Display.BlockDisplay entity = new Display.BlockDisplay(EntityType.BLOCK_DISPLAY, level);
				entity.setPos(x + 0.5, POS_Y + 0.5, z + 0.5);
				level.addFreshEntity(entity);
				hologramBases.add(entity.getUUID());
				level.setBlockAndUpdate(new BlockPos(x, POS_Y, z), Blocks.ORANGE_STAINED_GLASS.defaultBlockState());
			}
		}
	}

	public void saveInto(JumpPack pack) {
		List<JumpData> result = pack.jumps();
		result.clear();
		for (JumpData jump : jumps) {
			if (jump != null) {
				result.add(jump);
			}
		}
	}

	public void loadFrom(JumpPack pack) {
		jumps.clear();
		jumps.addAll(pack.jumps());
	}

	public boolean onUseBlock(Player player, BlockPos pos) {
		if (pos.getY() != POS_Y) {
			return false;
		}
		int col = switch (pos.getX()) {
			case 18 -> 0;
			case 22 -> 1;
			case 26 -> 2;
			case 36 -> 3;
			case 40 -> 4;
			case 44 -> 5;
			default -> -1;
		};
		if (col == -1) {
			return false;
		}
		int row = switch (pos.getZ()) {
			case -20 -> 0;
			case -16 -> 1;
			case -12 -> 2;
			case -8 -> 3;
			case -4 -> 4;
			default -> -1;
		};
		if (row == -1) {
			return false;
		}
		int i = row + col * 5;
		JumpData saveData = canvas.save(new JumpData(new ArrayList<>(), new ArrayList<>(), 1));
		JumpData loadData = getJump(i);
		setJump(i, saveData);
		canvas.load(loadData);
		updateHologram(i);
		return true;
	}

	@Nullable
	private JumpData getJump(int i) {
		i += page * COUNT;
		if (i >= jumps.size()) {
			return null;
		}
		return jumps.get(i);
	}

	private void setJump(int i, @Nullable JumpData data) {
		i += page * COUNT;
		while (i >= jumps.size()) {
			jumps.add(null);
		}
		jumps.set(i, data);
	}

	@Nullable
	private Entity getHologramBase(int i) {
		return level.getEntity(hologramBases.get(i));
	}

	private void cleanupHologram(Entity base) {
		base.getPassengers().forEach(e -> e.remove(Entity.RemovalReason.DISCARDED));
		level.setBlockAndUpdate(base.blockPosition(), Blocks.ORANGE_STAINED_GLASS.defaultBlockState());
	}

	private void cleanupAll() {
		for (int i = 0; i < COUNT; i++) {
			Entity base = getHologramBase(i);
			if (base != null) {
				cleanupHologram(base);
			}
		}
		loadingI = 0;
	}

	private void updateHologram(int i) {
		Entity base = getHologramBase(i);
		if (base == null) {
			return;
		}
		cleanupHologram(base);
		JumpData jumpData = getJump(i);
		if (jumpData == null) {
			level.setBlockAndUpdate(base.blockPosition(), Blocks.BLACK_STAINED_GLASS.defaultBlockState());
			return;
		}
		level.setBlockAndUpdate(base.blockPosition(), Blocks.GLASS.defaultBlockState());

		int minX = 63;
		int minY = 63;
		int minZ = 63;
		int maxX = 0;
		int maxY = 0;
		int maxZ = 0;
		BlockPos.MutableBlockPos pos = new BlockPos.MutableBlockPos();
		for (var blockData : jumpData.blocks()) {
			IPKUtils.intToPos(blockData.pos(), pos);
			int x = pos.getX();
			int y = pos.getY();
			int z = pos.getZ();
			minX = Math.min(minX, x);
			minY = Math.min(minY, y);
			minZ = Math.min(minZ, z);
			maxX = Math.max(maxX, x);
			maxY = Math.max(maxY, y);
			maxZ = Math.max(maxZ, z);
		}
		float x0 = 0.5f * (minX + maxX);
		float y0 = 0.5f * (minY + maxY);
		float z0 = 0.5f * (minZ + maxZ);
		float maxD = 0;
		for (var blockData : jumpData.blocks()) {
			IPKUtils.intToPos(blockData.pos(), pos);
			float dx = pos.getX() - x0;
			float dy = pos.getY() - y0;
			float dz = pos.getZ() - z0;
			maxD = Math.max(maxD, dx * dx + dz * dz);
			maxD = Math.max(maxD, dy * dy);
		}
		float scale = (float) (0.5 / (Math.sqrt(maxD) + 1));
		float yaw = getHologramYaw();

		for (var blockData : jumpData.blocks()) {
			IPKUtils.intToPos(blockData.pos(), pos);
			int x = pos.getX();
			int y = pos.getY();
			int z = pos.getZ();
			Display.BlockDisplay blockDisplay = new Display.BlockDisplay(EntityType.BLOCK_DISPLAY, level);
			blockDisplay.setTransformation(new Transformation(
					new Vector3f((x - x0 - 0.5f) * scale, (y - y0 - 0.5f) * scale, (z - z0 - 0.5f) * scale),
					null,
					new Vector3f(scale),
					null
			));
			blockDisplay.setBlockState(blockData.state());
			blockDisplay.setPos(base.getX(), base.getY(), base.getZ());
			blockDisplay.setViewRange(10 / 64.0f); // 10 blocks
			blockDisplay.setYRot(yaw);
			level.addFreshEntity(blockDisplay);
			blockDisplay.startRiding(base, true);
		}
	}

	public void tick() {
		float yaw = getHologramYaw();
		for (int i = 0; i < COUNT; i++) {
			Entity entity = getHologramBase(i);
			if (entity == null) {
				continue;
			}
			entity.setYRot(yaw);
			for (Entity passenger : entity.getPassengers()) {
				passenger.setYRot(yaw);
			}
		}
		if (loadingI < COUNT) {
			updateHologram(loadingI);
			loadingI++;
		}
	}

	private float getHologramYaw() {
		return (float) ((level.getGameTime() % 180) * 2);
	}

	public int getPage() {
		return page;
	}

	public boolean setPage(int page) {
		if (canSetPage(page)) {
			this.page = page;
			cleanupAll();
			return true;
		}
		return false;
	}

	public boolean canSetPage(int page) {
		if (page < 0 || page == this.page) {
			return false;
		}
		int pageStart = page * COUNT;
		if (page == 0 || pageStart < jumps.size()) {
			return true;
		}
		for (int i = pageStart - COUNT; i < jumps.size(); i++) {
			if (jumps.get(i) != null) {
				return true;
			}
		}
		return false;
	}
}
