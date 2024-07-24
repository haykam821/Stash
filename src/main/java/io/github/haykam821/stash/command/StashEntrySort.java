package io.github.haykam821.stash.command;

import java.util.Comparator;
import java.util.function.Consumer;
import java.util.function.IntFunction;

import io.netty.buffer.ByteBuf;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemGroups;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.registry.Registries;
import net.minecraft.text.Text;
import net.minecraft.util.StringIdentifiable;
import net.minecraft.util.function.ValueLists;

public enum StashEntrySort implements StringIdentifiable {
	UNSORTED(0, "unsorted", Items.BARRIER, (a, b) -> {
		return 0;
	}),
	ALPHABETICAL(1, "alphabetical", Items.OAK_SIGN, (a, b) -> {
		String pathA = Registries.ITEM.getId(a.getKey()).getPath();
		String pathB = Registries.ITEM.getId(b.getKey()).getPath();
		return pathA.compareTo(pathB);
	}),
	RAW_ID(2, "rawId", Items.PORKCHOP, (a, b) -> {
		return Integer.compare(Item.getRawId(a.getKey()), Item.getRawId(b.getKey()));
	}),
	GROUP(3, "group", Items.SLIME_BALL, (a, b) -> {
		int value = Integer.compare(getItemGroupIndex(a.getKey()), getItemGroupIndex(b.getKey()));
		return value == 0 ? RAW_ID.getComparator().compare(a, b) : value;
	}, player -> {
		ItemGroups.updateDisplayContext(player.getWorld().getEnabledFeatures(), player.isCreativeLevelTwoOp(), player.getWorld().getRegistryManager());
	}),
	RARITY(4, "rarity", Items.EMERALD, Comparator.comparing(entry -> {
		ItemStack stack = entry.getKey().getDefaultStack();
		return stack.getRarity();
	})),
	COUNT(5, "count", Items.BEETROOT_SEEDS, (a, b) -> {
		return Integer.compare(a.getIntValue(), b.getIntValue());
	}),
	STACKS(6, "stacks", Items.NETHERITE_SCRAP, Comparator.comparingDouble(entry -> {
		return entry.getIntValue() / (double) entry.getKey().getMaxCount();
	}));

	public static final StashEntrySort[] VALUES = StashEntrySort.values();
	public static final StashEntrySort DEFAULT = StashEntrySort.ALPHABETICAL;

	public static final IntFunction<StashEntrySort> ID_TO_VALUE = ValueLists.createIdToValueFunction(value -> value.index, VALUES, DEFAULT);
	public static final PacketCodec<ByteBuf, StashEntrySort> PACKET_CODEC = PacketCodecs.indexed(ID_TO_VALUE, value -> value.index);

	private final int index;

	private final String literal;
	private final Text name;

	private final Item icon;
	private final Comparator<Object2IntMap.Entry<Item>> comparator;
	private final Consumer<PlayerEntity> initializer;

	private StashEntrySort(int index, String literal, Item icon, Comparator<Object2IntMap.Entry<Item>> comparator, Consumer<PlayerEntity> initializer) {
		this.index = index;

		this.literal = literal;
		this.name = Text.translatable("text.stash.sort." + literal);

		this.icon = icon;
		this.comparator = comparator;

		this.initializer = initializer;
	}

	private StashEntrySort(int index, String literal, Item icon, Comparator<Object2IntMap.Entry<Item>> comparator) {
		this(index, literal, icon, comparator, player -> {});
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

	public void initialize(PlayerEntity player) {
		this.initializer.accept(player);
	}

	public StashEntrySort cycle(int offset) {
		return VALUES[(this.ordinal() + offset + VALUES.length) % VALUES.length];
	}

	public static StashEntrySort byLiteral(String literal) {
		if (literal != null) {
			for (StashEntrySort sort : VALUES) {
				if (literal.equals(sort.asString())) {
					return sort;
				}
			}
		}

		return DEFAULT;
	}

	private static int getItemGroupIndex(Item item) {
		int index = 0;

		for (ItemGroup group : ItemGroups.getGroups()) {
			if (group.isSpecial()) continue;

			for (ItemStack stack : group.getSearchTabStacks()) {
				if (stack.isOf(item)) {
					return index;
				}

				index += 1;
			}
		}

		return Integer.MAX_VALUE;
	}
}