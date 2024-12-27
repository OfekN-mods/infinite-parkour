package com.infinite_parkour.infinite_parkour.world;

import com.infinite_parkour.infinite_parkour.InfiniteParkour;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.Objects;

public final class IPKLevels {
	private static ServerLevel lobby;
	private static ServerLevel lanes;

	public static ServerLevel getLobby() {
		return lobby;
	}

	public static ServerLevel getLanes() {
		return lanes;
	}

	public static void teleportLobby(ServerPlayer player) {
		player.teleportTo(getLobby(), 0.5, 0, 0.5, Collections.emptySet(), 0, 0, true);
	}

	public static void teleportLobbyInit(ServerPlayer player, CompoundTag tag) {
		player.setUUID(player.getGameProfile().getId());
		player.setPos(0.5, 0, 0.5);
		player.setXRot(0);
		player.setYRot(0);
		tag.putString("Dimension", IPKLevels.getLobby().dimension().location().toString());

//		player.setRespawnPosition(getLobby().dimension(), BlockPos.ZERO, 0, true, false);
//		player.setServerLevel(getLobby());
//		player.setPos(0.5, 0, 0.5);
//		player.setXRot(0);
//		player.setYRot(0);
	}

	public static void onStart(MinecraftServer server) {
		lobby = load(server, "lobby");
		lanes = load(server, "lanes");
	}

	private static ServerLevel load(MinecraftServer server, String path) {
		ResourceLocation loc = InfiniteParkour.loc(path);
		ResourceKey<Level> key = ResourceKey.create(Registries.DIMENSION, loc);
		return Objects.requireNonNull(server.getLevel(key));
	}
}
