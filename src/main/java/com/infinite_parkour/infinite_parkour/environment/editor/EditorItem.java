package com.infinite_parkour.infinite_parkour.environment.editor;

import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.component.BundleContents;
import net.minecraft.world.item.component.ItemLore;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

public enum EditorItem {
	PLATFORM(Items.STONE, false, "Platform", "Simple blocks the player will jump on"),
	BLOCKER(Items.TUFF, false, "Blocker", "Blocks to stop the player"),
	STAIRS(Items.OAK_STAIRS),
	SLABS(Items.OAK_SLAB),
	FENCE(Items.OAK_FENCE),
	WALL(Items.COBBLESTONE_WALL),
	BARS(Items.IRON_BARS),
	CHAIN(Items.CHAIN),
	IRON_TRAPDOOR(Items.IRON_TRAPDOOR),
	CARPET(Items.WHITE_CARPET),
	HANGING_SIGN(Items.OAK_HANGING_SIGN),
	HEAD(Items.PLAYER_HEAD),
	LADDER(Items.LADDER),
	HONEY(Items.HONEY_BLOCK),
	SLIME(Items.SLIME_BLOCK),
	PACKED_ICE(Items.PACKED_ICE),
	BLUE_ICE(Items.BLUE_ICE),
	SOUL_SAND(Items.SOUL_SAND),
	POWDER_SNOW(Items.POWDER_SNOW_BUCKET),
	SCAFFOLDING(Items.SCAFFOLDING),
	TRAPDOOR(Items.SPRUCE_TRAPDOOR),
	TRAIL(Items.REDSTONE, true, "Trail", "Connect two blocks with a trail"),
	PICKUP0(Items.GOLD_NUGGET, true, "Simple Pickup"),
	PICKUP1(Items.EMERALD, true, "Advanced Pickup");

	public final ItemStack stack;

	EditorItem(Item item, boolean tool, @Nullable String name, String... lore) {
		if (tool) {
			stack = new ItemStack(Items.STICK);
			stack.set(DataComponents.ITEM_MODEL, item.components().get(DataComponents.ITEM_MODEL));
		} else {
			stack = new ItemStack(item);
		}
		stack.set(DataComponents.MAX_STACK_SIZE, 1);
		if (name != null) {
			stack.set(DataComponents.ITEM_NAME, Component.literal(name));
		}
		if (lore.length > 0) {
			stack.set(DataComponents.LORE, new ItemLore(Stream.of(lore)
					.map(Component::literal)
					.map(comp -> (Component) comp.withStyle(style -> style.withColor(0xAAAAAA).withItalic(false)))
					.toList()
			));
		}
	}

	EditorItem(Item item) {
		this(item, false, null);
	}

	public static final ItemStack[] BUNDLES;
	static {
		ItemStack[] placeholders = new ItemStack[12];
		for (int i = 0; i < placeholders.length; i++) {
			ItemStack stack = new ItemStack(Items.STICK);
			stack.set(DataComponents.ITEM_MODEL, ResourceLocation.withDefaultNamespace("air"));
			stack.set(DataComponents.ITEM_NAME, Component.literal("" + i));
			stack.set(DataComponents.MAX_STACK_SIZE, 1);
			placeholders[i] = stack;
		}


		EditorItem[] values = EditorItem.values();
		List<ItemStack> bundles = new ArrayList<>();
		int i = 0;
		for (EditorCategory category : EditorCategory.values()) {
			int lastToolI = category.lastTool.ordinal();
			List<ItemStack> stacks = new ArrayList<>(lastToolI - i + 1);
			while (i <= lastToolI) {
				stacks.add(values[i++].stack);
			}
			while (stacks.size() < placeholders.length) {
				stacks.add(placeholders[stacks.size()]);
			}
			category.stack.set(DataComponents.BUNDLE_CONTENTS, new BundleContents(stacks));
			bundles.add(category.stack);
		}
		BUNDLES = bundles.toArray(ItemStack[]::new);
	}

	private enum EditorCategory {
		SOLID_BLOCKS(Items.GREEN_BUNDLE, "Solid Blocks", BLOCKER),
		SHAPED_BLOCKS(Items.BROWN_BUNDLE, "Shaped Blocks", HEAD),
		EFFECT_BLOCKS(Items.CYAN_BUNDLE, "Effect Blocks", SCAFFOLDING),
		INTERACTABLE_BLOCKS(Items.RED_BUNDLE, "Interactable Blocks", TRAPDOOR),
		TEMP4(Items.WHITE_BUNDLE, "Future Category", TRAPDOOR),
		TEMP5(Items.WHITE_BUNDLE, "Future Category", TRAPDOOR),
		TEMP6(Items.WHITE_BUNDLE, "Future Category", TRAPDOOR),
		TEMP7(Items.WHITE_BUNDLE, "Future Category", TRAPDOOR),
		TOOLS(Items.PINK_BUNDLE, "Tools", TRAIL),
		DEV_TOOLS(Items.YELLOW_BUNDLE, "Dev Tools", PICKUP1);

		public final ItemStack stack;
		private final EditorItem lastTool;

		EditorCategory(Item bundle, String name, EditorItem lastTool) {
			this.stack = new ItemStack(bundle);
			this.stack.set(DataComponents.ITEM_NAME, Component.literal(name));
			this.lastTool = lastTool;
		}
	}
}
