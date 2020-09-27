package com.hungteen.pvz.gui.container;

import com.hungteen.pvz.capabilities.CapabilityHandler;
import com.hungteen.pvz.capabilities.player.PlayerDataManager;
import com.hungteen.pvz.item.tool.card.SummonCardItem;
import com.hungteen.pvz.register.ContainerRegister;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIntArray;
import net.minecraft.util.IntArray;
import net.minecraft.util.math.MathHelper;

public class PlayerInventoryContainer extends Container {

	private static final int SLOT_NUM_PER_PAGE = 54;
	private static final int DATA_SIZE = 1;
	private final PlayerEntity player;
	public int currentPage;
	private IIntArray inventoryData = new IntArray(DATA_SIZE);

	public PlayerInventoryContainer(int id, PlayerEntity player) {
		super(ContainerRegister.PLAYER_INVENTORY.get(), id);
		this.player = player;
		this.currentPage = 1;

		this.player.getCapability(CapabilityHandler.PLAYER_DATA_CAPABILITY).ifPresent((l) -> {
			PlayerDataManager.InventoryStats inv = l.getPlayerData().getInventoryStats();
			for (int i = 0; i < 6; i++) {
				for (int j = 0; j < 9; j++) {
					int cnt = i * 9 + j;
					if (inv.getSlotNum() < cnt)
						this.addSlot(new LockedSlot(inv.getInventory(), i * 9 + j, 24 + j * 18, 29 + i * 18));
					else
						this.addSlot(new CardSlot(inv.getInventory(), i * 9 + j, 24 + j * 18, 29 + i * 18));
				}
			}
			if (inv.getSlotNum() < SLOT_NUM_PER_PAGE) {
				int left = SLOT_NUM_PER_PAGE - inv.getSlotNum();
				this.inventoryData.set(0, left / 9);
			}
		});

		for (int i = 0; i < 3; i++) {
			for (int j = 0; j < 9; j++) {
				this.addSlot(new Slot(this.player.inventory, j + i * 9 + 9, 24 + 18 * j, 159 + 18 * i));
			}
		}

		for (int i = 0; i < 9; i++) {
			this.addSlot(new Slot(this.player.inventory, i, 24 + 18 * i, 217));
		}

		this.trackIntArray(inventoryData);
	}

	public void onPageChange() {
		this.updateSlots();
	}

	public int getLockRow() {
		return this.inventoryData.get(0);
	}

	private void updateSlots() {
		this.player.getCapability(CapabilityHandler.PLAYER_DATA_CAPABILITY).ifPresent((l) -> {
			PlayerDataManager.InventoryStats inv = l.getPlayerData().getInventoryStats();
			for (int i = 0; i < 6; i++) {
				for (int j = 0; j < 9; j++) {
					int pos = (this.currentPage - 1) * SLOT_NUM_PER_PAGE + 9 * i + j;
					if (inv.getSlotNum() < pos)
						this.inventorySlots.set(i * 9 + j,
								new LockedSlot(inv.getInventory(), pos, 24 + j * 18, 29 + i * 18));
					else
						this.inventorySlots.set(i * 9 + j,
								new CardSlot(inv.getInventory(), pos, 24 + j * 18, 29 + i * 18));
				}
			}
			if (inv.getSlotNum() < SLOT_NUM_PER_PAGE * this.currentPage) {
				int left = SLOT_NUM_PER_PAGE * this.currentPage - inv.getSlotNum();
				this.inventoryData.set(0, MathHelper.clamp(left / 9, 0, 6));
			}
		});
	}

	@Override
	public boolean canInteractWith(PlayerEntity playerIn) {
		return true;
	}

	@Override
	public ItemStack transferStackInSlot(PlayerEntity playerIn, int index) {
		ItemStack itemstack = ItemStack.EMPTY;
		Slot slot = this.inventorySlots.get(index);
		if (slot != null && slot.getHasStack()) {
			ItemStack itemstack1 = slot.getStack();
			itemstack = itemstack1.copy();
			if (index < 54 - this.getLockRow() * 9) {// in valid card slots
				if (!this.mergeItemStack(itemstack1, 54, this.inventorySlots.size(), true)) {
					return ItemStack.EMPTY;
				}
			} else if (index < 54) {// in locked card slots
				return ItemStack.EMPTY;
			} else {
				if (!this.mergeItemStack(itemstack1, 0, 54 - this.getLockRow() * 9, false)) {
					return ItemStack.EMPTY;
				}
			}
			if (itemstack1.isEmpty()) {
				slot.putStack(ItemStack.EMPTY);
			} else {
				slot.onSlotChanged();
			}
		}
		return itemstack;
	}

	static class CardSlot extends Slot {

		public CardSlot(IInventory inventoryIn, int index, int xPosition, int yPosition) {
			super(inventoryIn, index, xPosition, yPosition);
		}

		@Override
		public boolean isItemValid(ItemStack stack) {
			return stack.getItem() instanceof SummonCardItem;
		}
	}

	static class LockedSlot extends Slot {

		public LockedSlot(IInventory inventoryIn, int index, int xPosition, int yPosition) {
			super(inventoryIn, index, xPosition, yPosition);
		}

		@Override
		public boolean isItemValid(ItemStack stack) {
			return false;
		}
	}
}