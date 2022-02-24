package io.github.haykam821.stash.ui.layer;

import eu.pb4.sgui.api.elements.GuiElement;
import io.github.haykam821.stash.ui.StashUi;
import io.github.haykam821.stash.ui.element.BackgroundElement;
import io.github.haykam821.stash.ui.element.InfoElement;
import io.github.haykam821.stash.ui.element.InserterElement;
import io.github.haykam821.stash.ui.element.PageElement;

public class StashToolbarLayer extends AbstractStashLayer {
	private final GuiElement previousPageElement;
	private final GuiElement inserterElement;
	private final GuiElement nextPageElement;

	public StashToolbarLayer(StashUi ui, int width) {
		super(ui, width, 1);

		this.previousPageElement = PageElement.ofPrevious(ui);
		this.inserterElement = InserterElement.of(ui);
		this.nextPageElement = PageElement.ofNext(ui);
	}

	@Override
	public void update() {
		int size = this.getSize();

		for (int slot = 0; slot < size; slot += 1) {
			if (slot == 1) {
				this.setSlot(slot, this.previousPageElement);
			} else if (slot == 3) {
				this.setSlot(slot, InfoElement.of(ui));
			} else if (slot == 5) {
				this.setSlot(slot, this.inserterElement);
			} else if (slot == 7) {
				this.setSlot(slot, this.nextPageElement);
			} else {
				this.setSlot(slot, BackgroundElement.INSTANCE);
			}
		}
	}
}
