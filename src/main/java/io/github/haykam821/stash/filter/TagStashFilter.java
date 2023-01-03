package io.github.haykam821.stash.filter;

import java.util.Collection;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.tag.TagKey;

public class TagStashFilter implements StashFilter {
	public static final Codec<TagStashFilter> CODEC = RecordCodecBuilder.create(instance -> {
		return instance.group(
			TagKey.unprefixedCodec(RegistryKeys.ITEM).fieldOf("tag").forGetter(TagStashFilter::getTag)
		).apply(instance, TagStashFilter::new);
	});

	private final TagKey<Item> tag;

	public TagStashFilter(TagKey<Item> tag) {
		this.tag = tag;
	}

	public TagKey<Item> getTag() {
		return this.tag;
	}

	@Override
	public boolean matches(Collection<ItemStack> matchedStacks, ItemStack stack, PlayerEntity player, int slot) {
		return stack.isIn(tag);
	}

	@Override
	public StashFilterType<?> getType() {
		return StashFilterTypes.TAG.getType();
	}
}