package com.infinite_parkour.infinite_parkour.world;

import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
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
}
