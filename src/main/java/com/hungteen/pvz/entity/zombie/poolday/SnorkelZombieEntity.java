package com.hungteen.pvz.entity.zombie.poolday;

import com.hungteen.pvz.entity.zombie.base.SwimmerZombieEntity;
import com.hungteen.pvz.utils.ZombieUtil;
import com.hungteen.pvz.utils.enums.Zombies;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.monster.MonsterEntity;
import net.minecraft.world.World;

public class SnorkelZombieEntity extends SwimmerZombieEntity {

	public SnorkelZombieEntity(EntityType<? extends MonsterEntity> type, World worldIn) {
		super(type, worldIn);
	}

	@Override
	protected void registerAttributes() {
		super.registerAttributes();
		this.getAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(ZombieUtil.LITTLE_FAST);
	}

	@Override
	public float getLife() {
		return 20;
	}
	
	@Override
	public Zombies getZombieEnumName() {
		return Zombies.SNORKEL_ZOMBIE;
	}

}
