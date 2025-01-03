package com.infinite_parkour.infinite_parkour.environment;

import com.mojang.math.Transformation;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Display;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Interaction;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.EntityHitResult;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector3f;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.function.Consumer;

public abstract class BaseEnvironment extends Environment {
	private final Map<UUID, Consumer<ServerPlayer>> actions = new HashMap<>();

	protected BaseEnvironment() {}

	@Override
	public InteractionResult onUseEntity(Player player, InteractionHand interactionHand, Entity entity, @Nullable EntityHitResult entityHitResult) {
		Consumer<ServerPlayer> action = actions.get(entity.getUUID());
		if (action == null) {
			return InteractionResult.PASS;
		}
		if (entityHitResult == null) {
			action.accept((ServerPlayer)player);
		}
		return InteractionResult.SUCCESS;
	}

	public void createInteraction(double x, double y, double z, float width, float height, Consumer<ServerPlayer> action) {
		Interaction interaction = new Interaction(EntityType.INTERACTION, level);
		interaction.setPos(x, y, z);
		interaction.setWidth(width);
		interaction.setHeight(height);
		level.addFreshEntity(interaction);
		actions.put(interaction.getUUID(), action);
	}

	public UUID createTextDisplay(double x, double y, double z, float yRot, Component text) {
		Display.TextDisplay display = new Display.TextDisplay(EntityType.TEXT_DISPLAY, level);
		display.setPos(x, y, z);
		display.setText(text);
		display.setYRot(yRot);
		level.addFreshEntity(display);
		return display.getUUID();
	}

	public UUID createTextDisplay(double x, double y, double z, float yRot, float scale, Component text) {
		Display.TextDisplay display = new Display.TextDisplay(EntityType.TEXT_DISPLAY, level);
		display.setPos(x, y, z);
		display.setText(text);
		display.setYRot(yRot);
		display.setTransformation(new Transformation(null, null, new Vector3f(scale), null));
		level.addFreshEntity(display);
		return display.getUUID();
	}

	public UUID createTextDisplay(double x, double y, double z, Component text) {
		Display.TextDisplay display = new Display.TextDisplay(EntityType.TEXT_DISPLAY, level);
		display.setPos(x, y, z);
		display.setText(text);
		display.setBillboardConstraints(Display.BillboardConstraints.VERTICAL);
		level.addFreshEntity(display);
		return display.getUUID();
	}
}