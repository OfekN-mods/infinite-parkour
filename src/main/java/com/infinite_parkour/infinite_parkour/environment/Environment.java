package com.infinite_parkour.infinite_parkour.environment;

import com.infinite_parkour.infinite_parkour.world.DynamicLevel;
import com.infinite_parkour.infinite_parkour.world.IPKLevels;
import net.fabricmc.fabric.api.event.player.*;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Function;

public abstract class Environment {
	private static final Map<Level, Environment> ENVIRONMENTS = new HashMap<>();

	public static void registerEvents() {
		UseEntityCallback.EVENT.register((player, level, hand, entity, entityHitResult) ->
				applyEvent(level, InteractionResult.PASS, env -> env.onUseEntity(player, hand, entity, entityHitResult)));
		UseItemCallback.EVENT.register((player, level, hand) ->
				applyEvent(level, InteractionResult.PASS, env -> env.onUseItem(player, hand)));
		UseBlockCallback.EVENT.register((player, level, hand , blockHitResult) ->
				applyEvent(level, InteractionResult.PASS, env -> env.onUseBlock(player, hand, blockHitResult)));
		AttackBlockCallback.EVENT.register((player, level, hand, pos, direction) ->
				applyEvent(level, InteractionResult.PASS, env -> env.onAttackBlock(player, hand, pos, direction)));
		PlayerBlockBreakEvents.BEFORE.register((level, player, pos, state, blockEntity) ->
				applyEvent(level, true, env -> env.onBreakBlock(player, pos, state, blockEntity)));
	}

	public static void callTickFunctions() {
		//Converting to an array to be safe of ConcurrentModificationException
		Environment[] managers = ENVIRONMENTS.values().toArray(Environment[]::new);
		for (Environment manager : managers) {
			manager.onTick();
		}
	}

	public static void cleanup() {
		ENVIRONMENTS.clear();
	}

	@Nullable
	public static Environment getByLevel(Level level) {
		return ENVIRONMENTS.get(level);
	}

	private static <R> R applyEvent(Level level, R def, Function<Environment, R> caller) {
		Environment env = getByLevel(level);
		if (env == null) {
			return def;
		}
		return caller.apply(env);
	}

	private static void applyEvent(Level level, Consumer<Environment> caller) {
		Environment env = getByLevel(level);
		if (env != null) {
			caller.accept(env);
		}
	}

	public final DynamicLevel level;

	protected Environment() {
		this.level = new DynamicLevel(this::closeCallback);
		ENVIRONMENTS.put(level, this);
	}

	// API Function

	public void delete() {
		level.close();
	}

	public boolean isDeleted() {
		return level.isClosed();
	}

	// Events

	protected void onClose() {}

	protected void onTick() {}

	protected InteractionResult onUseEntity(Player player, InteractionHand hand, Entity entity, @Nullable EntityHitResult entityHitResult) {
		return InteractionResult.PASS;
	}

	protected InteractionResult onUseItem(Player player, InteractionHand hand) {
		return InteractionResult.PASS;
	}

	protected InteractionResult onUseBlock(Player player, InteractionHand hand, BlockHitResult blockHitResult) {
		return InteractionResult.PASS;
	}

	protected InteractionResult onAttackBlock(Player player, InteractionHand hand, BlockPos blockPos, Direction direction) {
		return InteractionResult.PASS;
	}

	protected boolean onBreakBlock(Player player, BlockPos blockPos, BlockState state, @Nullable BlockEntity blockEntity) {
		return true;
	}


	// Internal functions

	private void closeCallback() {
		level.players().forEach(IPKLevels::teleportLobby);
		ENVIRONMENTS.remove(this.level, this);
		onClose();
	}
}
