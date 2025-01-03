package com.infinite_parkour.infinite_parkour.environment.editor;

import com.mojang.math.Transformation;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Display;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.block.Blocks;
import org.joml.Vector3f;

import java.util.UUID;

public class EditorPanel {
	private final ServerLevel level;
	private final EditorHolograms holograms;
	private final UUID pageText;

	public EditorPanel(EditorEnvironment env, EditorHolograms holograms) {
		this.level = env.level;
		this.holograms = holograms;
		env.createTextDisplay(31.5, 37.0, -30.99, 0, 2, Component.literal("Editor Controls").withStyle(ChatFormatting.GOLD));
		env.createTextDisplay(31.5, 36.0, -30.99, 0, 1.5f, Component.literal("Pack").withStyle(ChatFormatting.YELLOW));
		env.createTextDisplay(31.5, 35.5, -30.99, 0, 1.5f, Component.literal("my_pack").withStyle(ChatFormatting.WHITE));
		env.createTextDisplay(31.5, 34.5, -30.99, 0, 1.5f, Component.literal("Page").withStyle(ChatFormatting.YELLOW));
		pageText = env.createTextDisplay(31.5, 34.0, -30.99, 0, 1.5f, Component.literal("0").withStyle(ChatFormatting.WHITE));

		env.createInteraction(31.5, 35.4, -33.49, 5, 0.5f, this::openSelectPack);
		createPageArrow(env, true);
		createPageArrow(env, false);
	}

	private void createPageArrow(EditorEnvironment env, boolean prev) {
		var nextPage = new Display.BlockDisplay(EntityType.BLOCK_DISPLAY, level);
		nextPage.setPos(prev ? 30.5 : 32.5, 34.2, prev ? -30.99 : -31.49);
		nextPage.setTransformation(new Transformation(null, null, new Vector3f(0.5f), null));
		nextPage.setBlockState(Blocks.SMOOTH_QUARTZ_STAIRS.defaultBlockState());
		nextPage.setYRot(prev ? -90 : 90);
		nextPage.setXRot(45);
		level.addFreshEntity(nextPage);

		int offset = prev ? -1 : 1;
		env.createInteraction(prev ? 30.75 : 32.25, 33.8, -31.29, 0.6f, 0.8f, player -> movePage(offset));
	}

	private void openSelectPack(ServerPlayer player) {
		// TODO open GUI
		player.displayClientMessage(Component.literal("TODO Select Pack"), false);
	}

	private void movePage(int offset) {
		int newPage = holograms.getPage() + offset;
		if (holograms.setPage(newPage)) {
			if (level.getEntity(pageText) instanceof Display.TextDisplay display) {
				display.setText(Component.literal("" + newPage));
			}
		}
	}
}
