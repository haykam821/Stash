package io.github.haykam821.stash.ui;

import io.github.haykam821.stash.ui.element.InfoElement;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.LoreComponent;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import net.minecraft.util.math.MathHelper;

public final class EntryStackHelper {
	private EntryStackHelper() {
		return;
	}

	/**
	 * Creates an item stack representing a given entry in a stash.
	 * @param entry the entry to create an item stack for
	 * @return the created item stack
	 */
	public static ItemStack createEntryStack(Object2IntMap.Entry<Item> entry) {
		Item item = entry.getKey();

		int count = entry.getIntValue();
		int maxCount = item.getMaxCount();

		ItemStack stack = new ItemStack(item);
		stack.setCount(MathHelper.clamp(count, 0, maxCount));

		if (count > maxCount) {
			addLoreLine(stack, InfoElement.getLoreLine("text.stash.ui.entry.count", count));

			if (maxCount > 1) {
				int stacks = count / maxCount;
				int remainder = count % maxCount;

				addLoreLine(stack, InfoElement.getLoreLine("text.stash.ui.entry.stacks", stacks, remainder));
			}
		}

		if (count > item.getMaxCount()) {
			stack.set(DataComponentTypes.ENCHANTMENT_GLINT_OVERRIDE, true);
		}

		return stack;
	}

	private static void addLoreLine(ItemStack stack, Text lore) {
		stack.apply(DataComponentTypes.LORE, LoreComponent.DEFAULT, component -> component.with(lore));
	}
}
