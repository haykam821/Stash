package io.github.haykam821.stash.sort;

import java.util.Comparator;

import io.github.haykam821.stash.Main;
import net.fabricmc.fabric.api.event.registry.FabricRegistryBuilder;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.util.Identifier;

public final class StashEntrySorts {
	private static final Identifier REGISTRY_ID = Main.id("stash_entry_sort");
	protected static final RegistryKey<Registry<StashEntrySort>> REGISTRY_KEY = RegistryKey.ofRegistry(REGISTRY_ID);

	public static final Registry<StashEntrySort> REGISTRY = FabricRegistryBuilder.createSimple(REGISTRY_KEY)
		.buildAndRegister();

	public static final StashEntrySort UNSORTED = new SimpleStashEntrySort("unsorted", Items.BARRIER, (a, b) -> {
		return 0;
	});

	public static final StashEntrySort ALPHABETICAL = new SimpleStashEntrySort("alphabetical", Items.OAK_SIGN, (a, b) -> {
		String pathA = Registries.ITEM.getId(a.getKey()).getPath();
		String pathB = Registries.ITEM.getId(b.getKey()).getPath();
		return pathA.compareTo(pathB);
	});

	public static final StashEntrySort RAW_ID = new SimpleStashEntrySort("rawId", Items.PORKCHOP, (a, b) -> {
		return Integer.compare(Item.getRawId(a.getKey()), Item.getRawId(b.getKey()));
	});

	public static final StashEntrySort GROUP = new GroupStashEntrySort("group", Items.SLIME_BALL);

	public static final StashEntrySort RARITY = new SimpleStashEntrySort("rarity", Items.EMERALD, Comparator.comparing(entry -> {
		ItemStack stack = entry.getKey().getDefaultStack();
		return stack.getRarity();
	}));

	public static final StashEntrySort COUNT = new SimpleStashEntrySort("count", Items.BEETROOT_SEEDS, (a, b) -> {
		return Integer.compare(a.getIntValue(), b.getIntValue());
	});

	public static final StashEntrySort STACKS = new SimpleStashEntrySort("stacks", Items.NETHERITE_SCRAP, Comparator.comparingDouble(entry -> {
		return entry.getIntValue() / (double) entry.getKey().getMaxCount();
	}));

	public static final StashEntrySort DEFAULT = ALPHABETICAL;

	private StashEntrySorts() {
		return;
	}

	public static void register() {
		register("unsorted", UNSORTED);
		register("alphabetical", ALPHABETICAL);
		register("raw_id", RAW_ID);
		register("group", GROUP);
		register("rarity", RARITY);
		register("count", COUNT);
		register("stacks", STACKS);
	}

	private static void register(String path, StashEntrySort sort) {
		Identifier id = Main.id(path);
		Registry.register(REGISTRY, id, sort);
	}

	public static StashEntrySort byLiteral(String literal) {
		if (literal != null) {
			for (StashEntrySort sort : REGISTRY) {
				if (literal.equals(sort.asString())) {
					return sort;
				}
			}
		}

		return DEFAULT;
	}
}
