package io.github.haykam821.stash.ui.element;

import eu.pb4.sgui.api.elements.GuiElement;
import eu.pb4.sgui.api.elements.GuiElementBuilder;
import eu.pb4.sgui.api.elements.GuiElementInterface.ClickCallback;
import io.github.haykam821.stash.ui.StashUi;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;

public final class InserterElement {
	private InserterElement() {
		return;
	}

	public static GuiElement of(StashUi ui) {
		String translationKey = "text.stash.ui.toolbar.inserter";

		return new GuiElementBuilder(Items.HOPPER)
			.setName(InfoElement.getToolbarName(translationKey))
			.addLoreLine(InfoElement.getHelpLine(translationKey, 1))
			.setCallback(InserterElement.createCallback(ui))
			.build();
	}

	private static ClickCallback createCallback(StashUi ui) {
		return (index, type, action, guiInterface) -> {
			switch (action) {
				case QUICK_MOVE:
					InserterElement.insertAllFromInventory(ui);
					InserterElement.insertAllFromCursorStack(ui);

					break;
				case PICKUP:
					if (type.isLeft) {
						InserterElement.insertAllFromCursorStack(ui);
					} else {
						InserterElement.insertOneFromCursorStack(ui);
					}
					break;
				default:
					break;
			}
		};
	}

	private static void insertAllFromInventory(StashUi ui) {
		boolean changed = false;
		Item filter = ui.hasEmptyCursor() ? null : ui.getCursorStack().getItem();

		Inventory inventory = ui.getPlayer().getInventory();
		for (int slot = 0; slot < inventory.size(); slot++) {
			ItemStack stack = inventory.getStack(slot);
			if ((filter == null || filter == stack.getItem()) && ui.getStash().insertStack(stack)) {
				changed = true;
				inventory.removeStack(slot);
			}
		}

		if (changed) {
			InserterElement.completeTransaction(ui);
		}
	}

	private static void insertAllFromCursorStack(StashUi ui) {
		if (ui.getStash().insertStack(ui.getCursorStack())) {
			ui.setCursorStack(ItemStack.EMPTY);
			InserterElement.completeTransaction(ui);
		}
	}

	private static void insertOneFromCursorStack(StashUi ui) {
		ItemStack stack = ui.getCursorStack().copy();
		stack.setCount(1);

		if (ui.getStash().insertStack(stack)) {
			ui.getCursorStack().decrement(1);
			InserterElement.completeTransaction(ui);
		}
	}

	private static void completeTransaction(StashUi ui) {
		ui.update();
		ui.getPlayer().playSound(SoundEvents.ITEM_BUNDLE_INSERT, SoundCategory.MASTER, 1, 1);
	}
}
