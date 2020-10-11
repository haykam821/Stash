package io.github.haykam821.stash.filter;

import java.util.Collection;
import java.util.List;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;

public class OrStashFilter implements StashFilter {
	public static final Codec<OrStashFilter> CODEC = RecordCodecBuilder.create(instance -> {
		return instance.group(
			StashFilter.TYPE_CODEC.listOf().fieldOf("stash_filters").forGetter(OrStashFilter::getStashFilters)
		).apply(instance, OrStashFilter::new);
	});

	private final List<StashFilter> stashFilters;

	public OrStashFilter(List<StashFilter> stashFilters) {
		this.stashFilters = stashFilters;
	}
	
	public List<StashFilter> getStashFilters() {
		return this.stashFilters;
	}

	@Override
	public boolean matches(Collection<ItemStack> matchedStacks, ItemStack stack, PlayerEntity player, int slot) {
		for (StashFilter stashFilter : this.stashFilters) {
			if (stashFilter.matches(matchedStacks, stack, player, slot)) {
				return true;
			}
		}
		return false;
	}

	@Override
	public StashFilterType<?> getType() {
		return StashFilterTypes.OR.getType();
	}
}