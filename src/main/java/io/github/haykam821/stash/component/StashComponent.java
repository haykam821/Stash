package io.github.haykam821.stash.component;

import dev.onyxstudios.cca.api.v3.component.sync.AutoSyncedComponent;
import io.github.haykam821.stash.command.StashEntrySort;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

public interface StashComponent extends AutoSyncedComponent {
	public static final int MAX_COUNT = 64 * 1000;

	public int getCount(Item item);
	public int setCount(Item item, int count);
	public int decreaseCount(Item item, int count);

	public Object2IntMap.FastEntrySet<Item> getEntries();

	public default boolean insertStack(ItemStack stack) {
		if (!StashComponent.isInsertable(stack)) return false;

		int newCount = this.getCount(stack.getItem()) + stack.getCount();
		if (newCount >= MAX_COUNT) return false;

		this.setCount(stack.getItem(), newCount);
		return true;
	}

	public default ItemStack extractStack(Item item, int maxCount) {
		int count = Math.min(this.getCount(item), maxCount);

		this.decreaseCount(item, count);
		return new ItemStack(item, count);
	}

	public StashEntrySort getSort();
	public void setSort(StashEntrySort sort);

	public static boolean isInsertable(ItemStack stack) {
		if (stack.isEmpty()) return false;
		return ItemStack.areEqual(stack, new ItemStack(stack.getItem(), stack.getCount()));
	}
}