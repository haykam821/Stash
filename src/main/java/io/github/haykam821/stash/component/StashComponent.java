package io.github.haykam821.stash.component;

import org.ladysnake.cca.api.v3.component.sync.AutoSyncedComponent;

import io.github.haykam821.stash.command.StashEntrySort;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;

/**
 * A component that represents a stash, which is a map of items to counts.
 * 
 * <p>This component is available for players and is synchronized to clients.
 * 
 * @see StashComponentInitializer#STASH
 */
public interface StashComponent extends AutoSyncedComponent {
	/**
	 * The maximum count for each item in the stash.
	 */
	public static final int MAX_COUNT = 64 * 1000;

	/**
	 * Gets the count of an item in this stash.
	 * @param item the item to get the count of
	 * @return the count of the item
	 */
	public int getCount(Item item);

	/**
	 * Sets the count of an item in this stash.
	 * @param item the item to set the count of
	 * @param count the new count of the item
	 * @return the old count of the item
	 */
	public int setCount(Item item, int count);

	/**
	 * Decreases the count of an item in this stash.
	 * @param item the item to decrease the count of
	 * @param count the amount to decrease the count by
	 * @return the old count of the item
	 */
	public int decreaseCount(Item item, int count);

	/**
	 * {@return a map of items to their counts in this stash}
	 */
	public Object2IntMap.FastEntrySet<Item> getEntries();

	/**
	 * Attempts to insert an item stack into this stash.
	 * @param stack the stack to insert
	 * @return whether the stack was successfully inserted
	 */
	public default boolean insertStack(ItemStack stack) {
		if (!StashComponent.isInsertable(stack)) return false;

		int newCount = this.getCount(stack.getItem()) + stack.getCount();
		if (newCount >= MAX_COUNT) return false;

		this.setCount(stack.getItem(), newCount);
		return true;
	}

	/**
	 * Creates an item stack by decreasing the count of an item in this stash.
	 * @param item the item to extract
	 * @param maxCount the maximum count of the stack
	 * @return the created stack
	 */
	public default ItemStack extractStack(Item item, int maxCount) {
		int count = Math.min(this.getCount(item), maxCount);

		this.decreaseCount(item, count);
		return new ItemStack(item, count);
	}

	/**
	 * {@return the currently persisted sort for this stash's entries}
	 */
	public StashEntrySort getSort();

	/**
	 * Changes the sort of this stash's entries.
	 * @param sort the new sort
	 */
	public void setSort(StashEntrySort sort);

	/**
	 * {@return title text that describes this stash}
	 */
	public Text getTitle();

	/**
	 * {@return whether this stash is associated with the given player}
	 * @param player the player to check
	 */
	public boolean isPlayer(PlayerEntity player);

	/**
	 * {@return whether a given stack can potentially be inserted into a stash}
	 * @param stack the stack to check
	 */
	public static boolean isInsertable(ItemStack stack) {
		if (stack.isEmpty()) return false;
		return ItemStack.areEqual(stack, new ItemStack(stack.getItem(), stack.getCount()));
	}
}