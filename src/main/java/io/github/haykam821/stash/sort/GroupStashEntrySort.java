package io.github.haykam821.stash.sort;

import java.util.Comparator;
import java.util.LinkedHashSet;
import java.util.Set;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Ordering;

import it.unimi.dsi.fastutil.objects.Object2IntMap;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemGroups;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;

public class GroupStashEntrySort extends StashEntrySort {
	public GroupStashEntrySort(String literal, Item icon) {
		super(literal, icon);
	}

	@Override
	public Comparator<Object2IntMap.Entry<Item>> getComparator(PlayerEntity player) {
		ItemGroups.updateDisplayContext(player.getWorld().getEnabledFeatures(), player.isCreativeLevelTwoOp(), player.getWorld().getRegistryManager());

		Set<Item> items = getItems();

		return Ordering.explicit(ImmutableList.copyOf(items))
			.onResultOf(Object2IntMap.Entry::getKey);
	}

	/**
	 * {@return an ordered set of items based on item group organization, with ungrouped items specified last}
	 */
	private static Set<Item> getItems() {
		Set<Item> items = new LinkedHashSet<>(Registries.ITEM.size());

		for (ItemGroup group : ItemGroups.getGroups()) {
			if (group.isSpecial()) continue;

			for (ItemStack stack : group.getSearchTabStacks()) {
				items.add(stack.getItem());
			}
		}

		for (Item item : Registries.ITEM) {
			items.add(item);
		}

		return items;
	}
}
