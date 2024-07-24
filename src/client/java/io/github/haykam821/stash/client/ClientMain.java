package io.github.haykam821.stash.client;

import io.github.haykam821.stash.client.network.StashClientNetworking;
import net.fabricmc.api.ClientModInitializer;

public class ClientMain implements ClientModInitializer {
	@Override
	public void onInitializeClient() {
		StashKeyBindings.register();

		StashClientNetworking.register();
	}
}
