package com.infinite_parkour.infinite_parkour.commands;

import com.infinite_parkour.infinite_parkour.world.EnvironmentManager;
import com.infinite_parkour.infinite_parkour.world.FreeplayEnvironment;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;

public class FreeplayCommand extends IPKCommand {
	protected FreeplayCommand() {
		super("freeplay");
	}

	@Override
	public int run(CommandContext<CommandSourceStack> context, ServerPlayer player) {
		EnvironmentManager.create(new FreeplayEnvironment(player));
		context.getSource().sendSuccess(()-> Component.literal("teleported!"), false);
		return Command.SINGLE_SUCCESS;
	}
}
