package io.github.haykam821.stash.network;

import io.github.haykam821.stash.component.StashComponent;
import io.github.haykam821.stash.component.StashComponentInitializer;
import io.github.haykam821.stash.network.packet.ExtractStashC2SPayload;
import io.github.haykam821.stash.network.packet.InsertStashC2SPayload;
import io.github.haykam821.stash.network.packet.OpenStashS2CPayload;
import io.github.haykam821.stash.network.packet.SetStashSortC2SPayload;
import io.github.haykam821.stash.ui.StashUi;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.PlayerScreenHandler;
import net.minecraft.server.network.ServerPlayerEntity;

public final class StashNetworking {
	private StashNetworking() {
		return;
	}

	public static void register() {
		PayloadTypeRegistry.playC2S().register(ExtractStashC2SPayload.ID, ExtractStashC2SPayload.CODEC);
		PayloadTypeRegistry.playC2S().register(InsertStashC2SPayload.ID, InsertStashC2SPayload.CODEC);
		PayloadTypeRegistry.playC2S().register(SetStashSortC2SPayload.ID, SetStashSortC2SPayload.CODEC);

		PayloadTypeRegistry.playS2C().register(OpenStashS2CPayload.ID, OpenStashS2CPayload.CODEC);

		ServerPlayNetworking.registerGlobalReceiver(ExtractStashC2SPayload.ID, (payload, context) -> {
			ServerPlayerEntity player = context.player();

			if (player.currentScreenHandler instanceof PlayerScreenHandler screenHandler) {
				StashComponent stash = StashComponentInitializer.STASH.get(player);

				ItemStack stack = extract(player, stash, screenHandler.getCursorStack(), payload.item(), payload.all(), payload.drop());
				screenHandler.setCursorStack(stack);
			}
		});

		ServerPlayNetworking.registerGlobalReceiver(InsertStashC2SPayload.ID, (payload, context) -> {
			ServerPlayerEntity player = context.player();

			if (player.currentScreenHandler instanceof PlayerScreenHandler screenHandler) {
				StashComponent stash = StashComponentInitializer.STASH.get(player);

				ItemStack stack = insert(stash, screenHandler.getCursorStack(), payload.all());
				screenHandler.setCursorStack(stack);
			}
		});

		ServerPlayNetworking.registerGlobalReceiver(SetStashSortC2SPayload.ID, (payload, context) -> {
			ServerPlayerEntity player = context.player();
			StashComponent stash = StashComponentInitializer.STASH.get(player);

			stash.setSort(payload.sort());
		});
	}

	public static ItemStack extract(PlayerEntity player, StashComponent stash, ItemStack cursorStack, Item item, boolean all, boolean drop) {
		int maxCount = all ? item.getMaxCount() : 1;

		if (drop) {
			player.dropItem(stash.extractStack(item, maxCount), true);
		} else if (cursorStack.isEmpty()) {
			return stash.extractStack(item, maxCount);
		} else if (cursorStack.isOf(item) && StashComponent.isInsertable(cursorStack)) {
			int count = Math.min(maxCount, cursorStack.getMaxCount() - cursorStack.getCount());

			ItemStack extractedStack = stash.extractStack(item, count);
			extractedStack.setCount(cursorStack.getCount() + extractedStack.getCount());

			return extractedStack;
		}

		return cursorStack;
	}

	public static ItemStack insert(StashComponent stash, ItemStack cursorStack, boolean all) {
		ItemStack stack = all ? cursorStack : cursorStack.copyWithCount(1);

		if (stash.insertStack(stack)) {
			if (all) {
				return ItemStack.EMPTY;
			} else {
				ItemStack remainderStack = cursorStack.copy();
				remainderStack.decrement(1);

				return remainderStack;
			}
		}

		return cursorStack;
	}

	public static void openScreen(ServerPlayerEntity player) {
		if (!ServerPlayNetworking.canSend(player, OpenStashS2CPayload.ID)) {
			ServerPlayNetworking.send(player, OpenStashS2CPayload.INSTANCE);
		} else {
			StashUi ui = new StashUi(player);
			ui.open();
		}
	}
}
