package com.infinite_parkour.infinite_parkour.commands;

import com.infinite_parkour.infinite_parkour.world.EnvironmentManager;
import com.infinite_parkour.infinite_parkour.world.IEnvironment;
import com.infinite_parkour.infinite_parkour.world.IPKLevels;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;

import java.util.Collections;

public class TestCommand extends IPKCommand {
	public TestCommand() {
		super("test");
	}

	@Override
	public int run(CommandContext<CommandSourceStack> context, ServerPlayer player) {
		var manager = EnvironmentManager.getByLevel(player.level());
		if (manager != null) {
			IPKLevels.teleportLobby(player);
			manager.delete();
			return Command.SINGLE_SUCCESS;
		}

		manager = EnvironmentManager.create(new IEnvironment() {});
		player.teleportTo(manager.getLevel(), 0, 0, 0, Collections.emptySet(), 0, 0, true);
		return Command.SINGLE_SUCCESS;
	}
}
