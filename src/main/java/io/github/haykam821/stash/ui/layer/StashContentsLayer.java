package io.github.haykam821.stash.ui.layer;

import java.util.stream.Collectors;

import eu.pb4.sgui.api.elements.GuiElement;
import eu.pb4.sgui.api.elements.GuiElementBuilder;
import eu.pb4.sgui.api.elements.GuiElementInterface.ClickCallback;
import io.github.haykam821.stash.ui.StashUi;
import io.github.haykam821.stash.ui.element.EmptyElement;
import io.github.haykam821.stash.ui.element.InfoElement;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.slot.SlotActionType;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.MathHelper;

public class StashContentsLayer extends AbstractStashLayer {
	public StashContentsLayer(StashUi ui, int width, int height) {
		super(ui, width, height);
	}

	@Override
	public void update() {
		this.clearSlots();

		if (this.ui.isStashEmpty()) {
			this.setSlot(this.getSize() / 2, EmptyElement.INSTANCE);
			return;
		}

		int slot = 0;
		for (Object2IntMap.Entry<Item> entry : this.getPageEntries()) {
			if (slot >= this.getSize()) {
				break;
			}

			this.setSlot(slot, this.createElement(entry));
			slot += 1;
		}
	}

	private Iterable<Object2IntMap.Entry<Item>> getPageEntries() {
		return this.getStash().getEntries().stream()
			.sorted(this.ui.getStash().getSort().getComparator())
			.skip(this.getSize() * this.ui.getPage())
			.collect(Collectors.toList());
	}

	private GuiElement createElement(Object2IntMap.Entry<Item> entry) {
		Item item = entry.getKey();

		int count = entry.getIntValue();
		int maxCount = item.getMaxCount();

		GuiElementBuilder builder = new GuiElementBuilder(item)
			.setCount(MathHelper.clamp(count, 0, maxCount))
			.setCallback(createCallback(entry.getKey()));

		if (count > maxCount) {
			builder.addLoreLine(InfoElement.getLoreLine("text.stash.ui.entry.count", count));

			if (maxCount > 1) {
				int stacks = count / maxCount;
				int remainder = count % maxCount;

				builder.addLoreLine(InfoElement.getLoreLine("text.stash.ui.entry.stacks", stacks, remainder));
			}
		}

		if (count > maxCount) {
			builder.glow();
		}

		return builder.build();
	}

	private ClickCallback createCallback(Item item) {
		return (index, type, action, guiInterface) -> {
			boolean throwing = action == SlotActionType.THROW;
			if (throwing || this.ui.hasEmptyCursor()) {
				int count = this.getExtractCount(item, action);
				if (count > 0) {
					ItemStack stack = this.getStash().extractStack(item, count);
					if (throwing) {
						this.ui.getPlayer().dropItem(stack, false, true);
					} else {
						this.ui.setCursorStack(stack);
					}

					this.completeTransaction();
				}
			}
		};
	}

	private void completeTransaction() {
		this.ui.update();
		this.ui.getPlayer().playSound(SoundEvents.ITEM_BUNDLE_REMOVE_ONE, SoundCategory.MASTER, 1, 1);
	}

	private int getExtractCount(Item item, SlotActionType action) {
		switch (action) {
			case QUICK_MOVE:
				return item.getMaxCount();
			case PICKUP:
			case THROW:
				return 1;
			default:
				return -1;
		}
	}

	public int getMaxPage() {
		return MathHelper.ceil(this.getStash().getEntries().size() / (float) this.getSize());
	}
}
