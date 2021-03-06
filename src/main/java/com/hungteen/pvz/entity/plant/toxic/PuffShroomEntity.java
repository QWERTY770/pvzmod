package com.hungteen.pvz.entity.plant.toxic;

import com.hungteen.pvz.entity.ai.PVZNearestTargetGoal;
import com.hungteen.pvz.entity.bullet.SporeEntity;
import com.hungteen.pvz.entity.plant.base.PlantShooterEntity;
import com.hungteen.pvz.register.EntityRegister;
import com.hungteen.pvz.register.SoundRegister;
import com.hungteen.pvz.utils.EntityUtil;
import com.hungteen.pvz.utils.enums.Plants;

import net.minecraft.entity.CreatureEntity;
import net.minecraft.entity.EntitySize;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.Pose;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;

public class PuffShroomEntity extends PlantShooterEntity {

	protected final double LENTH = 0.1d;
	
	public PuffShroomEntity(EntityType<? extends CreatureEntity> type, World worldIn) {
		super(type, worldIn);
	}
	
	@Override
	protected void registerGoals() {
		super.registerGoals();
		this.targetSelector.addGoal(0, new PVZNearestTargetGoal(this, true, 5, getShootRange(), 2, 0));
	}
	
	@Override
	public void shootBullet() {
		LivingEntity target=this.getAttackTarget();
		if(target==null) {
			//System.out.println("no target at all!");
			return ;
		}
		double dx = target.getPosX() - this.getPosX();
        double dz = target.getPosZ() - this.getPosZ();
        double y = this.getPosY()+this.getSize(getPose()).height*0.7f;
        double dis =MathHelper.sqrt(dx * dx + dz * dz);
        double tmp=this.LENTH / dis;
        double deltaX = tmp * dx;
        double deltaZ = tmp * dz;
        SporeEntity spore = new SporeEntity(EntityRegister.SPORE.get(), this.world, this);
        spore.setPosition(this.getPosX() + deltaX, y, this.getPosZ() + deltaZ);
        spore.shootPea(dx, target.getPosY() + target.getHeight() - y, dz, this.getBulletSpeed());      
        EntityUtil.playSound(this, SoundRegister.PUFF.get());
        this.world.addEntity(spore);
	}

	@Override
	public void startSuperMode(boolean first) {
		super.startSuperMode(first);
		if(first) {
			int x = this.getHelpRange();
			for(PuffShroomEntity shroom : world.getEntitiesWithinAABB(EntityRegister.PUFF_SHROOM.get(), EntityUtil.getEntityAABB(this, x, x), (shroom)-> {
				return !EntityUtil.checkCanEntityAttack(this, shroom);
			})) {
				if(shroom.canStartSuperMode()) {
				    shroom.startSuperMode(false);	
				}
				shroom.setLiveTick(0);
			}
		}
	}
	
	@Override
	public void startShootAttack() {
		this.setAttackTime(1);
	}
	
	@Override
	public EntitySize getSize(Pose poseIn) {
		return EntitySize.flexible(0.5f, 0.5f);
	}
	
	@Override
	public int getMaxLiveTick() {
		int lvl = this.getPlantLvl();
		if(lvl <= 19) {
			return 1485 + 15 * lvl;
		}
		return 1800;
	}
	
	public int getHelpRange() {
		if(this.isPlantInStage(1)) return 3;
		if(this.isPlantInStage(2)) return 4;
		return 6;
	}

	@Override
	public int getSuperTimeLength() {
		if(this.isPlantInStage(1)) return 40;
		if(this.isPlantInStage(2)) return 50;
		return 60;
	}
	
	@Override
	public float getPlantHealth() {
		int lvl = this.getPlantLvl();
		if(lvl <= 19) {
			return 29 + lvl;
		}
		return 50;
	}
	
	@Override
	public float getShootRange() {
		return 10;
	}
	
	@Override
	public Plants getPlantEnumName() {
		return Plants.PUFF_SHROOM;
	}

}
