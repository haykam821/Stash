package io.github.haykam821.stash.client;

import io.github.haykam821.stash.client.screen.StashScreen;
import io.github.haykam821.stash.component.StashComponent;
import io.github.haykam821.stash.component.StashComponentInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;

public final class StashKeyBindings {
	public static final KeyBinding OPEN_STASH = new KeyBinding("key.stash.open_stash", InputUtil.UNKNOWN_KEY.getCode(), KeyBinding.INVENTORY_CATEGORY);

	private StashKeyBindings() {
		return;
	}

	public static void register() {
		KeyBindingHelper.registerKeyBinding(OPEN_STASH);

		ClientTickEvents.END_CLIENT_TICK.register(client -> {
			while (OPEN_STASH.wasPressed()) {
				StashComponent stash = StashComponentInitializer.STASH.get(client.player);

				client.setScreen(new StashScreen(client.player, stash));
			}
		});
	}
}
