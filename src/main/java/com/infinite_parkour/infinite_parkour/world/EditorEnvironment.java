package com.infinite_parkour.infinite_parkour.world;

import com.infinite_parkour.infinite_parkour.InfiniteParkour;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.component.BundleContents;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.List;
import java.util.Objects;

public class EditorEnvironment extends BaseEnvironment {
	private static final ResourceLocation STRUCT_HOLOGRAM = InfiniteParkour.loc("editor_hologram_room");
	private int lastUsedSlot = -1;
	private ItemStack lastUsedItem = ItemStack.EMPTY;
	private int breakCooldown = 0;

	public EditorEnvironment(ServerPlayer player) {
		super(player);
	}

	@Override
	public void onStart(EnvironmentManager manager) {
		super.onStart(manager);
		BlockState blockBlack = Blocks.BLACK_CONCRETE.defaultBlockState();
		BlockState blockBlue = Blocks.LIGHT_BLUE_CONCRETE.defaultBlockState();
		BlockState blockWhite = Blocks.WHITE_CONCRETE.defaultBlockState();
		fill(blockWhite, -1, 0, 0, -1, 63, 63); // x=-1
		fill(blockWhite, 64, 0, 0, 64, 63, 63); // x=64
		fill(blockBlack, 0, -1, 0, 63, -1, 63); // y=-1
		fill(blockBlue, 0, 64, 0, 63, 64, 63);  // y=64
		fill(blockWhite, 0, 0, -1, 63, 63, -1); // z=-1
		fill(blockWhite, 0, 0, 64, 63, 63, 64); // z=64
		IPKUtils.placeStructure(level, new BlockPos(16, 31, -33), STRUCT_HOLOGRAM);
		respawn();
	}

	@Override
	public InteractionResult onUseItem(EnvironmentManager manager, Player player, InteractionHand interactionHand) {
		return InteractionResult.PASS;
	}

	@Override
	public InteractionResult onUseBlock(EnvironmentManager manager, Player player, InteractionHand interactionHand, BlockHitResult blockHitResult) {
		BlockPos pos = blockHitResult.getBlockPos().relative(blockHitResult.getDirection());
		return isInCanvas(pos) ? InteractionResult.PASS : InteractionResult.FAIL;
	}

	private static boolean isInCanvas(BlockPos pos) {
		int x = pos.getX();
		int y = pos.getY();
		int z = pos.getZ();
		return 0 <= x && x < 64 && 0 <= y && y < 64 && 0 <= z && z < 64;
	}

	@Override
	public InteractionResult onAttackBlock(EnvironmentManager manager, Player player, InteractionHand interactionHand, BlockPos blockPos, Direction direction) {
		return InteractionResult.PASS;
	}

	private void rememberSelected() {
		Inventory inventory = player.getInventory();
		lastUsedSlot = inventory.selected;
		lastUsedItem = inventory.getSelected().copy();
	}

	private static final ItemStack[] PLACEHOLDER = new ItemStack[12];
	static {
		for (int i = 0; i < PLACEHOLDER.length; i++) {
			ItemStack stack = new ItemStack(Items.STICK);
			stack.set(DataComponents.ITEM_MODEL, ResourceLocation.withDefaultNamespace("air"));
			stack.set(DataComponents.ITEM_NAME, Component.literal("" + i));
			PLACEHOLDER[i] = stack;
		}
	}

	private static ItemStack createBundle(Item base, ItemStack ... content) {
		ItemStack stack = new ItemStack(base);
		stack.set(DataComponents.BUNDLE_CONTENTS, new BundleContents(List.of(content)));
		return stack;
	}

