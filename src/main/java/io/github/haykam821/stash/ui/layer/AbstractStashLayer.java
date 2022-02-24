package io.github.haykam821.stash.ui.layer;

import eu.pb4.sgui.api.gui.layered.Layer;
import io.github.haykam821.stash.component.StashComponent;
import io.github.haykam821.stash.ui.StashUi;

public abstract class AbstractStashLayer extends Layer {
	protected final StashUi ui;

	public AbstractStashLayer(StashUi ui, int width, int height) {
		super(height, width);
		this.ui = ui;
	}

	protected StashComponent getStash() {
		return this.ui.getStash();
	}

	public abstract void update();
}
