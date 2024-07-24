package io.github.haykam821.stash.component;

import java.util.Optional;

import io.github.haykam821.stash.command.StashEntrySort;
import io.github.haykam821.stash.ui.element.InfoElement;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.registry.Registries;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

public class StashComponentImpl implements StashComponent {
	private static final String STASH_KEY = "Stash";
	private static final String SORT_KEY = "Sort";

	private final PlayerEntity player;

	private final Object2IntOpenHashMap<Item> stash = new Object2IntOpenHashMap<>();
	private StashEntrySort sort = StashEntrySort.DEFAULT;

	public StashComponentImpl(PlayerEntity player) {
		this.player = player;

		this.stash.defaultReturnValue(0);
	}

	private void sync() {
		StashComponentInitializer.STASH.sync(this.player);
	}

	private boolean shouldKeep(int count) {
		return count > 0;
	}

	@Override
	public int getCount(Item item) {
		return this.stash.getInt(item);
	}

	@Override
	public int setCount(Item item, int count) {
		int oldCount;

		if (!this.shouldKeep(count)) {
			oldCount = this.stash.removeInt(item);
		} else {
			oldCount = this.stash.put(item, count);
		}

		this.sync();
		return oldCount;
	}

	@Override
	public int decreaseCount(Item item, int count) {
		int oldCount = this.stash.addTo(item, -count);
		if (!this.shouldKeep(oldCount - count)) {
			this.stash.removeInt(item);
		}

		this.sync();
		return oldCount;
	}

	@Override
	public Object2IntMap.FastEntrySet<Item> getEntries() {
		return this.stash.object2IntEntrySet();
	}

	@Override
	public StashEntrySort getSort() {
		return this.sort;
	}

	@Override
	public void setSort(StashEntrySort sort) {
		this.sort = sort;
		this.sync();
	}

	@Override
	public Text getTitle() {
		Text name = this.player == null ? InfoElement.UNKNOWN_TEXT : this.player.getDisplayName();
		return Text.translatable("text.stash.ui.title", name);
	}

	@Override
	public boolean isPlayer(PlayerEntity player) {
		return this.player == player;
	}

	@Override
	public void readFromNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registryLookup) {
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
		this.sync();
	}

	@Override
	public void writeToNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registryLookup) {
		NbtCompound stashNbt = new NbtCompound();
		for (Object2IntMap.Entry<Item> entry : this.stash.object2IntEntrySet()) {
			Identifier id = Registries.ITEM.getId(entry.getKey());
			stashNbt.putInt(id.toString(), entry.getIntValue());
		}

		nbt.put(STASH_KEY, stashNbt);

		nbt.putString(SORT_KEY, this.sort.asString());
	}

	@Override
	public void applySyncPacket(RegistryByteBuf buf) {
		StashComponent.super.applySyncPacket(buf);
		StashSyncCallback.EVENT.invoker().onStashSync(this);
	}

	@Override
	public boolean shouldSyncWith(ServerPlayerEntity player) {
		return this.isPlayer(player);
	}

	@Override
	public boolean isRequiredOnClient() {
		return false;
	}
}