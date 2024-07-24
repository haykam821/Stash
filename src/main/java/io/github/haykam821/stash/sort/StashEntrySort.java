package io.github.haykam821.stash.sort;

import java.util.Comparator;
import java.util.Objects;

import it.unimi.dsi.fastutil.objects.Object2IntMap;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.text.Text;
import net.minecraft.util.StringIdentifiable;

public abstract class StashEntrySort implements StringIdentifiable {
	public static final PacketCodec<RegistryByteBuf, StashEntrySort> PACKET_CODEC = PacketCodecs.registryValue(StashEntrySorts.REGISTRY_KEY);

	private final String literal;
	private final Text name;

	private final Item icon;

	public StashEntrySort(String literal, Item icon) {
		this.literal = Objects.requireNonNull(literal);
		this.name = Text.translatable("text.stash.sort." + literal);

		this.icon = Objects.requireNonNull(icon);
	}

	/**
	 * {@return a comparator that sorts items in a stash according to this sort}
	 * @param player the player context to get a comparator for
	 */
	public abstract Comparator<Object2IntMap.Entry<Item>> getComparator(PlayerEntity player);

	/**
	 * {@return a string representation of this sort that can be used for lookups and serialization}
	 * @see StashEntrySorts#byLiteral
	 */
	@Override
	public String asString() {
		return this.literal;
	}

	/**
	 * {@return the name of this sort}
	 */
	public Text getName() {
		return this.name;
	}

	/**
	 * {@return an item that represents this sort}
	 */
	public Item getIcon() {
		return this.icon;
	}

	/**
	 * {@return the sort in the registry at the specified offset from this sort}
	 * @param offset the index offset from this sort
	 */
	public StashEntrySort cycle(int offset) {
		int rawId = StashEntrySorts.REGISTRY.getRawIdOrThrow(this);
		int size = StashEntrySorts.REGISTRY.size();

		int index = (rawId + offset + size) % size;
		return StashEntrySorts.REGISTRY.getOrThrow(index);
	}
}
