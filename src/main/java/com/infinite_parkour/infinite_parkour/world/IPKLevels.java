package com.infinite_parkour.infinite_parkour.world;

import com.infinite_parkour.infinite_parkour.InfiniteParkour;
import net.fabricmc.fabric.api.event.player.UseEntityCallback;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;

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
