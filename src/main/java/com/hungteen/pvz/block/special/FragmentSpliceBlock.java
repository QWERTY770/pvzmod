package com.hungteen.pvz.block.special;

import com.hungteen.pvz.register.BlockRegister;
import com.hungteen.pvz.tileentity.FragmentSpliceTileEntity;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.InventoryHelper;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import net.minecraftforge.fml.network.NetworkHooks;

public class FragmentSpliceBlock extends Block {

	public FragmentSpliceBlock() {
		super(Properties.from(BlockRegister.STEEL_BLOCK.get()));
	}
	
	@Override
	public ActionResultType onBlockActivated(BlockState state, World worldIn, BlockPos pos, PlayerEntity player,
			Hand handIn, BlockRayTraceResult hit) {
		if (! worldIn.isRemote && handIn == Hand.MAIN_HAND) {
			FragmentSpliceTileEntity te = (FragmentSpliceTileEntity) worldIn.getTileEntity(pos);
		    NetworkHooks.openGui((ServerPlayerEntity) player, te, pos);
		}
		return ActionResultType.SUCCESS;
	}
	
	@Override
	public boolean hasTileEntity(BlockState state) {
		return true;
	}
	
	@Override
	public TileEntity createTileEntity(BlockState state, IBlockReader world) {
		return new FragmentSpliceTileEntity();
	}
	
	@SuppressWarnings("deprecation")
	@Override
	public void onReplaced(BlockState state, World worldIn, BlockPos pos, BlockState newState, boolean isMoving) {
		if (state.getBlock() != newState.getBlock()) {
			TileEntity tileentity = worldIn.getTileEntity(pos);
			if (tileentity instanceof FragmentSpliceTileEntity) {
				FragmentSpliceTileEntity te = (FragmentSpliceTileEntity) worldIn.getTileEntity(pos);
				for (int i = 0; i < te.handler.getSlots(); ++i) {
					InventoryHelper.spawnItemStack(worldIn, pos.getX(), pos.getY(), pos.getZ(),
							te.handler.getStackInSlot(i));
				}
			}
			super.onReplaced(state, worldIn, pos, newState, isMoving);
		}
	}

}
