package io.github.haykam821.stash.filter;

import net.minecraft.util.StringIdentifiable;

public enum SlotRange implements StringIdentifiable {
	HOTBAR("hotbar", 0, 8),
	MAIN("main", 0, 35),
	OTHER("other", 9, 35),
	ARMOR("armor", 36, 39),
	OFFHAND("offhand", 40);

	private final String name;
	private final int min;
	private final int max;

	private SlotRange(String name, int min, int max) {
		this.name = name;
		this.min = min;
		this.max = max;
	}

	private SlotRange(String name, int index) {
		this(name, index, index);
	}

	public String asString() {
		return this.name;
	}

	public int getMin() {
		return this.min;
	}

	public int getMax() {
		return this.max;
	}
}