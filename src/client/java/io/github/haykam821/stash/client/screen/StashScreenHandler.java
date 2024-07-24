package io.github.haykam821.stash.client.screen;

import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.ScreenHandler;

public class StashScreenHandler extends ScreenHandler {
	private final ScreenHandler parent;

	protected StashScreenHandler(ClientPlayerEntity player) {
		super(null, 0);

		this.parent = player.playerScreenHandler;
	}

	@Override
	public boolean canUse(PlayerEntity player) {
		return true;
	}

	@Override
	public ItemStack quickMove(PlayerEntity player, int slot) {
		return ItemStack.EMPTY;
	}

	@Override
	public ItemStack getCursorStack() {
		return this.parent.getCursorStack();
	}

	@Override
	public void setCursorStack(ItemStack stack) {
		this.parent.setCursorStack(stack);
	}
}
