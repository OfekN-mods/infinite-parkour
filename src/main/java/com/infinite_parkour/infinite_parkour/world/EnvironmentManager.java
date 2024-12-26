package com.infinite_parkour.infinite_parkour.world;

import com.google.common.collect.ImmutableList;
import com.infinite_parkour.infinite_parkour.InfiniteParkour;
import net.fabricmc.fabric.api.event.player.*;
import net.minecraft.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.progress.ChunkProgressListener;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.RandomSequences;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.status.ChunkStatus;
import net.minecraft.world.level.dimension.LevelStem;
import net.minecraft.world.level.storage.DerivedLevelData;
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
import java.util.UUID;

public final class EnvironmentManager {
	private static MinecraftServer currentServer;
	private static LevelStorageSource.LevelStorageAccess storageAccess;
	private static final Map<UUID, EnvironmentManager> BY_ID = new HashMap<>();
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
		EnvironmentManager[] managers = BY_ID.values().toArray(EnvironmentManager[]::new);
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
		BY_ID.clear();
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
		if (currentServer == null) {
			throw new IllegalStateException("Can't create level when the server is off");
		}
		EnvironmentManager result = new EnvironmentManager(currentServer, Objects.requireNonNull(environment));
		BY_ID.put(result.id, result);
		BY_LOC.put(result.loc, result);
		return result;
	}

	@Nullable
	public static EnvironmentManager getById(UUID id) {
		return BY_ID.get(id);
	}

	@Nullable
	public static EnvironmentManager getByLoc(ResourceLocation loc) {
		return BY_LOC.get(loc);
	}

	@Nullable
	public static EnvironmentManager getByLevel(Level level) {
		return getByLoc(level.dimension().location());
	}

	private final MinecraftServer server;
	private final UUID id;
	private final ResourceLocation loc;
	private final ResourceKey<Level> key;
	private final ServerLevel level;
	private IEnvironment environment;

	private EnvironmentManager(MinecraftServer server, IEnvironment environment) {
		this.server = server;
		this.id = UUID.randomUUID();
		this.loc = InfiniteParkour.loc("env_" + id.toString().replace("-", ""));
		this.key = ResourceKey.create(Registries.DIMENSION, loc);
		this.level = createLevel(server, key);
		this.environment = environment;
		environment.onStart(this);
	}

	public MinecraftServer getServer() {
		return server;
	}

	public UUID getId() {
		return id;
	}

	public ResourceLocation getLoc() {
		return loc;
	}

	public ResourceKey<Level> getKey() {
		return key;
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
		BY_ID.remove(id);
		BY_LOC.remove(loc);

		environment.onEnd(this);
		//noinspection resource
		server.levels.remove(key);
		environment = null;
		try {
			level.close();
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
		try {
			String namespace = key.location().getNamespace();
			String path = key.location().getPath();
			Files.deleteRecursively(new File("tempworlds/dimensions/" + namespace + "/" + path));
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	private void validateNotDeleted() {
		if (isDeleted()) {
			throw new IllegalStateException("Tried to use a deleted dynamic level " + id);
		}
	}

	private static ServerLevel createLevel(MinecraftServer server, ResourceKey<Level> key) {
		DerivedLevelData derivedLevelData = new DerivedLevelData(server.getWorldData(), server.getWorldData().overworldData());
		RandomSequences randomSequences = server.overworld().getRandomSequences();
		ServerLevel newLevel = new ServerLevel(
				server,
				Util.backgroundExecutor(),
				storageAccess,
				derivedLevelData,
				key,
				getStem(server),
				chunkProcessListener(),
				false,
				0L,
				ImmutableList.of(),
				false,
				randomSequences
		) {
			@Override
			public void blockUpdated(BlockPos blockPos, Block block) {
//				super.blockUpdated(blockPos, block);
			}
		};
		server.levels.put(key, newLevel);
		return newLevel;
	}

	private static LevelStem getStem(MinecraftServer server) {
		RegistryAccess registryAccess = server.registryAccess();
		Registry<LevelStem> levelStemRegistry = registryAccess.lookupOrThrow(Registries.LEVEL_STEM);
		ResourceLocation id = InfiniteParkour.loc("lobby");
		return Objects.requireNonNull(levelStemRegistry.getValue(id));
	}


	private static ChunkProgressListener chunkProcessListener() {
		return new ChunkProgressListener() {
			@Override
			public void updateSpawnPos(ChunkPos chunkPos) {}

			@Override
			public void onStatusChange(ChunkPos chunkPos, @Nullable ChunkStatus chunkStatus) {}

			@Override
			public void start() {}

			@Override
			public void stop() {}
		};
	}
}
