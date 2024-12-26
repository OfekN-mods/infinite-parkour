package com.infinite_parkour.infinite_parkour.commands;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;

public abstract class IPKCommand implements Command<CommandSourceStack> {
	public final String name;

	protected IPKCommand(String name) {
		this.name = name;
	}

	@Override
	public int run(CommandContext<CommandSourceStack> context) {
		CommandSourceStack source = context.getSource();
		if (source.getEntity() instanceof ServerPlayer player) {
			return run(context, player);
		}
		source.sendFailure(Component.literal("you're not a player"));
		return 0;
	}

	public abstract int run(CommandContext<CommandSourceStack> context, ServerPlayer player);
}
