package io.github.haykam821.stash.filter;

import java.util.Collection;

import com.mojang.serialization.Codec;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;

public class NeverStashFilter implements StashFilter {
	public static final Codec<NeverStashFilter> CODEC = Codec.unit(() -> {
		return new NeverStashFilter();
	});

	@Override
	public boolean matches(Collection<ItemStack> matchedStacks, ItemStack stack, PlayerEntity player, int slot) {
		return true;
	}

	@Override
	public StashFilterType<?> getType() {
		return StashFilterTypes.NEVER.getType();
	}
}