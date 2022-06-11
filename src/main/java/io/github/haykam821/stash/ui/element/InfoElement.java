package io.github.haykam821.stash.ui.element;

import eu.pb4.sgui.api.elements.GuiElement;
import eu.pb4.sgui.api.elements.GuiElementBuilder;
import io.github.haykam821.stash.ui.StashUi;
import net.minecraft.item.Items;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

public final class InfoElement {
	public static final Text UNKNOWN_TEXT = Text.literal("???");

	private InfoElement() {
		return;
	}

	public static GuiElement of(StashUi ui) {
		String translationKey = "text.stash.ui.toolbar.info";

		return new GuiElementBuilder(ui.isStashEmpty() ? Items.MAP : Items.FILLED_MAP)
			.setName(InfoElement.getToolbarName(translationKey))
			.addLoreLine(InfoElement.getLoreLine(translationKey + ".page", ui.getPage() == -1 ? UNKNOWN_TEXT : ui.getPage() + 1))
			.build();
	}

	protected static Text getToolbarName(String translationKey) {
		return Text.translatable(translationKey).formatted(Formatting.YELLOW);
	}

	protected static Text getHelpLine(String translationKey, int ordinal, Object... args) {
		return getLoreLine(translationKey + ".help." + ordinal, args);
	}

	public static Text getLoreLine(String translationKey, Object... args) {
		return Text.translatable(translationKey, args).formatted(Formatting.GRAY);
	}
}
