package io.github.haykam821.stash.filter;

import java.util.Collection;

import com.mojang.serialization.Codec;

import io.github.haykam821.stash.Main;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;

public interface StashFilter {
	public static final Codec<StashFilter> TYPE_CODEC = Main.STASH_FILTER_TYPE_REGISTRY.getCodec().dispatch(StashFilter::getType, StashFilterType::getCodec);

	boolean matches(Collection<ItemStack> matchedStacks, ItemStack stack, PlayerEntity player, int slot);
	StashFilterType<?> getType();
}