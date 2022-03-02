package io.github.haykam821.stash.command;

import java.util.Comparator;

import it.unimi.dsi.fastutil.objects.Object2IntMap;
import net.minecraft.item.Item;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.StringIdentifiable;
import net.minecraft.util.registry.Registry;

public enum StashEntrySort implements StringIdentifiable {
	UNSORTED("unsorted", (a, b) -> {
		return 0;
	}),
	ALPHABETICAL("alphabetical", (a, b) -> {
		String pathA = Registry.ITEM.getId(a.getKey()).getPath();
		String pathB = Registry.ITEM.getId(b.getKey()).getPath();
		return pathA.compareTo(pathB);
	}),
	RAW_ID("rawId", (a, b) -> {
		return Integer.compare(Item.getRawId(a.getKey()), Item.getRawId(b.getKey()));
	}),
	COUNT("count", (a, b) -> {
		return Integer.compare(a.getIntValue(), b.getIntValue());
	});

	private static final StashEntrySort[] VALUES = StashEntrySort.values();

	private final String literal;
	private final Text name;
	private final Comparator<Object2IntMap.Entry<Item>> comparator;

	private StashEntrySort(String literal, Comparator<Object2IntMap.Entry<Item>> comparator) {
		this.literal = literal;
		this.name = new TranslatableText("text.stash.sort." + literal);
		this.comparator = comparator;
	}

	@Override
	public String asString() {
		return this.literal;
	}

	public Text getName() {
		return this.name;
	}

	public Comparator<Object2IntMap.Entry<Item>> getComparator() {
		return this.comparator;
	}

	public StashEntrySort cycle(int offset) {
		return VALUES[(this.ordinal() + offset + VALUES.length) % VALUES.length];
	}
}