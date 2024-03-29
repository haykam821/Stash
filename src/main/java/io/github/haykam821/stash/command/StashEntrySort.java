package io.github.haykam821.stash.command;

import java.util.Comparator;

import it.unimi.dsi.fastutil.objects.Object2IntMap;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemGroups;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.registry.Registries;
import net.minecraft.text.Text;
import net.minecraft.util.StringIdentifiable;

public enum StashEntrySort implements StringIdentifiable {
	UNSORTED("unsorted", Items.BARRIER, (a, b) -> {
		return 0;
	}),
	ALPHABETICAL("alphabetical", Items.OAK_SIGN, (a, b) -> {
		String pathA = Registries.ITEM.getId(a.getKey()).getPath();
		String pathB = Registries.ITEM.getId(b.getKey()).getPath();
		return pathA.compareTo(pathB);
	}),
	RAW_ID("rawId", Items.PORKCHOP, (a, b) -> {
		return Integer.compare(Item.getRawId(a.getKey()), Item.getRawId(b.getKey()));
	}),
	GROUP("group", Items.SLIME_BALL, (a, b) -> {
		int value = Integer.compare(getItemGroupIndex(a.getKey()), getItemGroupIndex(b.getKey()));
		return value == 0 ? RAW_ID.getComparator().compare(a, b) : value;
	}),
	RARITY("rarity", Items.EMERALD, Comparator.comparing(entry -> {
		ItemStack stack = entry.getKey().getDefaultStack();
		return entry.getKey().getRarity(stack);
	})),
	COUNT("count", Items.BEETROOT_SEEDS, (a, b) -> {
		return Integer.compare(a.getIntValue(), b.getIntValue());
	}),
	STACKS("stacks", Items.NETHERITE_SCRAP, Comparator.comparingDouble(entry -> {
		return entry.getIntValue() / (double) entry.getKey().getMaxCount();
	}));

	public static final StashEntrySort[] VALUES = StashEntrySort.values();
	public static final StashEntrySort DEFAULT = StashEntrySort.ALPHABETICAL;

	private final String literal;
	private final Text name;

	private final Item icon;
	private final Comparator<Object2IntMap.Entry<Item>> comparator;

	private StashEntrySort(String literal, Item icon, Comparator<Object2IntMap.Entry<Item>> comparator) {
		this.literal = literal;
		this.name = Text.translatable("text.stash.sort." + literal);

		this.icon = icon;
		this.comparator = comparator;
	}

	@Override
	public String asString() {
		return this.literal;
	}

	public Text getName() {
		return this.name;
	}

	public Item getIcon() {
		return this.icon;
	}

	public Comparator<Object2IntMap.Entry<Item>> getComparator() {
		return this.comparator;
	}

	public StashEntrySort cycle(int offset) {
		return VALUES[(this.ordinal() + offset + VALUES.length) % VALUES.length];
	}

	public static StashEntrySort byLiteral(String literal) {
		if (literal != null) {
			for (StashEntrySort sort : VALUES) {
				if (literal.equals(sort.asString())) {
					return sort;
				}
			}
		}

		return DEFAULT;
	}

	private static int getItemGroupIndex(Item item) {
		int index = 0;

		for (ItemGroup group : ItemGroups.getGroups()) {
			if (group.isSpecial()) continue;

			for (ItemStack stack : group.getSearchTabStacks()) {
				if (stack.isOf(item)) {
					return index;
				}

				index += 1;
			}
		}

		return Integer.MAX_VALUE;
	}
}