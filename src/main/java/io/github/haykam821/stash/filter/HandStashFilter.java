package io.github.haykam821.stash.filter;

import java.util.Collection;

import com.mojang.serialization.Codec;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;

public class HandStashFilter implements StashFilter {
	public static final Codec<HandStashFilter> CODEC = Codec.unit(() -> {
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