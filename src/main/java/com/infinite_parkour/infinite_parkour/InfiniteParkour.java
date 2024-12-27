package com.infinite_parkour.infinite_parkour;

import com.infinite_parkour.infinite_parkour.commands.CommandsRegister;
import com.infinite_parkour.infinite_parkour.environment.Environment;
import com.infinite_parkour.infinite_parkour.world.DynamicLevel;
import com.infinite_parkour.infinite_parkour.world.IPKLevels;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
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
		Environment.registerEvents();
	}

	public static ResourceLocation loc(String path) {
		return ResourceLocation.fromNamespaceAndPath(MODID, path);
	}

	private void onStart(MinecraftServer server) {
		IPKLevels.onStart(server);
		DynamicLevel.onStart(server);
	}

	private void onTick(MinecraftServer server) {
		Environment.callTickFunctions();
	}

	private void onStop(MinecraftServer server) {
		Environment.cleanup();
		DynamicLevel.onStop();
	}
}
