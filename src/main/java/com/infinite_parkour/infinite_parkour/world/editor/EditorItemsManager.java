package com.infinite_parkour.infinite_parkour.world.editor;

import net.minecraft.core.component.DataComponents;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.BundleContents;

import java.util.Objects;

public class EditorItemsManager {
	private final Player player;
	private final Inventory inventory;

	public EditorItemsManager(Player player) {
		this.player = player;
		this.inventory = player.getInventory();
	}

	public void giveBundles() {
		int max = player.getPermissionLevel() >= 2 ? 10 : 9;
		for (int i = 0; i < max; i++) {
			giveBundle(i + 9, EditorItem.BUNDLES[i]);
		}
	}

	private void giveBundle(int slot, ItemStack stack) {
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
