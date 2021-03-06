package com.hungteen.pvz.item.misc;

import com.hungteen.pvz.gui.container.AlmanacContainer;
import com.hungteen.pvz.register.GroupRegister;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import net.minecraftforge.fml.network.NetworkHooks;

public class AlmanacItem extends Item{

	public AlmanacItem() {
		super(new Properties().group(GroupRegister.PVZ_MISC).maxStackSize(1));
	}

	@Override
	public ActionResult<ItemStack> onItemRightClick(World worldIn, PlayerEntity playerIn, Hand handIn) {
		if(!worldIn.isRemote&&playerIn instanceof ServerPlayerEntity) {
			NetworkHooks.openGui((ServerPlayerEntity) playerIn, new INamedContainerProvider() {

				@Override
				public Container createMenu(int p_createMenu_1_, PlayerInventory p_createMenu_2_,
						PlayerEntity p_createMenu_3_) {
					return new AlmanacContainer(p_createMenu_1_, p_createMenu_3_);
				}

				@Override
				public ITextComponent getDisplayName() {
					return new TranslationTextComponent("gui.pvz.almanac.show");
				}
			});
		}
		return ActionResult.resultPass(playerIn.getHeldItem(handIn));
	}
	
}
