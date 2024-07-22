package io.github.haykam821.stash.filter;

import java.util.Collection;

import com.mojang.serialization.MapCodec;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;

public class AlwaysStashFilter implements StashFilter {
	public static final MapCodec<AlwaysStashFilter> CODEC = MapCodec.unit(() -> {
		return new AlwaysStashFilter();
	});

	@Override
	public boolean matches(Collection<ItemStack> matchedStacks, ItemStack stack, PlayerEntity player, int slot) {
		return true;
	}

	@Override
	public StashFilterType<?> getType() {
		return StashFilterTypes.ALWAYS.getType();
	}
}