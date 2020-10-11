package io.github.haykam821.stash.filter;

import java.util.Collection;
import java.util.List;

import com.google.common.collect.Lists;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;

public class AndStashFilter implements StashFilter {
	public static final Codec<AndStashFilter> CODEC = RecordCodecBuilder.create(instance -> {
		return instance.group(
			StashFilter.TYPE_CODEC.listOf().fieldOf("stash_filters").forGetter(AndStashFilter::getStashFilters)
		).apply(instance, AndStashFilter::new);
	});

	private final List<StashFilter> stashFilters;

	public AndStashFilter(List<StashFilter> stashFilters) {
		this.stashFilters = stashFilters;
	}

	public AndStashFilter(StashFilter stashFilter) {
		this(Lists.newArrayList(stashFilter));
	}
	
	public List<StashFilter> getStashFilters() {
		return this.stashFilters;
	}

	@Override
	public boolean matches(Collection<ItemStack> matchedStacks, ItemStack stack, PlayerEntity player, int slot) {
		for (StashFilter stashFilter : this.stashFilters) {
			if (!stashFilter.matches(matchedStacks, stack, player, slot)) {
				return false;
			}
		}
		return true;
	}

	@Override
	public StashFilterType<?> getType() {
		return StashFilterTypes.AND.getType();
	}
}