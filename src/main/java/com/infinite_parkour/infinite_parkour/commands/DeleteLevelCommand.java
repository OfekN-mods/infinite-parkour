package com.infinite_parkour.infinite_parkour.commands;

import com.infinite_parkour.infinite_parkour.environment.Environment;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;

public class DeleteLevelCommand extends IPKCommand {
	public DeleteLevelCommand() {
		super("delete_level");
	}

	@Override
	public int run(CommandContext<CommandSourceStack> context, ServerPlayer player) {
		var manager = Environment.getByLevel(player.level());
		if (manager != null) {
			manager.delete();
			return Command.SINGLE_SUCCESS;
		}
		context.getSource().sendFailure(Component.literal("you're not in a dynamic level"));
		return 0;
	}
}
