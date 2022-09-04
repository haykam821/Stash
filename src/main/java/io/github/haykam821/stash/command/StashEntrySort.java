package io.github.haykam821.stash.command;

import java.util.Comparator;

import it.unimi.dsi.fastutil.objects.Object2IntMap;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.text.Text;
import net.minecraft.util.StringIdentifiable;
import net.minecraft.util.registry.Registry;

public enum StashEntrySort implements StringIdentifiable {
	UNSORTED("unsorted", Items.BARRIER, (a, b) -> {
		return 0;
	}),
	ALPHABETICAL("alphabetical", Items.OAK_SIGN, (a, b) -> {
		String pathA = Registry.ITEM.getId(a.getKey()).getPath();
		String pathB = Registry.ITEM.getId(b.getKey()).getPath();
		return pathA.compareTo(pathB);
	}),
	RAW_ID("rawId", Items.PORKCHOP, (a, b) -> {
		return Integer.compare(Item.getRawId(a.getKey()), Item.getRawId(b.getKey()));
	}),
	GROUP("group", Items.SLIME_BALL, (a, b) -> {
		int value = Integer.compare(a.getKey().getGroup().getIndex(), b.getKey().getGroup().getIndex());
		return value == 0 ? RAW_ID.getComparator().compare(a, b) : value;
	}),
	COUNT("count", Items.BEETROOT_SEEDS, (a, b) -> {
		return Integer.compare(a.getIntValue(), b.getIntValue());
	}),
	STACKS("stacks", Items.NETHERITE_SCRAP, Comparator.comparingDouble(entry -> {
		return entry.getIntValue() / (double) entry.getKey().getMaxCount();
	}));

	public static final StashEntrySort[] VALUES = StashEntrySort.values();

	private final String literal;
	private final Text name;

	private final Item icon;
	private final Comparator<Object2IntMap.Entry<Item>> comparator;

	private StashEntrySort(String literal, Item icon, Comparator<Object2IntMap.Entry<Item>> comparator) {
		this.literal = literal;
		this.name = Text.translatable("text.stash.sort." + literal);

		this.icon = icon;
		this.comparator = comparator;
	}

	@Override
	public String asString() {
		return this.literal;
	}

	public Text getName() {
		return this.name;
	}

	public Item getIcon() {
		return this.icon;
	}

	public Comparator<Object2IntMap.Entry<Item>> getComparator() {
		return this.comparator;
	}

	public StashEntrySort cycle(int offset) {
		return VALUES[(this.ordinal() + offset + VALUES.length) % VALUES.length];
	}
}