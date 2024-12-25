package com.infinite_parkour.infinite_parkour.commands;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;

public class RegenerateChunkCommand extends IPKCommand {
	public RegenerateChunkCommand() {
		super("heal");
	}

	@Override
	public int run(CommandContext<CommandSourceStack> context) {
		CommandSourceStack source = context.getSource();
		if (source.getEntity() instanceof Player player) {
			player.heal(10000);
			source.sendSuccess(()-> Component.literal("healed!"), false);
			return Command.SINGLE_SUCCESS;
		}
		source.sendFailure(Component.literal("you're not a player"));
		return 0;
	}
}
