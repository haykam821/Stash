package io.github.haykam821.stash.ui.element;

import eu.pb4.sgui.api.elements.GuiElement;
import eu.pb4.sgui.api.elements.GuiElementBuilder;
import eu.pb4.sgui.api.elements.GuiElementInterface.ClickCallback;
import io.github.haykam821.stash.command.StashEntrySort;
import io.github.haykam821.stash.ui.StashUi;

public final class SortSelectElement {
	private SortSelectElement() {
		return;
	}

	public static GuiElement of(StashUi ui, StashEntrySort sort) {
		GuiElementBuilder builder = new GuiElementBuilder(sort.getIcon())
			.setName(sort.getName())
			.setCallback(SortSelectElement.createCallback(ui, sort));

		if (sort == ui.getSort()) {
			builder.glow();
		}

		return builder.build();
	}

	private static ClickCallback createCallback(StashUi ui, StashEntrySort sort) {
		return (index, type, action, guiInterface) -> {
			ui.selectSort(sort);
			PageElement.playClickSound(ui);
		};
	}
}
