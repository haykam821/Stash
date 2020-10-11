package io.github.haykam821.stash.filter;

import com.mojang.serialization.Codec;

import io.github.haykam821.stash.Main;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

public enum StashFilterTypes {
	ALWAYS("always", AlwaysStashFilter.CODEC),
	AND("and", AndStashFilter.CODEC),
	COUNT("count", CountStashFilter.CODEC),
	HAND("hand", HandStashFilter.CODEC),
	ITEM("item", ItemStashFilter.CODEC),
	NEVER("never", NeverStashFilter.CODEC),
	OR("or", OrStashFilter.CODEC),
	PREDICATE("predicate", PredicateStashFilter.CODEC),
	SLOT_INDEX("slot_index", SlotIndexStashFilter.CODEC),
	TAG("tag", TagStashFilter.CODEC);
	
	private final StashFilterType<?> type;

	private <T extends StashFilter> StashFilterTypes(String path, Codec<T> codec) {
		Identifier id = new Identifier(Main.MOD_ID, path);

		StashFilterType<T> type = new StashFilterType<>(codec);
		this.type = type;

		Registry.register(Main.STASH_FILTER_TYPE_REGISTRY, id, type);
	}

	public StashFilterType<?> getType() {
		return this.type;
	}

	public static void register() {
		return;
	}
}