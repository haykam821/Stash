package io.github.haykam821.stash.ui.element;

import eu.pb4.sgui.api.elements.GuiElement;
import eu.pb4.sgui.api.elements.GuiElementBuilder;
import eu.pb4.sgui.api.elements.GuiElementInterface.ClickCallback;
import io.github.haykam821.stash.ui.StashUi;
import net.minecraft.item.Items;
import net.minecraft.screen.slot.SlotActionType;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;

public final class PageElement {
	private PageElement() {
		return;
	}

	public static GuiElement ofPrevious(StashUi ui) {
		return PageElement.of(ui, "previous", -1, 0, "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMzEwODI5OGZmMmIyNjk1MWQ2ODNlNWFkZTQ2YTQyZTkwYzJmN2M3ZGQ0MWJhYTkwOGJjNTg1MmY4YzMyZTU4MyJ9fX0");
	}

	public static GuiElement ofNext(StashUi ui) {
		return PageElement.of(ui, "next", 1, -1, "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYzg2MTg1YjFkNTE5YWRlNTg1ZjE4NGMzNGYzZjNlMjBiYjY0MWRlYjg3OWU4MTM3OGU0ZWFmMjA5Mjg3In19fQ");
	}

	private static GuiElement of(StashUi ui, String type, int offset, int shiftPage, String texture) {
		String translationKey = "text.stash.ui.toolbar.page." + type;

		return new GuiElementBuilder(Items.PLAYER_HEAD)
			.setName(InfoElement.getToolbarName("spectatorMenu." + type + "_page"))
			.setSkullOwner(texture)
			.addLoreLine(InfoElement.getHelpLine(translationKey, 1))
			.addLoreLine(InfoElement.getHelpLine(translationKey, 2))
			.setCallback(PageElement.createCallback(ui, offset, shiftPage))
			.build();
	}

	private static ClickCallback createCallback(StashUi ui, int offset, int shiftPage) {
		return (index, type, action, guiInterface) -> {
			int previousPage = ui.getPage();
			if (action == SlotActionType.QUICK_MOVE) {
				ui.setWrappedPage(shiftPage);
			} else {
				ui.movePage(offset);
			}

			if (previousPage != ui.getPage()) {
				PageElement.playClickSound(ui);
			}
		};
	}

	protected static void playClickSound(StashUi ui) {
		ui.getPlayer().playSound(SoundEvents.UI_BUTTON_CLICK.value(), SoundCategory.MASTER, 1, 1);
	}
}
