package io.github.haykam821.stash.component;

import java.util.Optional;

import dev.onyxstudios.cca.api.v3.component.sync.AutoSyncedComponent;
import io.github.haykam821.stash.command.StashEntrySort;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.Registries;
import net.minecraft.util.Identifier;

public class StashComponent implements AutoSyncedComponent {
	private static final String STASH_KEY = "Stash";
	private static final String SORT_KEY = "Sort";

	private static final int MAX_COUNT = 64 * 1000;

	private final Object2IntOpenHashMap<Item> stash = new Object2IntOpenHashMap<>();
	private StashEntrySort sort = StashEntrySort.DEFAULT;

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

	public ItemStack extractStack(Item item, int maxCount) {
		int count = Math.min(this.getCount(item), maxCount);

		this.decreaseCount(item, count);
		return new ItemStack(item, count);
	}

	public StashEntrySort getSort() {
		return this.sort;
	}

	public void setSort(StashEntrySort sort) {
		this.sort = sort;
	}

	@Override
	public void readFromNbt(NbtCompound nbt) {
		this.stash.clear();

		NbtCompound stashNbt = nbt.getCompound(STASH_KEY);
		for (String key : stashNbt.getKeys()) {
			int count = stashNbt.getInt(key);
			if (!this.shouldKeep(count)) continue;

			Optional<Item> itemMaybe = Registries.ITEM.getOrEmpty(Identifier.tryParse(key));
			if (itemMaybe.isPresent()) {
				this.stash.put(itemMaybe.get(), count);
			}
		}

		this.sort = StashEntrySort.byLiteral(nbt.getString(SORT_KEY));
	}

	@Override
	public void writeToNbt(NbtCompound nbt) {
		NbtCompound stashNbt = new NbtCompound();
		for (Object2IntMap.Entry<Item> entry : this.stash.object2IntEntrySet()) {
			Identifier id = Registries.ITEM.getId(entry.getKey());
			stashNbt.putInt(id.toString(), entry.getIntValue());
		}

		nbt.put(STASH_KEY, stashNbt);

		nbt.putString(SORT_KEY, this.sort.asString());
	}

	public static boolean isInsertable(ItemStack stack) {
		if (stack.isEmpty()) return false;
		return ItemStack.areEqual(stack, new ItemStack(stack.getItem(), stack.getCount()));
	}
}