package io.github.haykam821.stash.client.screen.slot;

import org.lwjgl.glfw.GLFW;

import io.github.haykam821.stash.client.screen.StashScreen;
import io.github.haykam821.stash.component.StashComponent;
import io.github.haykam821.stash.network.StashNetworking;
import io.github.haykam821.stash.network.packet.ExtractStashC2SPayload;
import io.github.haykam821.stash.network.packet.InsertStashC2SPayload;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.slot.Slot;
import net.minecraft.screen.slot.SlotActionType;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;

public class StashSlot extends Slot {
	public StashSlot(Inventory inventory, int index, int x, int y) {
		super(inventory, index, x, y);
	}

	/**
	 * @param button the mouse button used to click the slot
	 * @return whether the stash inventory should be {@linkplain StashScreen#updateStashInventory updated}
	 */
	public boolean handleStashClick(ClientPlayerEntity player, StashComponent stash, ScreenHandler handler, int button, SlotActionType action) {
		if (action == SlotActionType.PICKUP || action == SlotActionType.PICKUP_ALL || action == SlotActionType.QUICK_MOVE || action == SlotActionType.THROW) {
			ItemStack cursorStack = handler.getCursorStack();
			ItemStack stack = this.getStack();

			if (!cursorStack.isEmpty() && (button == GLFW.GLFW_MOUSE_BUTTON_2 || !ItemStack.areItemsEqual(cursorStack, stack)) && action != SlotActionType.THROW) {
				boolean all = button != GLFW.GLFW_MOUSE_BUTTON_2;
				ItemStack newStack = StashNetworking.insert(stash, cursorStack, all);

				if (!ItemStack.areEqual(cursorStack, newStack)) {
					handler.setCursorStack(newStack);

					ClientPlayNetworking.send(new InsertStashC2SPayload(all));
					player.playSoundToPlayer(SoundEvents.ITEM_BUNDLE_INSERT, SoundCategory.MASTER, 1, 1);

					return true;
				}
			} else if (!stack.isEmpty()) {
				boolean drop = action == SlotActionType.THROW;
				boolean all = drop ? button == GLFW.GLFW_MOUSE_BUTTON_2 : action == SlotActionType.QUICK_MOVE;

				ItemStack newStack = StashNetworking.extract(player, stash, cursorStack, stack.getItem(), all, drop);

				if (!ItemStack.areEqual(cursorStack, newStack) || drop) {
					handler.setCursorStack(newStack);

					ClientPlayNetworking.send(new ExtractStashC2SPayload(stack.getItem(), all, drop));
					player.playSoundToPlayer(SoundEvents.ITEM_BUNDLE_REMOVE_ONE, SoundCategory.MASTER, 1, 1);

					return true;
				}
			}
		}

		return false;
	}
}
