package io.github.haykam821.stash.sort;

import java.util.Comparator;
import java.util.Objects;

import it.unimi.dsi.fastutil.objects.Object2IntMap;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;

public class SimpleStashEntrySort extends StashEntrySort {
	private final Comparator<Object2IntMap.Entry<Item>> comparator;

	public SimpleStashEntrySort(String literal, Item icon, Comparator<Object2IntMap.Entry<Item>> comparator) {
		super(literal, icon);

		this.comparator = Objects.requireNonNull(comparator);
	}

	@Override
	public Comparator<Object2IntMap.Entry<Item>> getComparator(PlayerEntity player) {
		return this.comparator;
	}
}
