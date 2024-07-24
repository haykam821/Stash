package io.github.haykam821.stash.client.screen;

import java.util.List;
import java.util.stream.Collectors;

import org.lwjgl.glfw.GLFW;

import com.mojang.blaze3d.systems.RenderSystem;

import io.github.cottonmc.cotton.gui.client.BackgroundPainter;
import io.github.cottonmc.cotton.gui.widget.WPanel;
import io.github.cottonmc.cotton.gui.widget.WPlainPanel;
import io.github.haykam821.stash.client.StashKeyBindings;
import io.github.haykam821.stash.client.screen.slot.SlotHelper;
import io.github.haykam821.stash.client.screen.slot.StashSlot;
import io.github.haykam821.stash.command.StashEntrySort;
import io.github.haykam821.stash.component.StashComponent;
import io.github.haykam821.stash.network.packet.SetStashSortC2SPayload;
import io.github.haykam821.stash.ui.EntryStackHelper;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.ingame.AbstractInventoryScreen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.Item;
import net.minecraft.screen.slot.Slot;
import net.minecraft.screen.slot.SlotActionType;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;

public class StashScreen extends AbstractInventoryScreen<StashScreenHandler> {
	private static final Identifier SCROLLER_TEXTURE = Identifier.ofVanilla("widget/scroller");
	private static final Identifier SCROLLER_BACKGROUND_TEXTURE = Identifier.ofVanilla("widget/scroller_background");

	private static final int STASH_ROWS = 7;
	private static final int ROW_SIZE = 9;

	private static final int SLOT_HEIGHT = 18;

	private static final int SCROLLER_WIDTH = 6;
	private static final int SCROLLER_HEIGHT = 15;
	private static final int SCROLLER_BACKGROUND_HEIGHT = SLOT_HEIGHT * STASH_ROWS;

	private final ClientPlayerEntity player;
	private final StashComponent stash;

	private final Inventory stashInventory = new SimpleInventory(ROW_SIZE * STASH_ROWS);

	private boolean draggingScrollbar;
	private double scrollPosition = 0;

	public StashScreen(ClientPlayerEntity player, StashComponent stash) {
		super(new StashScreenHandler(player), player.getInventory(), stash.getTitle());

		this.player = player;
		this.stash = stash;
	}

	public void updateStashInventory() {
		StashEntrySort sort = this.stash.getSort();
		sort.initialize(this.player);

		List<Object2IntMap.Entry<Item>> entries = this.stash.getEntries().stream()
			.sorted(sort.getComparator())
			.skip(this.getRow() * ROW_SIZE)
			.limit(this.stashInventory.size())
			.collect(Collectors.toList());

		int stashIndex = 0;
		this.stashInventory.clear();

		for (Object2IntMap.Entry<Item> entry : entries) {
			this.stashInventory.setStack(stashIndex, EntryStackHelper.createEntryStack(entry));
			stashIndex += 1;
		}
	}

	private Text getSortButtonMessage() {
		return this.stash.getSort().getName();
	}

	private void onSortButtonPressed(ButtonWidget button) {
		int offset = Screen.hasShiftDown() ? -1 : 1;
		StashEntrySort sort = this.stash.getSort().cycle(offset);

		this.stash.setSort(sort);
		ClientPlayNetworking.send(new SetStashSortC2SPayload(sort));

		button.setMessage(this.getSortButtonMessage());
		this.updateStashInventory();
	}

	@Override
	protected void init() {
		this.handler.slots.clear();

		int y = 0;

		// Screen label
		y += 18;

		// Stash slots
		this.updateStashInventory();

		for (int index = 0; index < this.stashInventory.size(); index += 1) {
			Slot slot = new StashSlot(this.stashInventory, index, 8 + (index % ROW_SIZE) * SLOT_HEIGHT, SLOT_HEIGHT + (index / ROW_SIZE) * SLOT_HEIGHT);
			this.handler.slots.add(slot);
		}

		y += SLOT_HEIGHT * STASH_ROWS + 2;

		// Inventory label
		this.playerInventoryTitleY = y;
		y += 11;

		// Player inventory slots
		for (Slot slot : this.player.playerScreenHandler.slots) {
			if (slot.inventory instanceof PlayerInventory && slot.getIndex() < PlayerInventory.MAIN_SIZE) {
				Slot newSlot = SlotHelper.cloneSlot(slot, slot.y - 84 + y);
				this.handler.slots.add(newSlot);
			}
		}

		y += SLOT_HEIGHT * 4 + 10;

		// Background
		this.backgroundWidth = 176 + 6;
		this.backgroundHeight = y;

		super.init();

		// Sort button
		ButtonWidget sortButton = ButtonWidget.builder(this.getSortButtonMessage(), this::onSortButtonPressed)
			.position(this.x + this.backgroundWidth + 8, this.y)
			.width(80)
			.build();

		this.addDrawableChild(sortButton);
	}

