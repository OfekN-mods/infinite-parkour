package com.infinite_parkour.infinite_parkour.world;

import com.google.common.collect.ImmutableList;
import com.infinite_parkour.infinite_parkour.InfiniteParkour;
import net.minecraft.Util;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.progress.ChunkProgressListener;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.chunk.status.ChunkStatus;
import net.minecraft.world.level.dimension.LevelStem;
import net.minecraft.world.level.storage.DerivedLevelData;
import net.minecraft.world.level.storage.LevelStorageSource;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.util.Files;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Objects;
import java.util.UUID;

public final class DynamicLevel extends ServerLevel {
	private static final Logger LOGGER = LogManager.getLogger();
	private static MinecraftServer currentServer;
	private static LevelStem stem;
	private static LevelStorageSource.LevelStorageAccess storageAccess;
	private static final ChunkProgressListener LISTENER = new ChunkProgressListener() {
		@Override public void updateSpawnPos(ChunkPos chunkPos) {}
		@Override public void onStatusChange(ChunkPos chunkPos, @Nullable ChunkStatus chunkStatus) {}
		@Override public void start() {}
		@Override public void stop() {}
	};

	public static void onStart(MinecraftServer server) {
		// currentServer
		currentServer = server;
		// stem
		RegistryAccess registryAccess = server.registryAccess();
		Registry<LevelStem> levelStemRegistry = registryAccess.lookupOrThrow(Registries.LEVEL_STEM);
		ResourceLocation id = InfiniteParkour.loc("lobby");
		stem = Objects.requireNonNull(levelStemRegistry.getValue(id));
		// storageAccess
		var source = LevelStorageSource.createDefault(Path.of("."));
		try {
			storageAccess = source.createAccess("temporary_worlds");
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	public static void onStop() {
		currentServer = null;
		stem = null;
		try {
			storageAccess.close();
			storageAccess = null;
		} catch (IOException e) {
			LOGGER.error("Failed to close storage access {}", storageAccess);
		}
		try {
			Files.deleteRecursively(new File("temporary_worlds"));
		} catch (IOException e) {
			LOGGER.error("Failed to delete temporary_worlds folder", e);
		}
	}

	private static MinecraftServer getCurrentServer() {
		if (currentServer == null) {
			throw new IllegalStateException("Can't create level when the server is off");
		}
		return currentServer;
	}

	private static ResourceKey<Level> generateKey() {
		UUID id = UUID.randomUUID();
		String name = id.toString().replace("-", "");
		ResourceLocation loc = ResourceLocation.fromNamespaceAndPath("dynamic", name);
		return ResourceKey.create(Registries.DIMENSION, loc);
	}

	private final Runnable onClose;

	public DynamicLevel(Runnable onClose) {
		super(
				getCurrentServer(),
				Util.backgroundExecutor(),
				storageAccess,
				new DerivedLevelData(currentServer.getWorldData(), currentServer.getWorldData().overworldData()),
				generateKey(),
				stem,
				LISTENER,
				false,
				0L,
				ImmutableList.of(),
				false,
				currentServer.overworld().getRandomSequences()
		);
		LOGGER.info("Created new level {}", dimension().location());
		currentServer.levels.put(dimension(), this);
		this.onClose = onClose;
	}

	@Override
	public void close() {
		ResourceKey<Level> key = dimension();
		ResourceLocation id = key.location();
		LOGGER.info("Closing level {}", id);
		onClose.run();
		try {
			super.close();
		} catch (IOException e) {
			LOGGER.error("Couldn't close level {}", id, e);
		}
		//noinspection resource
		getServer().levels.remove(key);
		try {
			String namespace = id.getNamespace();
			String path = id.getPath();
			Files.deleteRecursively(new File("temporary_worlds/dimensions/" + namespace + "/" + path));
		} catch (IOException e) {
			LOGGER.error("Couldn't delete level {}", id, e);
		}
	}
}
