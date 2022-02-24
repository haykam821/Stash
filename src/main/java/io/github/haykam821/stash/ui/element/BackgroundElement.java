package io.github.haykam821.stash.ui.element;

import eu.pb4.sgui.api.elements.GuiElement;
import eu.pb4.sgui.api.elements.GuiElementBuilder;
import net.minecraft.item.Items;
import net.minecraft.text.LiteralText;

public final class BackgroundElement {
	public static final GuiElement INSTANCE = BackgroundElement.of();

	private BackgroundElement() {
		return;
	}

	private static GuiElement of() {
		return new GuiElementBuilder(Items.WHITE_STAINED_GLASS_PANE)
			.setName(LiteralText.EMPTY)
			.build();
	}
}
