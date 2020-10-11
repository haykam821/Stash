package io.github.haykam821.stash.filter;

import java.util.Collection;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;

public class SlotIndexStashFilter implements StashFilter {
	public static final Codec<SlotIndexStashFilter> CODEC = RecordCodecBuilder.create(instance -> {
		return instance.group(
			Codec.INT.fieldOf("min_index").forGetter(SlotIndexStashFilter::getMinIndex),
			Codec.INT.fieldOf("max_index").forGetter(SlotIndexStashFilter::getMaxIndex)
		).apply(instance, SlotIndexStashFilter::new);
	});

	private final int minIndex;
	private final int maxIndex;

	public SlotIndexStashFilter(int minIndex, int maxIndex) {
		this.minIndex = minIndex;
		this.maxIndex = maxIndex;
	}

	public SlotIndexStashFilter(SlotRange slotRange) {
		this(slotRange.getMax(), slotRange.getMax());
	}

	public int getMinIndex() {
		return this.minIndex;
	}

	public int getMaxIndex() {
		return this.maxIndex;
	}

	@Override
	public boolean matches(Collection<ItemStack> matchedStacks, ItemStack stack, PlayerEntity player, int slot) {
		return slot >= this.minIndex && slot <= this.maxIndex;
	}

	@Override
	public StashFilterType<?> getType() {
		return StashFilterTypes.SLOT_INDEX.getType();
	}
}