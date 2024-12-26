package com.infinite_parkour.infinite_parkour.data;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import java.util.List;

public record JumpData(List<JumpBlockData> blocks, List<Long> trails, float weight) {
	public static final Codec<JumpData> CODEC = RecordCodecBuilder.create(instance -> instance.group(
			JumpBlockData.CODEC.listOf().fieldOf("blocks").forGetter(JumpData::blocks),
			Codec.LONG.listOf().fieldOf("trails").forGetter(JumpData::trails),
			Codec.FLOAT.fieldOf("weight").forGetter(JumpData::weight)
	).apply(instance, JumpData::new));
}
