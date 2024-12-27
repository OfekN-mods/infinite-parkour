package com.infinite_parkour.infinite_parkour.world;

import net.fabricmc.fabric.api.event.player.*;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.LevelStorageSource;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.util.Files;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public final class EnvironmentManager {
	private static MinecraftServer currentServer;
	private static LevelStorageSource.LevelStorageAccess storageAccess;
	private static final Map<ResourceLocation, EnvironmentManager> BY_LOC = new HashMap<>();

	public static void onStart(MinecraftServer server) {
		currentServer = server;
		var source = LevelStorageSource.createDefault(Path.of("."));
		try {
			storageAccess = source.createAccess("tempworlds");
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	public static void onTick() {
		EnvironmentManager[] managers = BY_LOC.values().toArray(EnvironmentManager[]::new);
		for (EnvironmentManager manager : managers) {
			if (manager.environment.onTick(manager)) {
				manager.delete();
			}
		}
	}

	public static void onStop() {
		currentServer = null;
		try {
			storageAccess.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		BY_LOC.clear();
		try {
			Files.deleteRecursively(new File("tempworlds"));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static void registerEvents() {
		UseEntityCallback.EVENT.register(EnvironmentManager::useEntityCallback);
		UseItemCallback.EVENT.register(EnvironmentManager::useItemCallback);
		UseBlockCallback.EVENT.register(EnvironmentManager::useBlockCallback);
		AttackBlockCallback.EVENT.register(EnvironmentManager::attackBlockCallback);
		PlayerBlockBreakEvents.BEFORE.register(EnvironmentManager::blockBreakEvent);
	}

	private static InteractionResult useEntityCallback(Player player, Level level, InteractionHand interactionHand, Entity entity, @Nullable EntityHitResult entityHitResult) {
		EnvironmentManager manager = getByLevel(level);
		if (manager == null) {
			return InteractionResult.PASS;
		}
		return manager.environment.onUseEntity(manager, player, interactionHand, entity, entityHitResult);
	}

	private static InteractionResult useItemCallback(Player player, Level level, InteractionHand interactionHand) {
		EnvironmentManager manager = getByLevel(level);
		if (manager == null) {
			return InteractionResult.PASS;
		}
		return manager.environment.onUseItem(manager, player, interactionHand);
	}

	private static InteractionResult useBlockCallback(Player player, Level level, InteractionHand interactionHand, BlockHitResult blockHitResult) {
		EnvironmentManager manager = getByLevel(level);
		if (manager == null) {
			return InteractionResult.PASS;
		}
		return manager.environment.onUseBlock(manager, player, interactionHand, blockHitResult);
	}

	private static InteractionResult attackBlockCallback(Player player, Level level, InteractionHand interactionHand, BlockPos blockPos, Direction direction) {
		EnvironmentManager manager = getByLevel(level);
		if (manager == null) {
			return InteractionResult.PASS;
		}
		return manager.environment.onAttackBlock(manager, player, interactionHand, blockPos, direction);
	}

	private static boolean blockBreakEvent(Level level, Player player, BlockPos blockPos, BlockState state, @Nullable BlockEntity blockEntity) {
		EnvironmentManager manager = getByLevel(level);
		if (manager == null) {
			return true;
		}
		return manager.environment.onBreakBlock(manager, player, blockPos, state, blockEntity);
	}

	public static EnvironmentManager create(IEnvironment environment) {
		EnvironmentManager result = new EnvironmentManager(Objects.requireNonNull(environment));
		BY_LOC.put(result.level.dimension().location(), result);
		return result;
	}

	@Nullable
	public static EnvironmentManager getByLoc(ResourceLocation loc) {
		return BY_LOC.get(loc);
	}

	@Nullable
	public static EnvironmentManager getByLevel(Level level) {
		return getByLoc(level.dimension().location());
	}

	private final DynamicLevel level;
	private IEnvironment environment;

	private EnvironmentManager(IEnvironment environment) {
		this.level = new DynamicLevel(() -> environment.onEnd(this));
		this.environment = environment;
		environment.onStart(this);
	}

	public ServerLevel getLevel() {
		validateNotDeleted();
		return level;
	}

	public boolean isDeleted() {
		return environment == null;
	}

	public void delete() {
		validateNotDeleted();
		level.close();
		BY_LOC.remove(level.dimension().location());
		environment = null;
	}

	private void validateNotDeleted() {
		if (isDeleted()) {
			throw new IllegalStateException("Tried to use a deleted dynamic level " + level.dimension().location());
		}
	}
}
