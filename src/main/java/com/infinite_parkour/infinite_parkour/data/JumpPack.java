package com.infinite_parkour.infinite_parkour.data;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.UUIDUtil;

import java.util.List;
import java.util.UUID;

public record JumpPack(UUID id, List<JumpData> jumps, UUID owner, String name) {
	public static final Codec<JumpPack> CODEC = RecordCodecBuilder.create(instance -> instance.group(
			UUIDUtil.CODEC.fieldOf("id").forGetter(JumpPack::id),
			JumpData.CODEC.listOf().fieldOf("jumps").forGetter(JumpPack::jumps),
			UUIDUtil.CODEC.fieldOf("owner").forGetter(JumpPack::owner),
			Codec.STRING.fieldOf("name").forGetter(JumpPack::name)
	).apply(instance, JumpPack::new));
}
