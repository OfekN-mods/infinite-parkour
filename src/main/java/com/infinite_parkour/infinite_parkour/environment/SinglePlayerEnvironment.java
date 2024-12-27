package com.infinite_parkour.infinite_parkour.environment;

import net.minecraft.server.level.ServerPlayer;

public abstract class SinglePlayerEnvironment extends BaseEnvironment {
	protected final ServerPlayer player;

	protected SinglePlayerEnvironment(ServerPlayer player) {
		this.player = player;
	}

	@Override
	public void onTick() {
		if (player.hasDisconnected() || player.level() != level) {
			delete();
		}
	}
}