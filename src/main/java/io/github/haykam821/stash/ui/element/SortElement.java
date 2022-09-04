package io.github.haykam821.stash.ui.element;

import eu.pb4.sgui.api.elements.GuiElement;
import eu.pb4.sgui.api.elements.GuiElementBuilder;
import eu.pb4.sgui.api.elements.GuiElementInterface.ClickCallback;
import io.github.haykam821.stash.command.StashEntrySort;
import io.github.haykam821.stash.ui.StashUi;
import net.minecraft.item.Items;

public final class SortElement {
	private SortElement() {
		return;
	}

	public static GuiElement of(StashUi ui) {
		String translationKey = "text.stash.ui.toolbar.sort";

		return new GuiElementBuilder(Items.COMPARATOR)
			.setName(InfoElement.getToolbarName(translationKey))
			.addLoreLine(InfoElement.getLoreLine(translationKey + ".current", ui.getSort().getName()))
			.addLoreLine(InfoElement.getHelpLine(translationKey, 1))
			.setCallback(SortElement.createCallback(ui))
			.build();
	}

	private static ClickCallback createCallback(StashUi ui) {
		return (index, type, action, guiInterface) -> {
			if (type.shift) {
				StashEntrySort previousSort = ui.getSort();
				ui.cycleSort(type.isLeft ? 1 : -1);

				if (ui.getSort() == previousSort) {
					return;
				}
			} else {
				ui.startSelectingSort();
			}

			PageElement.playClickSound(ui);
		};
	}
}
