package io.github.haykam821.stash.ui.element;

import eu.pb4.sgui.api.elements.GuiElement;
import eu.pb4.sgui.api.elements.GuiElementBuilder;
import net.minecraft.item.Items;
import net.minecraft.text.Text;

public final class EmptyElement {
	public static final GuiElement INSTANCE = EmptyElement.of();

	private EmptyElement() {
		return;
	}

	private static GuiElement of() {
		return new GuiElementBuilder(Items.BARRIER)
			.setName(Text.translatable("text.stash.ui.empty"))
			.addLoreLine(InfoElement.getLoreLine("text.stash.ui.empty.description"))
			.build();
	}
}
