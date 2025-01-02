package com.infinite_parkour.infinite_parkour;

import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructurePlaceSettings;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplateManager;

public final class IPKUtils {
	private IPKUtils() {}

	public static void placeStructure(ServerLevel level, BlockPos position, ResourceLocation structureId) {
		StructureTemplateManager structureManager = level.getStructureManager();

		// Load the structure template
		StructureTemplate template = structureManager.get(structureId).orElse(null);
		if (template == null) {
			level.getServer().getPlayerList().broadcastSystemMessage(
					Component.literal("Failed to load structure: " + structureId), false);
			return;
		}

		// Place the structure at the specified position
		template.placeInWorld(
				level, position, position,
				new StructurePlaceSettings(),
				level.random, 2
		);
	}

	public static void fill(Level level, BlockState state, int x0, int y0, int z0, int x1, int y1, int z1) {
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

	public static int posToInt(BlockPos pos) {
		return pos.getX() | pos.getY() << 6 | pos.getZ() << 12;
	}

	public static void intToPos(int posInt, BlockPos.MutableBlockPos pos) {
		pos.setX(posInt & 0x3F);
		pos.setY((posInt >> 6) & 0x3F);
		pos.setZ((posInt >> 12) & 0x3F);
	}
}
