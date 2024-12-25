package com.infinite_parkour.infinite_parkour.commands;

import com.infinite_parkour.infinite_parkour.world.EnvironmentManager;
import com.infinite_parkour.infinite_parkour.world.IEnvironment;
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
	public int run(CommandContext<CommandSourceStack> context) {
		CommandSourceStack source = context.getSource();
		if (source.getEntity() instanceof ServerPlayer player) {
			run(player);
			return Command.SINGLE_SUCCESS;
		}
		source.sendFailure(Component.literal("you're not a player"));
		return 0;
	}

	private void run(ServerPlayer player) {
		var manager = EnvironmentManager.getByLevel(player.level());
		if (manager != null) {
			player.teleportTo(player.server.overworld(), 0, 68, 0, Collections.emptySet(), 0, 0, true);
			manager.delete();
			return;
		}

		manager = EnvironmentManager.create(new IEnvironment() {});
		player.teleportTo(manager.getLevel(), 0, 0, 0, Collections.emptySet(), 0, 0, true);
	}
}
