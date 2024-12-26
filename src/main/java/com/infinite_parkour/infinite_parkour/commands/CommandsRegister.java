package com.infinite_parkour.infinite_parkour.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;

public final class CommandsRegister {
	private CommandsRegister() {}

	private static final IPKCommand[] COMMANDS = {
			new LobbyCommand(),
			new FreeplayCommand(),
			new EditorCommand(),
			new TestCommand(),
	};

	public static void register(CommandDispatcher<CommandSourceStack> dispatcher, CommandBuildContext context, Commands.CommandSelection selection) {
		LiteralArgumentBuilder<CommandSourceStack> builder = LiteralArgumentBuilder.literal("ipk");
		builder.requires(source -> source.hasPermission(2));
		for (IPKCommand command : COMMANDS) {
			builder.then(Commands.literal(command.name).executes(command));
		}
		dispatcher.register(builder);
	}
}
