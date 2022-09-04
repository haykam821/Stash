package io.github.haykam821.stash.ui;

import eu.pb4.sgui.api.gui.layered.LayeredGui;
import io.github.haykam821.stash.command.StashEntrySort;
import io.github.haykam821.stash.component.StashComponent;
import io.github.haykam821.stash.component.StashComponentInitializer;
import io.github.haykam821.stash.ui.element.InfoElement;
import io.github.haykam821.stash.ui.layer.StashContentsLayer;
import io.github.haykam821.stash.ui.layer.StashToolbarLayer;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;

public class StashUi extends LayeredGui {
	private final ServerPlayerEntity player;
	private final StashComponent stash;

	private final StashContentsLayer contentsLayer;
	private final StashToolbarLayer toolbarLayer;

	private StashEntrySort sort = StashEntrySort.ALPHABETICAL;
	private boolean selectingSort = false;

	private int page = 0;

	public StashUi(ServerPlayerEntity player) {
		super(ScreenHandlerType.GENERIC_9X6, player, false);

		this.player = player;
		this.stash = StashComponentInitializer.STASH.get(player);

		this.contentsLayer = new StashContentsLayer(this, this.width, this.height - 1);
		this.addLayer(contentsLayer, 0, 0);

		this.toolbarLayer = new StashToolbarLayer(this, this.width);
		this.addLayer(this.toolbarLayer, 0, this.height - 1);

		this.setTitle(this.getTitle());
		this.update();
	}

	@Override
	public Text getTitle() {
		Text name = this.player == null ? InfoElement.UNKNOWN_TEXT : this.player.getDisplayName();
		return Text.translatable("text.stash.ui.title", name);
	}

	public StashComponent getStash() {
		return this.stash;
	}

	public boolean isStashEmpty() {
		return this.stash.getEntries().isEmpty();
	}

	public StashEntrySort getSort() {
		return this.sort;
	}

	public void selectSort(StashEntrySort sort) {
		this.sort = sort;

		this.selectingSort = false;
		this.update();
	}

	public void cycleSort(int offset) {
		this.sort = this.sort.cycle(offset);
		this.update();
	}

	public boolean isSelectingSort() {
		return this.selectingSort;
	}

	public void startSelectingSort() {
		this.selectingSort = true;
		this.update();
	}

	public int getPage() {
		return this.page;
	}

	private void setPage(int page) {
		this.page = page;
		this.update();
	}

	public void setWrappedPage(int page) {
		if (page < 0) {
			page = this.contentsLayer.getMaxPage() - page - 1;
		}
		this.setPage(page);
	}

	public void movePage(int offset) {
		this.setPage(this.getPage() + offset);
	}

	private void clampPage() {
		if (page < 0) {
			page = 0;
		}

		int maxPage = this.contentsLayer.getMaxPage();
		if (page >= maxPage) {
			page = maxPage - 1;
		}
	}

	public void update() {
		this.clampPage();

		this.contentsLayer.update();
		this.toolbarLayer.update();
	}

	public boolean hasEmptyCursor() {
		return this.getCursorStack().isEmpty();
	}

	public ItemStack getCursorStack() {
		return this.player.currentScreenHandler.getCursorStack();
	}

	public void setCursorStack(ItemStack stack) {
		this.player.currentScreenHandler.setCursorStack(stack);
	}
}
