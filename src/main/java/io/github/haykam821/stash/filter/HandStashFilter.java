package io.github.haykam821.stash.filter;

import java.util.Collection;

import com.mojang.serialization.MapCodec;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;

public class HandStashFilter implements StashFilter {
	public static final MapCodec<HandStashFilter> CODEC = MapCodec.unit(() -> {
		return new HandStashFilter();
	});

	@Override
	public boolean matches(Collection<ItemStack> matchedStacks, ItemStack stack, PlayerEntity player, int slot) {
		return slot == player.getInventory().selectedSlot;
	}

	@Override
	public StashFilterType<?> getType() {
		return StashFilterTypes.HAND.getType();
	}
}