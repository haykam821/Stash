package io.github.haykam821.stash.ui.element;

import eu.pb4.sgui.api.elements.GuiElement;
import eu.pb4.sgui.api.elements.GuiElementBuilder;
import io.github.haykam821.stash.ui.StashUi;
import net.minecraft.item.Items;
import net.minecraft.text.LiteralText;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Formatting;

public final class InfoElement {
	public static final Text UNKNOWN_TEXT = new LiteralText("???");

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

	protected static MutableText getToolbarName(String translationKey) {
		return new TranslatableText(translationKey).formatted(Formatting.YELLOW);
	}

	protected static MutableText getHelpLine(String translationKey, int ordinal, Object... args) {
		return getLoreLine(translationKey + ".help." + ordinal, args);
	}

	protected static MutableText getLoreLine(String translationKey, Object... args) {
		return new TranslatableText(translationKey, args).formatted(Formatting.GRAY);
	}
}
