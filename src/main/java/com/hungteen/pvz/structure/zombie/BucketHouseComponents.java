package com.hungteen.pvz.structure.zombie;

import java.util.List;
import java.util.Random;

import com.hungteen.pvz.data.loot.PVZLoot;
import com.hungteen.pvz.register.EntityRegister;
import com.hungteen.pvz.register.StructureRegister;
import com.hungteen.pvz.structure.PVZTemplateComponent;
import com.hungteen.pvz.utils.StringUtil;

import net.minecraft.block.Blocks;
import net.minecraft.entity.EntityType;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.MobSpawnerTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.MutableBoundingBox;
import net.minecraft.world.IWorld;
import net.minecraft.world.gen.ChunkGenerator;
import net.minecraft.world.gen.Heightmap.Type;
import net.minecraft.world.gen.feature.structure.IStructurePieceType;
import net.minecraft.world.gen.feature.structure.StructurePiece;
import net.minecraft.world.gen.feature.template.TemplateManager;

public class BucketHouseComponents {

	public static final ResourceLocation res1 = StringUtil.prefix("zombie_house/bucket_house");
	
	public static void generate(TemplateManager manager, BlockPos pos1, Rotation rotation, List<StructurePiece> list, Random rand) {
	      list.add(new BucketHouseComponent(manager, res1, pos1, rotation));
    }
	
	public static class BucketHouseComponent extends PVZTemplateComponent{

		private static final IStructurePieceType type = StructureRegister.BUCKET_HOUSE;
		
		public BucketHouseComponent(TemplateManager manager, CompoundNBT nbt) {
			super(type, manager, nbt);
		}

		public BucketHouseComponent(TemplateManager manager, ResourceLocation res,BlockPos pos, Rotation rotation) {
			super(type, manager, res, pos, rotation);
		}
		
		@Override
		public boolean create(IWorld worldIn, ChunkGenerator<?> chunkGeneratorIn, Random randomIn,
				MutableBoundingBox mutableBoundingBoxIn, ChunkPos chunkPosIn) {
			BlockPos mid = BlockPos.ZERO;
			int dx = 6, dz = 5;
			switch(rotation) {
			case CLOCKWISE_90:{mid = this.templatePosition.add(-dz, 0, dx); break;}
			case CLOCKWISE_180:{mid = this.templatePosition.add(-dx, 0, -dz); break;}
			case COUNTERCLOCKWISE_90:{mid = this.templatePosition.add(dz, 0, -dx); break;}
			default:{mid = this.templatePosition.add(dx, 0, dz); break;}
			}
			int height = chunkGeneratorIn.func_222531_c(mid.getX(), mid.getZ(), Type.WORLD_SURFACE_WG);
			this.templatePosition = new BlockPos(this.templatePosition.getX(), height + 1, this.templatePosition.getZ());
			return super.create(worldIn, chunkGeneratorIn, randomIn, mutableBoundingBoxIn, chunkPosIn);
		}
		
        @Override
		protected void handleDataMarker(String function, BlockPos pos, IWorld worldIn, Random rand,
				MutableBoundingBox sbb) {
			if(function.equals("bonus_chest1")){
				this.generateChest(worldIn, sbb, rand, pos, PVZLoot.BUCKET_HOUSE_CHEST, null);
			} else if(function.equals("bonus_chest2")) {
				this.generateChest(worldIn, sbb, rand, pos, PVZLoot.BUCKET_HOUSE_CHEST, null);
			} else if(function.equals("spawner")) {
				worldIn.setBlockState(pos, Blocks.SPAWNER.getDefaultState(), 2);
				TileEntity te = worldIn.getTileEntity(pos);
				if(te instanceof MobSpawnerTileEntity) {
					((MobSpawnerTileEntity)te).getSpawnerBaseLogic().setEntityType(getRandomEntityType(rand));
				}
			}
		}
        
		protected EntityType<?> getRandomEntityType(Random rand){
			int num = rand.nextInt(3);
			if(num == 0) return EntityRegister.CONEHEAD_ZOMBIE.get();
			if(num == 1) return EntityRegister.POLE_ZOMBIE.get();
			if(num == 2) return EntityRegister.BUCKETHEAD_ZOMBIE.get();
			return null;
		}
		
	}
}
