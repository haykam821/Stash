package io.github.haykam821.stash.filter;

import java.util.Collection;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import net.minecraft.util.Identifier;

public class ItemStashFilter implements StashFilter {
	public static final Codec<ItemStashFilter> CODEC = RecordCodecBuilder.create(instance -> {
		return instance.group(
			Identifier.CODEC.fieldOf("item").forGetter(ItemStashFilter::getItem)
		).apply(instance, ItemStashFilter::new);
	});

	private final Identifier item;

	public ItemStashFilter(Identifier item) {
		this.item = item;
	}

	public Identifier getItem() {
		return this.item;
	}

	@Override
	public boolean matches(Collection<ItemStack> matchedStacks, ItemStack stack, PlayerEntity player, int slot) {
		return this.item == Registries.ITEM.getId(stack.getItem());
	}

	@Override
	public StashFilterType<?> getType() {
		return StashFilterTypes.ITEM.getType();
	}
}