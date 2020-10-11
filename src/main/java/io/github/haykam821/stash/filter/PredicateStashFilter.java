package io.github.haykam821.stash.filter;

import java.util.Collection;
import java.util.function.Predicate;

import com.mojang.serialization.Codec;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;

public class PredicateStashFilter implements StashFilter {
	public static final Codec<PredicateStashFilter> CODEC = Codec.unit(() -> {
		return new PredicateStashFilter(stack -> true);
	});

	private final Predicate<ItemStack> predicate;

	public PredicateStashFilter(Predicate<ItemStack> predicate) {
		this.predicate = predicate;
	}

	@Override
	public boolean matches(Collection<ItemStack> matchedStacks, ItemStack stack, PlayerEntity player, int slot) {
		return this.predicate.test(stack);
	}

	@Override
	public StashFilterType<?> getType() {
		return StashFilterTypes.PREDICATE.getType();
	}
}