package com.infinite_parkour.infinite_parkour.environment.editor;

import net.minecraft.core.component.DataComponents;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.BundleContents;

import java.util.Objects;

public class EditorItemsManager {
	public static void giveBundles(ServerPlayer player) {
		Inventory inventory = player.getInventory();
		int max = player.getPermissionLevel() >= 2 ? 10 : 9;
		for (int i = 0; i < max; i++) {
			giveBundle(inventory, i + 9, EditorItem.BUNDLES[i]);
		}
	}

	private static void giveBundle(Inventory inventory, int slot, ItemStack stack) {
		ItemStack oldStack = inventory.getItem(slot);
		if (!compareBundles(oldStack, stack)) {
			inventory.setItem(slot, stack.copy());
		}
	}

	private static boolean compareBundles(ItemStack a, ItemStack b) {
		if (a.getItem() != b.getItem()) {
			return false;
		}
		BundleContents contentA = a.getComponents().get(DataComponents.BUNDLE_CONTENTS);
		BundleContents contentB = b.getComponents().get(DataComponents.BUNDLE_CONTENTS);
		return Objects.equals(contentA, contentB);
	}
}
