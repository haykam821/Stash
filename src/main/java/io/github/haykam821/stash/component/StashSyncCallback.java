package io.github.haykam821.stash.component;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;

@FunctionalInterface
public interface StashSyncCallback {
	public static final Event<StashSyncCallback> EVENT = EventFactory.createArrayBacked(StashSyncCallback.class, callbacks -> {
		return stash -> {
			for (StashSyncCallback callback : callbacks) {
				callback.onStashSync(stash);
			}
		};
	});

	public void onStashSync(StashComponent stash);
}
