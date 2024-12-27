package com.infinite_parkour.infinite_parkour;

import com.infinite_parkour.infinite_parkour.commands.CommandsRegister;
import com.infinite_parkour.infinite_parkour.world.EnvironmentManager;
import com.infinite_parkour.infinite_parkour.world.IPKLevels;
import com.infinite_parkour.infinite_parkour.world.DynamicLevel;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;

public class InfiniteParkour implements ModInitializer {
	public static final String MODID = "infinite_parkour";


	@Override
	public void onInitialize() {
		CommandRegistrationCallback.EVENT.register(CommandsRegister::register);
		ServerLifecycleEvents.SERVER_STARTED.register(this::onStart);
		ServerTickEvents.END_SERVER_TICK.register(this::onTick);
		ServerLifecycleEvents.SERVER_STOPPED.register(this::onStop);
//		ServerPlayConnectionEvents.JOIN.register((handler, sender, server) -> IPKLevels.teleportLobbyInit(handler.player));
//		ServerPlayConnectionEvents.INIT.register((handler, server) -> IPKLevels.teleportLobbyInit(handler.player));
		EnvironmentManager.registerEvents();
	}

	public static ResourceLocation loc(String path) {
		return ResourceLocation.fromNamespaceAndPath(MODID, path);
	}

	private void onStart(MinecraftServer server) {
		IPKLevels.onStart(server);
		DynamicLevel.onStart(server);
		EnvironmentManager.onStart(server);
	}

	private void onTick(MinecraftServer server) {
		EnvironmentManager.onTick();
	}

	private void onStop(MinecraftServer server) {
		EnvironmentManager.onStop();
		DynamicLevel.onStop();
	}
}
