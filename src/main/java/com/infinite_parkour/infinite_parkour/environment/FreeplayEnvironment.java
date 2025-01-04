package com.infinite_parkour.infinite_parkour.environment;

import com.infinite_parkour.infinite_parkour.InfiniteParkour;
import com.infinite_parkour.infinite_parkour.environment.editor.EditorEnvironment;
import com.infinite_parkour.infinite_parkour.IPKUtils;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.ClickEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.HoverEvent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;

public class FreeplayEnvironment extends BaseEnvironment {
	private static final ResourceLocation STRUCT_TOP = InfiniteParkour.loc("freeplay_top");
	private static final ResourceLocation STRUCT_BOT = InfiniteParkour.loc("freeplay_bottom");

	public FreeplayEnvironment(ServerPlayer player) {
		IPKUtils.placeStructure(level, new BlockPos(-15, -11, -15), STRUCT_TOP);
		IPKUtils.placeStructure(level, new BlockPos(-15, -42, -15), STRUCT_BOT);
		player.teleportTo(level, 0.5, 0, 0.5, Collections.emptySet(), 0, 0, true);

		//settings
		/*
		summon text_display ~-6.49 3.2 0.5 {text:'{"color":"yellow","text":"Settings"}',transformation:{translation:[0,0,0],scale:[2,2,2],left_rotation:[0,0,0,1],right_rotation:{angle:1.57079,axis:[0,1,0]}}}
	  summon text_display ~-6.49 2.5 0.5 {text:'{"color":"white","text":"Decorations"}',transformation:{translation:[0,0,0],scale:[1,1,1],left_rotation:[0,0,0,1],right_rotation:{angle:1.57079,axis:[0,1,0]}}}
	  summon text_display ~-6.49 2.2 0.5 {text:'{"color":"red","text":"off"}',transformation:{translation:[0,0,0],scale:[1,1,1],left_rotation:[0,0,0,1],right_rotation:{angle:1.57079,axis:[0,1,0]}}}
	  summon interaction ~-6.6 2.2 0.5 {width:0.4,height:0.25}
	  summon text_display ~-6.49 1.6 0.5 {text:'{"color":"white","text":"JumpPack"}',transformation:{translation:[0,0,0],scale:[1,1,1],left_rotation:[0,0,0,1],right_rotation:{angle:1.57079,axis:[0,1,0]}}}
	  summon text_display ~-6.49 1.3 0.5 {text:'{"color":"blue","text":"loading..."}',transformation:{translation:[0,0,0],scale:[1,1,1],left_rotation:[0,0,0,1],right_rotation:{angle:1.57079,axis:[0,1,0]}}}
	  summon text_display ~-6.49 1.3 2.0 {text:'{"color":"green","text":"\\u2190"}',transformation:{translation:[0,0,0],scale:[1,1,1],left_rotation:[0,0,0,1],right_rotation:{angle:1.57079,axis:[0,1,0]}}}
	  summon text_display ~-6.49 1.3 -1.0 {text:'{"color":"green","text":"\\u2192"}',transformation:{translation:[0,0,0],scale:[1,1,1],left_rotation:[0,0,0,1],right_rotation:{angle:1.57079,axis:[0,1,0]}}}
	  summon interaction ~-6.6 1.3 2.0 {width:0.4,height:0.25}
	  summon interaction ~-6.6 1.3 -1.0 {width:0.4,height:0.25}
		 */
		// editor
		createInteraction(13.5, 0.0, 0.5, 2.0f, 2.0f, this::teleportEditor);
		createTextDisplay(13.5, 1.6, 0.5, Component.empty()
				.append(Component.literal("Teleport to the ").withStyle(ChatFormatting.AQUA))
				.append(Component.literal("Editor").withStyle(ChatFormatting.DARK_AQUA))
		);
		// credits
		createTextDisplay(-2.5, 1.6, -5.5, Component.literal("Youtube Channel").withStyle(ChatFormatting.RED));
		createInteraction(-2.5, 0.0, -5.5, 1.1f, 2.0f, this::showYoutube);
		createTextDisplay(3.5, 1.6, -5.5, Component.literal("Discord Server").withColor(0x5662F6));
		createInteraction(3.5, 0.0, -5.5, 1.1f, 2.0f, this::showDiscord);
		createTextDisplay(-0.5, 1.6, -7.5, Component.literal("OfekN").withStyle(ChatFormatting.GREEN));
		createTextDisplay(1.5, 1.6, -7.5, Component.literal("Big_Con__").withStyle(ChatFormatting.GREEN));
		// builders
		createBuilder(-6.5,4.5, 315, "Join us!", "on discord", 0x5662F6);
		createBuilder(-4.5,3.5, 0, "Join us!", "on discord", 0x5662F6);
		createBuilder(-4.5,7.5, 180, "Join us!", "on discord", 0x5662F6);
		createBuilder(-2.5,3.5, 0, "lags_kills", "Suggestor", 0xebd68f);
		createBuilder(-0.5,5.5, 90, "Flaming_Thunder_", "Head Builder", 0xeb8f8f);
		createBuilder(-2.5,7.5, 180, "2s2s", "Build Helper", 0xebe58f);
	}

	@Override
	public boolean onBreakBlock(Player player, BlockPos blockPos, BlockState state, @Nullable BlockEntity blockEntity) {
		return false;
	}

	private void createBuilder(double x, double z, float yRot, String name, String title, int color) {
		createTextDisplay(x, -5.2, z, yRot, Component.literal(name).withColor(color));
		createTextDisplay(x, -5.45, z, yRot, Component.literal(title).withStyle(ChatFormatting.GRAY));
		createInteraction(x, -7.0, z, 1.1f, 2.0f, name.equals("Join us!") ?
				this::showDiscord :
				player -> player.displayClientMessage(Component.literal("Thanks to " + name), false)
		);
	}

	public static final HoverEvent HOVER_CLICK = new HoverEvent(HoverEvent.Action.SHOW_TEXT, Component.literal("Click").withStyle(ChatFormatting.GRAY, ChatFormatting.BOLD));
	public static final Component YOUTUBE_COMPONENT = Component.literal("Click here to visit the BigConGaming's youtube channel")
			.withStyle(style -> style
					.withUnderlined(true)
					.withColor(0xFF0000)
					.withClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL, "https://www.youtube.com/@bigcongaming"))
					.withHoverEvent(HOVER_CLICK)
			);
	public static final Component DISCORD_COMPONENT = Component.literal("Click here to join the discord server")
			.withStyle(style -> style
					.withUnderlined(true)
					.withColor(0x5662F6)
					.withClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL, "https://discord.gg/dnNu2xHWsQ"))
					.withHoverEvent(HOVER_CLICK)
			);

	private void showYoutube(ServerPlayer player) {
		player.displayClientMessage(YOUTUBE_COMPONENT, false);

	}

	private void showDiscord(ServerPlayer player) {
		player.displayClientMessage(DISCORD_COMPONENT, false);
	}

	private void teleportEditor(ServerPlayer player) {
		delete();
		new EditorEnvironment(player);
	}
}
