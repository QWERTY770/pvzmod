package com.hungteen.pvz.gui.container;

import com.hungteen.pvz.item.tool.SunStorageSaplingItem;
import com.hungteen.pvz.register.ContainerRegister;
import com.hungteen.pvz.tileentity.SunConverterTileEntity;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.items.SlotItemHandler;

public class SunConverterContainer extends Container {

	@SuppressWarnings("unused")
	private final PlayerEntity player;
	public final SunConverterTileEntity te;
	
	public SunConverterContainer(int id, PlayerEntity player, BlockPos pos) {
		super(ContainerRegister.SUN_CONVERTER.get(), id);
		this.player = player;
		this.te = (SunConverterTileEntity) player.world.getTileEntity(pos);
		if(this.te == null) {
			System.out.println("Error: Open Sun Converter GUI !");
			return ;
		}
		this.trackIntArray(this.te.array);
		for(int i = 0; i < 3; ++ i) {
			for(int j = 0; j < 3; ++ j) {
				this.addSlot(new SlotItemHandler(this.te.handler, i * 3 + j, 62 + 18 * j, 17 + 18 * i) {
					@Override
					public boolean isItemValid(ItemStack stack) {
						return stack.getItem() instanceof SunStorageSaplingItem;
					}
				});
			}
		}
		for(int i = 0; i < 3; ++ i) {
			for(int j = 0; j < 9; ++ j) {
				this.addSlot(new Slot(player.inventory, j + i * 9 + 9, 8 + 18 * j, 84 + 18 * i));
			}
		}
		for(int i = 0; i < 9; ++ i) {
			this.addSlot(new Slot(player.inventory, i, 8 + 18 * i, 142));
		}
	}

	@Override
    public ItemStack transferStackInSlot(PlayerEntity playerIn, int index) {
		ItemStack itemstack = ItemStack.EMPTY;
		Slot slot = this.inventorySlots.get(index);
		if (slot != null && slot.getHasStack()) {
			ItemStack itemstack1 = slot.getStack();
			itemstack = itemstack1.copy();
			if (index < 9) {
				if (!this.mergeItemStack(itemstack1, 9, this.inventorySlots.size(), true)) {
					return ItemStack.EMPTY;
				}
			} else if (index < 9 + 27) {
				if(!mergeItemStack(itemstack1, 0, 9, false)
						&& !mergeItemStack(itemstack1, 9 + 27, this.inventorySlots.size(), false)) {
					return ItemStack.EMPTY;
				}
			} else {
				if (!this.mergeItemStack(itemstack1, 0, 9 + 27, false)) {
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
	
	@Override
	public boolean canInteractWith(PlayerEntity playerIn) {
		return this.te.isUsableByPlayer(playerIn);
	}

}
