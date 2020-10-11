package io.github.haykam821.stash.command;

import java.util.Comparator;

import it.unimi.dsi.fastutil.objects.Object2IntMap;
import net.minecraft.item.Item;
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
	
	private final String name;
	private final Comparator<Object2IntMap.Entry<Item>> comparator;

	private StashEntrySort(String name, Comparator<Object2IntMap.Entry<Item>> comparator) {
		this.name = name;
		this.comparator = comparator;
	}

	@Override
	public String asString() {
		return this.name;
	}

	public Comparator<Object2IntMap.Entry<Item>> getComparator() {
		return this.comparator;
	}
}