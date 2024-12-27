package com.infinite_parkour.infinite_parkour.commands;

import com.infinite_parkour.infinite_parkour.environment.editor.EditorEnvironment;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;

public class EditorCommand extends IPKCommand {
	public EditorCommand() {
		super("editor");
	}

	@Override
	public int run(CommandContext<CommandSourceStack> context, ServerPlayer player) {
		new EditorEnvironment(player);
		context.getSource().sendSuccess(()-> Component.literal("teleported!"), false);
		return Command.SINGLE_SUCCESS;
	}
}