	private static final ItemStack[] BUNDLES = {
			createBundle(
					Items.GREEN_BUNDLE,
					new ItemStack(Items.STONE),
					new ItemStack(Items.TUFF),
					PLACEHOLDER[2],
					PLACEHOLDER[3],
					PLACEHOLDER[4],
					PLACEHOLDER[5],
					PLACEHOLDER[6],
					PLACEHOLDER[7],
					PLACEHOLDER[8],
					PLACEHOLDER[9],
					PLACEHOLDER[10],
					PLACEHOLDER[11]
			),
			createBundle(
					Items.YELLOW_BUNDLE,
					new ItemStack(Items.GOLD_NUGGET),
					new ItemStack(Items.EMERALD),
					PLACEHOLDER[2],
					PLACEHOLDER[3],
					PLACEHOLDER[4],
					PLACEHOLDER[5],
					PLACEHOLDER[6],
					PLACEHOLDER[7],
					PLACEHOLDER[8],
					PLACEHOLDER[9],
					PLACEHOLDER[10],
					PLACEHOLDER[11]
			),
			createBundle(
					Items.RED_BUNDLE,
					new ItemStack(Items.LADDER),
					new ItemStack(Items.SLIME_BLOCK),
					new ItemStack(Items.HONEY_BLOCK),
					PLACEHOLDER[3],
					PLACEHOLDER[4],
					PLACEHOLDER[5],
					PLACEHOLDER[6],
					PLACEHOLDER[7],
					PLACEHOLDER[8],
					PLACEHOLDER[9],
					PLACEHOLDER[10],
					PLACEHOLDER[11]
			),
			createBundle(Items.WHITE_BUNDLE, PLACEHOLDER),
			createBundle(Items.WHITE_BUNDLE, PLACEHOLDER),
			createBundle(Items.WHITE_BUNDLE, PLACEHOLDER),
			createBundle(Items.WHITE_BUNDLE, PLACEHOLDER),
			createBundle(Items.WHITE_BUNDLE, PLACEHOLDER),
			createBundle(
					Items.RED_BUNDLE,
					new ItemStack(Items.REDSTONE),
					PLACEHOLDER[1],
					PLACEHOLDER[2],
					PLACEHOLDER[3],
					PLACEHOLDER[4],
					PLACEHOLDER[5],
					PLACEHOLDER[6],
					PLACEHOLDER[7],
					PLACEHOLDER[8],
					PLACEHOLDER[9],
					PLACEHOLDER[10],
					PLACEHOLDER[11]
			)
	};

	@Override
	public boolean onTick(EnvironmentManager manager) {
		if (super.onTick(manager)) {
			return true;
		}
		if (breakCooldown > 0) {
			breakCooldown--;
		} else if (player.gameMode.isDestroyingBlock) {
			BlockPos pos = player.gameMode.destroyPos;
			if (isInCanvas(pos)) {
				level.setBlockAndUpdate(pos, Blocks.AIR.defaultBlockState());
				breakCooldown = 4;
			}
		}

		player.getAbilities().mayfly = true;
		player.getAbilities().instabuild = true;
		player.onUpdateAbilities();
		player.getAttributes().getInstance(Attributes.BLOCK_BREAK_SPEED).setBaseValue(0);
		player.getAttributes().getInstance(Attributes.BLOCK_INTERACTION_RANGE).setBaseValue(10);

		if (lastUsedSlot >= 0) {
			player.getInventory().setItem(lastUsedSlot, lastUsedItem);
			lastUsedSlot = -1;
			lastUsedItem = ItemStack.EMPTY;
		}
		if (player.getY() <= 0.01 && player.onGround()) {
			respawn();
		}
		for (int i = 0; i < 9; i++) {
			giveBundle(i + 9, BUNDLES[i]);
		}
		return false;
	}

	public void giveBundle(int slot, ItemStack stack) {
		var inventory = player.getInventory();
		ItemStack oldStack = inventory.getItem(slot);
		if (!compareBundles(oldStack, stack)) {
			inventory.setItem(slot, stack.copy());
		}
	}

	@Override
	public boolean onBreakBlock(EnvironmentManager manager, Player player, BlockPos blockPos, BlockState state, @Nullable BlockEntity blockEntity) {
		return false;
	}

	private static boolean compareBundles(ItemStack a, ItemStack b) {
		if (a.getItem() != b.getItem()) {
			return false;
		}
		BundleContents contentA = a.getComponents().get(DataComponents.BUNDLE_CONTENTS);
		BundleContents contentB = b.getComponents().get(DataComponents.BUNDLE_CONTENTS);
		return Objects.equals(contentA, contentB);
	}

	private void respawn() {
		player.teleportTo(level, 31.5, 32.0, -4.5, Collections.emptySet(), 0, 0, true);
	}

	private void fill(BlockState state, int x0, int y0, int z0, int x1, int y1, int z1) {
		BlockPos.MutableBlockPos pos = new BlockPos.MutableBlockPos(0, 0, 0);
		for (int y = y0; y <= y1; y++) {
			pos.setY(y);
			for (int x = x0; x <= x1; x++) {
				pos.setX(x);
				for (int z = z0; z <= z1; z++) {
					pos.setZ(z);
					level.setBlock(pos, state, 0);
				}
			}
		}
	}
}
