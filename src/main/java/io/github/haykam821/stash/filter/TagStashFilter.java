package io.github.haykam821.stash.filter;

import java.util.Collection;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tag.ServerTagManagerHolder;
import net.minecraft.tag.Tag;
import net.minecraft.util.Identifier;

public class TagStashFilter implements StashFilter {
	public static final Codec<TagStashFilter> CODEC = RecordCodecBuilder.create(instance -> {
		return instance.group(
			Identifier.CODEC.fieldOf("tag").forGetter(TagStashFilter::getTag)
		).apply(instance, TagStashFilter::new);
	});

	private final Identifier tag;

	public TagStashFilter(Identifier tag) {
		this.tag = tag;
	}

	public Identifier getTag() {
		return this.tag;
	}

	@Override
	public boolean matches(Collection<ItemStack> matchedStacks, ItemStack stack, PlayerEntity player, int slot) {
		Tag<Item> tag = ServerTagManagerHolder.getTagManager().getItems().getTag(this.tag);
		if (tag == null) return false;

		return tag.contains(stack.getItem());
	}

	@Override
	public StashFilterType<?> getType() {
		return StashFilterTypes.TAG.getType();
	}
}