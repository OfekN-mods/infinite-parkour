package com.infinite_parkour.infinite_parkour.commands;

import com.infinite_parkour.infinite_parkour.world.IPKLevels;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;

public class LobbyCommand extends IPKCommand {
	public LobbyCommand() {
		super("lobby");
	}

	@Override
	public int run(CommandContext<CommandSourceStack> context, ServerPlayer player) {
		IPKLevels.teleportLobby(player);
		context.getSource().sendSuccess(()-> Component.literal("teleported!"), false);
		return Command.SINGLE_SUCCESS;
	}
}
