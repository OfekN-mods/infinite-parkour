package com.infinite_parkour.infinite_parkour.commands;

import com.mojang.brigadier.Command;
import net.minecraft.commands.CommandSourceStack;

public abstract class IPKCommand implements Command<CommandSourceStack> {
	public final String name;

	protected IPKCommand(String name) {
		this.name = name;
	}
}
