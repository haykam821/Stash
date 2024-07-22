package io.github.haykam821.stash.filter;

import java.util.Collection;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.entry.RegistryEntry;

public class ItemStashFilter implements StashFilter {
	public static final MapCodec<ItemStashFilter> CODEC = RecordCodecBuilder.mapCodec(instance -> {
		return instance.group(
			ItemStack.ITEM_CODEC.fieldOf("item").forGetter(ItemStashFilter::getItem)
		).apply(instance, ItemStashFilter::new);
	});

	private final RegistryEntry<Item> item;

	public ItemStashFilter(RegistryEntry<Item> item) {
		this.item = item;
	}

	public RegistryEntry<Item> getItem() {
		return this.item;
	}

	@Override
	public boolean matches(Collection<ItemStack> matchedStacks, ItemStack stack, PlayerEntity player, int slot) {
		return stack.itemMatches(this.item);
	}

	@Override
	public StashFilterType<?> getType() {
		return StashFilterTypes.ITEM.getType();
	}
}