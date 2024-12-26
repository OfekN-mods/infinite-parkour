package com.infinite_parkour.infinite_parkour.data;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.world.level.block.state.BlockState;

public record JumpBlockData(int pos, BlockState state) {
	public static final Codec<JumpBlockData> CODEC = RecordCodecBuilder.create(instance -> instance.group(
			Codec.INT.fieldOf("pos").forGetter(JumpBlockData::pos),
			BlockState.CODEC.fieldOf("state").forGetter(JumpBlockData::state)
	).apply(instance, JumpBlockData::new));
}
