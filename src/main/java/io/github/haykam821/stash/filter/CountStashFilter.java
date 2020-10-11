package io.github.haykam821.stash.filter;

import java.util.Collection;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;

public class CountStashFilter implements StashFilter {
	public static final Codec<CountStashFilter> CODEC = RecordCodecBuilder.create(instance -> {
		return instance.group(
			Codec.INT.optionalFieldOf("min_count", 0).forGetter(CountStashFilter::getMinCount),
			Codec.INT.fieldOf("max_count").forGetter(CountStashFilter::getMaxCount)
		).apply(instance, CountStashFilter::new);
	});

	private final int minCount;
	private final int maxCount;

	public CountStashFilter(int minCount, int maxCount) {
		this.minCount = minCount;
		this.maxCount = maxCount;
	}

	public CountStashFilter(int maxCount) {
		this(0, maxCount);
	}

	public int getMinCount() {
		return this.minCount;
	}

	public int getMaxCount() {
		return this.maxCount;
	}

	@Override
	public boolean matches(Collection<ItemStack> matchedStacks, ItemStack stack, PlayerEntity player, int slot) {
		int count = 0;
		for (ItemStack matchedStack : matchedStacks) {
			count += matchedStack.getCount();
		}

		int newCount = count + stack.getCount();
		return newCount >= this.minCount && newCount <= this.maxCount;
	}

	@Override
	public StashFilterType<?> getType() {
		return StashFilterTypes.COUNT.getType();
	}
}