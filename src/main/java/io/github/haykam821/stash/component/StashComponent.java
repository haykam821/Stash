package io.github.haykam821.stash.component;

import java.util.Optional;

import dev.onyxstudios.cca.api.v3.component.sync.AutoSyncedComponent;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

public class StashComponent implements AutoSyncedComponent {
	private static final int MAX_COUNT = 64 * 1000;

	private final Object2IntOpenHashMap<Item> stash = new Object2IntOpenHashMap<>();

	public StashComponent(PlayerEntity player) {
		this.stash.defaultReturnValue(0);
	}

	private boolean shouldKeep(int count) {
		return count > 0;
	}

	public int getCount(Item item) {
		return this.stash.getInt(item);
	}

	public int setCount(Item item, int count) {
		if (!this.shouldKeep(count)) {
			return this.stash.removeInt(item);
		}
		return this.stash.put(item, count);
	}

	public int decreaseCount(Item item, int count) {
		int oldCount = this.stash.addTo(item, -count);
		if (!this.shouldKeep(oldCount - count)) {
			this.stash.removeInt(item);
		}

		return oldCount;
	}

	public Object2IntMap.FastEntrySet<Item> getEntries() {
		return this.stash.object2IntEntrySet();
	}

	public boolean insertStack(ItemStack stack) {
		if (!StashComponent.isInsertable(stack)) return false;

		int newCount = this.getCount(stack.getItem()) + stack.getCount();
		if (newCount >= MAX_COUNT) return false;

		this.setCount(stack.getItem(), newCount);
		return true;
	}

	@Override
	public void readFromNbt(CompoundTag tag) {
		CompoundTag stashTag = tag.getCompound("Stash");
		for (String key : stashTag.getKeys()) {
			int count = stashTag.getInt(key);
			if (this.shouldKeep(count)) continue;

			Optional<Item> itemMaybe = Registry.ITEM.getOrEmpty(Identifier.tryParse(key));
			if (itemMaybe.isPresent()) {
				this.stash.put(itemMaybe.get(), count);
			}
		}
	}

	@Override
	public void writeToNbt(CompoundTag tag) {
		CompoundTag stashTag = new CompoundTag();
		for (Object2IntMap.Entry<Item> entry : this.stash.object2IntEntrySet()) {
			Identifier id = Registry.ITEM.getId(entry.getKey());
			stashTag.putInt(id.toString(), entry.getIntValue());
		}

		tag.put("Stash", stashTag);
	}

	public static boolean isInsertable(ItemStack stack) {
		if (stack.isEmpty()) return false;
		return ItemStack.areEqual(stack, new ItemStack(stack.getItem(), stack.getCount()));
	}
}