	@Override
	public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
		if (StashKeyBindings.OPEN_STASH.matchesKey(keyCode, scanCode)) {
			this.close();
			return true;
		} else if (super.keyPressed(keyCode, scanCode, modifiers)) {
			return true;
		}

		return false;
	}

	@Override
	public boolean mouseClicked(double mouseX, double mouseY, int button) {
		if (button == GLFW.GLFW_MOUSE_BUTTON_1) {
			int scrollerMinX = this.x + 169;
			int scrollerMaxX = scrollerMinX + SCROLLER_WIDTH;

			int scrollerMinY = this.y + 17;
			int scrollerMaxY = scrollerMinY + SCROLLER_BACKGROUND_HEIGHT;

			if (mouseX >= scrollerMinX && mouseX < scrollerMaxX && mouseY >= scrollerMinY && mouseY < scrollerMaxY) {
				this.draggingScrollbar = true;
				return true;
			}
		}

		return super.mouseClicked(mouseX, mouseY, button);
	}

	@Override
	public boolean mouseReleased(double mouseX, double mouseY, int button) {
		if (button == GLFW.GLFW_MOUSE_BUTTON_1) {
			this.draggingScrollbar = false;
		}

		return super.mouseReleased(mouseX, mouseY, button);
	}

	@Override
	public boolean mouseDragged(double mouseX, double mouseY, int button, double deltaX, double deltaY) {
		if (this.draggingScrollbar) {
			int minY = this.y + 17;

			double scrollY = mouseY - minY - (SCROLLER_HEIGHT / 2d);
			int unoccupiedHeight = SCROLLER_BACKGROUND_HEIGHT - SCROLLER_HEIGHT;

			this.scrollPosition = MathHelper.clamp(scrollY / unoccupiedHeight, 0, 1);

			this.updateStashInventory();
			return true;
		} else {
			return super.mouseDragged(mouseX, mouseY, button, deltaX, deltaY);
		}
	}

	@Override
	public boolean mouseScrolled(double mouseX, double mouseY, double horizontalAmount, double verticalAmount) {
		this.scrollPosition = this.getUpdatedScrollPosition(verticalAmount);
		this.updateStashInventory();

		return true;
	}

	@Override
	protected void onMouseClick(Slot slot, int slotId, int button, SlotActionType action) {
		if (slot instanceof StashSlot stashSlot) {
			if (stashSlot.handleStashClick(this.player, this.stash, this.handler, button, action)) {
				this.updateStashInventory();
			}
		} else {
			// Player inventory slots use vanilla behavior
			super.onMouseClick(slot, slotId, button, action);
		}
	}

	@Override
	public void render(DrawContext context, int mouseX, int mouseY, float delta) {
		super.render(context, mouseX, mouseY, delta);
		this.drawMouseoverTooltip(context, mouseX, mouseY);
	}

	@Override
	protected void drawStatusEffects(DrawContext context, int mouseX, int mouseY) {
		return;
	}

	@Override
	public boolean hideStatusEffectHud() {
		return false;
	}

	@Override
	protected void drawBackground(DrawContext context, float delta, int mouseX, int mouseY) {
		WPanel panel = new WPlainPanel();

		panel.setSize(this.backgroundWidth, this.backgroundHeight);
		BackgroundPainter.VANILLA.paintBackground(context, this.x, this.y, panel);

		for (Slot slot : this.handler.slots) {
			panel.setSize(16, 16);
			BackgroundPainter.SLOT.paintBackground(context, this.x + slot.x, this.y + slot.y, panel);
		}

		this.drawScroller(context);
	}

	private void drawScroller(DrawContext context) {
		int minX = this.x + 169;
		int minY = this.y + 17;

		int y = (int) (minY + (SCROLLER_BACKGROUND_HEIGHT - SCROLLER_HEIGHT) * this.scrollPosition);

		RenderSystem.enableBlend();

		context.drawGuiTexture(SCROLLER_BACKGROUND_TEXTURE, minX, minY, SCROLLER_WIDTH, SCROLLER_BACKGROUND_HEIGHT);
		context.drawGuiTexture(SCROLLER_TEXTURE, minX, y, SCROLLER_WIDTH, SCROLLER_HEIGHT);

		RenderSystem.disableBlend();
	}

	private int getOverflowRows() {
		return MathHelper.ceilDiv(this.stash.getEntries().size(), ROW_SIZE) - STASH_ROWS;
	}

	private int getRow() {
		return Math.max((int) (this.scrollPosition * this.getOverflowRows() + 0.5), 0);
	}

	private double getUpdatedScrollPosition(double amount) {
		return MathHelper.clamp(this.scrollPosition - (amount / (double) this.getOverflowRows()), 0, 1);
	}
}
