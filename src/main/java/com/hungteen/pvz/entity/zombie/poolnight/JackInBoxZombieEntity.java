package com.hungteen.pvz.entity.zombie.poolnight;

import com.hungteen.pvz.capability.CapabilityHandler;
import com.hungteen.pvz.entity.zombie.PVZZombieEntity;
import com.hungteen.pvz.misc.damage.PVZDamageSource;
import com.hungteen.pvz.register.SoundRegister;
import com.hungteen.pvz.utils.EntityUtil;
import com.hungteen.pvz.utils.PlayerUtil;
import com.hungteen.pvz.utils.ZombieUtil;
import com.hungteen.pvz.utils.enums.MetalTypes;
import com.hungteen.pvz.utils.enums.Zombies;
import com.hungteen.pvz.utils.interfaces.IHasMetal;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.monster.MonsterEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.world.Explosion;
import net.minecraft.world.World;

public class JackInBoxZombieEntity extends PVZZombieEntity implements IHasMetal {

	private static final DataParameter<Boolean> HAS_BOX = EntityDataManager.createKey(JackInBoxZombieEntity.class, DataSerializers.BOOLEAN);
	private static final DataParameter<Integer> ANIM_TICK = EntityDataManager.createKey(JackInBoxZombieEntity.class, DataSerializers.VARINT);
	public static final int MIN_ANIM_TICK = 20;
	
	public JackInBoxZombieEntity(EntityType<? extends MonsterEntity> type, World worldIn) {
		super(type, worldIn);
		if(! world.isRemote) {
			this.setExplosionTime();
		}
		this.setBox(true);
	}
	
	@Override
	protected void registerData() {
		super.registerData();
		this.dataManager.register(HAS_BOX, true);
		this.dataManager.register(ANIM_TICK, 0);
	}

	@Override
	public void normalZombieTick() {
		super.normalZombieTick();
		if(! world.isRemote) {
			if(this.getAnimTick() > 0) {
				if(this.getAnimTick() == MIN_ANIM_TICK) {
					EntityUtil.playSound(this, SoundRegister.JACK_SURPRISE.get());
				}
				this.setAnimTick(this.getAnimTick() - 1);
			}
			if(this.hasBox() && this.ticksExisted % 40 == 0) {
				world.getPlayers().forEach((player) -> {
					if(this.getDistanceSq(player) <= 400) {
						player.getCapability(CapabilityHandler.PLAYER_DATA_CAPABILITY).ifPresent((l) -> {
							if(l.getPlayerData().getOtherStats().playSoundTick == 0) {
								PlayerUtil.playClientSound(player, 10);
								l.getPlayerData().getOtherStats().playSoundTick = 300;
							}
						});
					}
				});
			}
		}
		if(this.hasBox()) {
			if(this.getAnimTick() == 1) {
			    if(world.isRemote) {
				    for(int i = 0; i < 2; ++ i) {
					    world.addParticle(ParticleTypes.EXPLOSION_EMITTER, this.getPosX(), this.getPosY(), this.getPosZ(), 0, 0, 0);
				    }
			    }
		    } else if(this.getAnimTick() == 0) {
			    if(! world.isRemote) {
				    this.doExplosion();
				    Explosion.Mode mode = net.minecraftforge.event.ForgeEventFactory.getMobGriefingEvent(this.world, this) ? Explosion.Mode.DESTROY : Explosion.Mode.NONE;
					this.world.createExplosion(this, getPosX(), getPosY(), getPosZ(), 3f, mode);
			    }
			    this.remove();
		    }
		}
	}
	
	@Override
	public int getTalkInterval() {
		return 200;
	}
	
	private void doExplosion() {
		EntityUtil.getEntityAttackableTarget(this, EntityUtil.getEntityAABB(this, 3, 3)).forEach((entity)->{
			if(entity instanceof LivingEntity) {
				entity.attackEntityFrom(PVZDamageSource.causeExplosionDamage(this, this), ((LivingEntity) entity).getMaxHealth() * 2);
			}
		});
		EntityUtil.playSound(this, SoundRegister.CAR_EXPLOSION.get());
	}
	
	@Override
	protected void registerAttributes() {
		super.registerAttributes();
		this.getAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(ZombieUtil.FAST);
	}
	
	private void setExplosionTime() {
		int min = 200, max = 2400;
	    this.setAnimTick(this.getRNG().nextInt(max - min + 1) + min);
	}
	
	@Override
	public float getLife() {
		return 40;
	}
	
	@Override
	public boolean hasMetal() {
		return this.hasBox();
	}

	@Override
	public void decreaseMetal() {
		this.setBox(false);
	}

	@Override
	public void increaseMetal() {
		this.setBox(true);
	}

	@Override
	public MetalTypes getMetalType() {
		return MetalTypes.JACK_BOX;
	}

	@Override
	public void readAdditional(CompoundNBT compound) {
		super.readAdditional(compound);
		if(compound.contains("has_jack_box")) {
			this.setBox(compound.getBoolean("has_jack_box"));
		}
		if(compound.contains("jack_anim_tick")) {
			this.setAnimTick(compound.getInt("jack_anim_tick"));
		}
	}
	
	@Override
	public void writeAdditional(CompoundNBT compound) {
		super.writeAdditional(compound);
		compound.putBoolean("has_jack_box", this.hasBox());
		compound.putInt("jack_anim_tick", this.getAnimTick());
	}
	
	public void setBox(boolean has) {
		this.dataManager.set(HAS_BOX, has);
	}
	
	public boolean hasBox() {
		return this.dataManager.get(HAS_BOX);
	}
	
	public void setAnimTick(int tick) {
		this.dataManager.set(ANIM_TICK, tick);
	}
	
	public int getAnimTick() {
		return this.dataManager.get(ANIM_TICK);
	}
	
	@Override
	public Zombies getZombieEnumName() {
		return Zombies.JACK_IN_BOX_ZOMBIE;
	}

}
