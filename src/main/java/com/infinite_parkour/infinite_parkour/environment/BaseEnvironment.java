package com.infinite_parkour.infinite_parkour.environment;

import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Display;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Interaction;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.EntityHitResult;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public abstract class BaseEnvironment extends Environment {
	private final Map<UUID, Runnable> actions = new HashMap<>();

	protected BaseEnvironment() {}

	@Override
	public InteractionResult onUseEntity(Player player, InteractionHand interactionHand, Entity entity, @Nullable EntityHitResult entityHitResult) {
		Runnable action = actions.get(entity.getUUID());
		if (action == null) {
			return InteractionResult.PASS;
		}
		if (entityHitResult == null) {
			action.run();
		}
		return InteractionResult.SUCCESS;
	}

	protected void createInteraction(double x, double y, double z, float width, float height, Runnable action) {
		Interaction interaction = new Interaction(EntityType.INTERACTION, level);
		interaction.setPos(x, y, z);
		interaction.setWidth(width);
		interaction.setHeight(height);
		level.addFreshEntity(interaction);
		actions.put(interaction.getUUID(), action);
	}

	protected void createTextDisplay(double x, double y, double z, float yRot, Component text) {
		Display.TextDisplay display = new Display.TextDisplay(EntityType.TEXT_DISPLAY, level);
		display.setPos(x, y, z);
		display.setText(text);
		display.setYRot(yRot);
		level.addFreshEntity(display);
	}

	protected void createTextDisplay(double x, double y, double z, Component text) {
		Display.TextDisplay display = new Display.TextDisplay(EntityType.TEXT_DISPLAY, level);
		display.setPos(x, y, z);
		display.setText(text);
		display.setBillboardConstraints(Display.BillboardConstraints.VERTICAL);
		level.addFreshEntity(display);
	}
}