package com.hungteen.pvz.entity.zombie.part;

import com.hungteen.pvz.entity.zombie.base.DefenceZombieEntity;
import com.hungteen.pvz.misc.damage.PVZDamageSource;
import com.hungteen.pvz.misc.damage.PVZDamageType;
import com.hungteen.pvz.utils.EntityUtil;

import net.minecraft.entity.EntityType;
import net.minecraft.util.DamageSource;
import net.minecraft.util.SoundEvent;
import net.minecraft.world.World;

public class PVZHealthPartEntity extends PVZZombiePartEntity{

	protected DefenceZombieEntity zombie;
	
	public PVZHealthPartEntity(EntityType<?> entityTypeIn, World worldIn) {
		super(entityTypeIn, worldIn);
	}
	
	public PVZHealthPartEntity(DefenceZombieEntity owner, float sizeX, float sizeY) {
		super(owner, sizeX, sizeY);
		this.zombie = owner;
	}
	
	@Override
	public boolean attackEntityFrom(DamageSource source, float damage) {
		if(source instanceof PVZDamageSource) {
			if(((PVZDamageSource) source).getPVZDamageType() == PVZDamageType.THROUGH) {
				return super.attackEntityFrom(source, damage);
			}
			((PVZDamageSource) source).setDefended(true);
		}
		if(this.zombie.getDefenceLife() > 0) {
			SoundEvent sound = null;
			if(this.zombie.getDefenceLife() > damage) {
			    this.zombie.setDefenceLife(this.zombie.getDefenceLife() - damage);
			    sound = this.zombie.getPartHurtSound();
		    } else {
			    damage -= this.zombie.getDefenceLife();
			    this.zombie.setDefenceLife(0);
			    sound = this.zombie.getPartDeathSound();
		    }
			if(sound != null) {
				EntityUtil.playSound(this, sound);
			}
			return true;
		}
		return super.attackEntityFrom(source, damage);
	}
	
	@Override
	public boolean shouldNotExist() {
		return super.shouldNotExist() || this.zombie.getDefenceLife() == 0;
	}
	
}
