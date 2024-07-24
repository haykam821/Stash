package io.github.haykam821.stash.client.screen.slot;

import net.minecraft.screen.slot.Slot;

public final class SlotHelper {
	private SlotHelper() {
		return;
	}

	public static Slot cloneSlot(Slot slot, int y) {
		Slot clone = new Slot(slot.inventory, slot.getIndex(), slot.x, y);
		clone.id = slot.id;

		return clone;
	}
}
