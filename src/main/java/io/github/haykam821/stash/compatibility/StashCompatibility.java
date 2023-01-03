package io.github.haykam821.stash.compatibility;

import eu.pb4.placeholders.api.PlaceholderResult;
import eu.pb4.placeholders.api.Placeholders;
import io.github.haykam821.stash.Main;
import io.github.haykam821.stash.component.StashComponent;
import io.github.haykam821.stash.component.StashComponentInitializer;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.item.Item;
import net.minecraft.registry.Registries;
import net.minecraft.util.Identifier;

public final class StashCompatibility {
	private static final String PLACEHOLDER_API_MOD_ID = "placeholder-api";

	private static final Identifier QUERY_ID = new Identifier(Main.MOD_ID, "query");

	private StashCompatibility() {
		return;
	}

	public static void register() {
		if (StashCompatibility.isModLoaded(PLACEHOLDER_API_MOD_ID)) {
			StashCompatibility.registerPlaceholders();
		}
	}

	private static void registerPlaceholders() {
		Placeholders.register(QUERY_ID, (context, argument) -> {
			if (!context.hasPlayer()) {
				return PlaceholderResult.invalid("No player!");
			}

			Identifier id = Identifier.tryParse(argument);
			if (id == null) {
				return PlaceholderResult.invalid("Unknown item ID!");
			}

			Item item = Registries.ITEM.get(id);
			if (item == null) {
				return PlaceholderResult.invalid("Unknown item!");
			}

			StashComponent stash = StashComponentInitializer.STASH.get(context.player());
			int count = stash.getCount(item);

			return PlaceholderResult.value("" + count);
		});
	}

	private static boolean isModLoaded(String modId) {
		return FabricLoader.getInstance().isModLoaded(modId);
	}
}