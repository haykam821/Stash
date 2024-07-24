package io.github.haykam821.stash.client.network;

import io.github.haykam821.stash.client.screen.StashScreen;
import io.github.haykam821.stash.component.StashComponent;
import io.github.haykam821.stash.component.StashComponentInitializer;
import io.github.haykam821.stash.component.StashSyncCallback;
import io.github.haykam821.stash.network.packet.OpenStashS2CPayload;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;

public final class StashClientNetworking {
	private StashClientNetworking() {
		return;
	}

	public static void register() {
		ClientPlayNetworking.registerGlobalReceiver(OpenStashS2CPayload.ID, (payload, context) -> {
			ClientPlayerEntity player = context.player();
			StashComponent stash = StashComponentInitializer.STASH.get(player);

			context.client().setScreen(new StashScreen(player, stash));
		});

		StashSyncCallback.EVENT.register(stash -> {
			MinecraftClient client = MinecraftClient.getInstance();

			if (stash.isPlayer(client.player) && client.currentScreen instanceof StashScreen screen) {
				screen.updateStashInventory();
			}
		});
	}
}
