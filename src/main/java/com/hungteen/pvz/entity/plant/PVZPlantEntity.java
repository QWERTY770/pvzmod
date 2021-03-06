package com.hungteen.pvz.entity.plant;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import javax.annotation.Nullable;

import com.hungteen.pvz.PVZConfig;
import com.hungteen.pvz.entity.ai.PVZLookRandomlyGoal;
import com.hungteen.pvz.entity.drop.SunEntity;
import com.hungteen.pvz.entity.plant.enforce.SquashEntity;
import com.hungteen.pvz.entity.plant.light.GoldLeafEntity;
import com.hungteen.pvz.entity.plant.spear.SpikeWeedEntity;
import com.hungteen.pvz.entity.zombie.grassnight.TombStoneEntity;
import com.hungteen.pvz.entity.zombie.poolnight.BalloonZombieEntity;
import com.hungteen.pvz.entity.zombie.poolnight.DiggerZombieEntity;
import com.hungteen.pvz.item.tool.card.PlantCardItem;
import com.hungteen.pvz.misc.damage.PVZDamageSource;
import com.hungteen.pvz.misc.damage.PVZDamageType;
import com.hungteen.pvz.register.EntityRegister;
import com.hungteen.pvz.register.ParticleRegister;
import com.hungteen.pvz.register.SoundRegister;
import com.hungteen.pvz.utils.EntityUtil;
import com.hungteen.pvz.utils.PlantUtil;
import com.hungteen.pvz.utils.PlayerUtil;
import com.hungteen.pvz.utils.enums.Essences;
import com.hungteen.pvz.utils.enums.Plants;
import com.hungteen.pvz.utils.enums.Ranks;
import com.hungteen.pvz.utils.interfaces.IPVZPlant;

import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.entity.CreatureEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ILivingEntityData;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.MobEntity;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.server.management.PreYggdrasilConverter;
import net.minecraft.util.DamageSource;
import net.minecraft.util.Hand;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.GameRules;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;

public abstract class PVZPlantEntity extends CreatureEntity implements IPVZPlant{

	private static final DataParameter<Integer> SUPER_TIME = EntityDataManager.createKey(PVZPlantEntity.class, DataSerializers.VARINT);
	private static final DataParameter<Integer> PLANT_LVL = EntityDataManager.createKey(PVZPlantEntity.class, DataSerializers.VARINT);
	private static final DataParameter<Optional<UUID>> OWNER_UUID = EntityDataManager.createKey(PVZPlantEntity.class, DataSerializers.OPTIONAL_UNIQUE_ID);
//	private static final DataParameter<Byte> PLANT_STATES = EntityDataManager.createKey(PVZPlantEntity.class, DataSerializers.BYTE);
	private static final DataParameter<Integer> ATTACK_TIME = EntityDataManager.createKey(PVZPlantEntity.class, DataSerializers.VARINT);
	private static final DataParameter<Integer> GOLD_TIME = EntityDataManager.createKey(PVZPlantEntity.class, DataSerializers.VARINT);
	private static final DataParameter<Integer> BOOST_TIME = EntityDataManager.createKey(PVZPlantEntity.class, DataSerializers.VARINT);
	private static final DataParameter<Boolean> IS_CHARMED = EntityDataManager.createKey(PVZPlantEntity.class, DataSerializers.BOOLEAN);
	private static final DataParameter<Integer> SLEEP_TIME = EntityDataManager.createKey(PVZPlantEntity.class, DataSerializers.VARINT);
	private static final DataParameter<Integer> LIVE_TICK = EntityDataManager.createKey(PVZPlantEntity.class, DataSerializers.VARINT);
	private static final DataParameter<Float> PUMPKIN_LIFE = EntityDataManager.createKey(PVZPlantEntity.class, DataSerializers.FLOAT);
	protected int weakTime = 0;
	protected boolean isImmuneToWeak = false;
	private final int weakCD = 10;
	private final int weakDamage = 15;
	protected Optional<Plants> outerPlant = Optional.empty();
	public boolean canCollideWithPlant = true;
	protected boolean canBeCharmed = true;
	public int plantSunCost = 0;
	public int outerSunCost = 0;
	
	public PVZPlantEntity(EntityType<? extends CreatureEntity> type, World worldIn) {
		super(type, worldIn);
		this.recalculateSize();
	}
	
	@Override
	protected void registerData() {
		super.registerData();
		dataManager.register(SUPER_TIME, 0);
		dataManager.register(PLANT_LVL, 1);
		dataManager.register(OWNER_UUID, Optional.empty());
		dataManager.register(ATTACK_TIME, 0);
		dataManager.register(GOLD_TIME, 0);
		dataManager.register(BOOST_TIME, 0);
		dataManager.register(IS_CHARMED, false);
		dataManager.register(SLEEP_TIME, 0);
		this.dataManager.register(LIVE_TICK, 0);
		this.dataManager.register(PUMPKIN_LIFE, 0f);
	}
	
	@Override
	protected void registerAttributes() {
		super.registerAttributes();
		this.getAttributes().registerAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).setBaseValue(0);
		this.getAttributes().registerAttribute(SharedMonsterAttributes.ATTACK_SPEED).setBaseValue(0);
        this.getAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(0d);
        this.getAttribute(SharedMonsterAttributes.KNOCKBACK_RESISTANCE).setBaseValue(1d);
	}
	
	@Override
	protected void registerGoals() {
		this.goalSelector.addGoal(2, new PVZLookRandomlyGoal(this));
	}
	
	/**
	 * init attributes with plant lvl
	 */
	public void onSpawnedByPlayer(PlayerEntity player, int lvl) {
		this.setPlantLvl(lvl);
		this.setOwnerUUID(player.getUniqueID());
		this.getAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(this.getPlantHealth());
		this.heal(this.getMaxHealth());
	}
	
	@Override
	public float getPlantHealth() {
		int lvl = this.getPlantLvl();
		if(lvl <= 14) {
			return 27.5f + 2.5f * lvl;
		} 
		return 5 * lvl - 10;
	}
	
	@Override
	public void livingTick() {
		super.livingTick();
		if(! this.isAlive()) {
			return ;
		}
		this.plantBaseTick();
		if(this.canPlantNormalUpdate()) {
			   this.normalPlantTick();
		}
	}
	
	public boolean canPlantNormalUpdate() {
		return ! this.isPlantSleeping();
	}
	
	/**
	 * base tick for normal plant
	 */
    protected void plantBaseTick() {
    	//when plant stand on wrong block
		if(!this.world.isRemote && !this.isImmuneToWeak && this.getRidingEntity() == null) {
			if(this.checkNormalPlantWeak() && this.weakTime == 0) {
				this.weakTime = this.weakCD;
				this.attackEntityFrom(PVZDamageSource.causeWeakDamage(this, this), this.weakDamage);
			}
			if(this.weakTime > 0) {
				this.weakTime --;
			}
		}
		//super mode or boost time or sleep time
		if(!this.world.isRemote) {
			//super 
		    if(this.getSuperTime() > 0) {
			    this.setSuperTime(this.getSuperTime() - 1);
		    }
		    //boost
		    if(this.getBoostTime() > 0) {
		    	this.setBoostTime(this.getBoostTime() - 1);
		    }
		    //sleep
		    if(this.shouldPlantRegularSleep()) {
		    	if(this.getSleepTime() < 0) {
		    		this.setSleepTime(this.getSleepTime() + 1);
		    	}else {
		    		this.setSleepTime(Math.max(1, this.getSleepTime()));
		    	}
		    }else {
		    	if(this.getSleepTime() > 0) {
		    		this.setSleepTime(this.getSleepTime() - 1);
		    	}
		    }
		}
		//spawn sleep particle
		if(world.isRemote && this.isPlantSleeping() && this.ticksExisted % 20 == 0) {
			world.addParticle(ParticleRegister.SLEEP.get(), this.getPosX(), this.getPosY() + this.getEyeHeight(), this.getPosZ(), 0.05, 0.05, 0.05);
		}
		//max live tick
		if(! world.isRemote) {
			this.setLiveTick(this.getLiveTick() + 1);
		    if(this.getLiveTick() >= this.getMaxLiveTick()) {//it's time to disappear 
			    this.remove();
		    }
		}
		//lock the x and z of plant
		if(this.shouldLockXZ()) {
		    if(this.getRidingEntity() != null) {
		    }else {
			    BlockPos pos = this.getPosition();
		        this.setPosition(pos.getX() + 0.5, this.getPosY(), pos.getZ() + 0.5);
		    }
		}
		if(! world.isRemote) {
			if(this.getPlantEnumName().isWaterPlant && this.isInWater()) {
				Vec3d vec = this.getMotion();
				double speedY = Math.min(vec.y, 0.05D);
				this.setMotion(vec.x, speedY, vec.z);
			}
		}
    }
	
	/**
	 * tick for normal plant
	 */
	protected void normalPlantTick(){
		if(! this.world.isRemote && this.getGoldTime() < GoldLeafEntity.GOLD_GEN_CD) {
			Block block = this.world.getBlockState(this.getPosition().down()).getBlock();
			int lvl = GoldLeafEntity.getBlockGoldLevel(block);
			if(lvl <= 0) return ;
			this.setGoldTime(this.getGoldTime() + 1);
			if(this.getGoldTime() >= GoldLeafEntity.GOLD_GEN_CD) {
				this.setGoldTime(0);
				SunEntity sun = EntityRegister.SUN.get().create(world);
				sun.setAmount(GoldLeafEntity.getGoldGenAmount(lvl));
				EntityUtil.onMobEntityRandomPosSpawn(world, sun, getPosition(), 2);
				EntityUtil.playSound(this, SoundEvents.ENTITY_EXPERIENCE_ORB_PICKUP);
			}
		}
	}
	
	@Override
	public ILivingEntityData onInitialSpawn(IWorld worldIn, DifficultyInstance difficultyIn, SpawnReason reason,ILivingEntityData spawnDataIn, CompoundNBT dataTag) {
		if(! worldIn.isRemote()) {
			EntityUtil.playSound(this, this.getSpawnSound());
			this.getAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(this.getPlantHealth());
			this.heal(this.getMaxHealth());
		}
		return super.onInitialSpawn(worldIn, difficultyIn, reason, spawnDataIn, dataTag);
	}
	
	
	/**
	 * check if the plant can stand on the current position
	 */
	protected boolean checkNormalPlantWeak(){
		if(this.isImmuneToWeak) {
			return false;
		}
		if(this.getPlantEnumName().isWaterPlant) {
			return ! this.isInWater() && world.getBlockState(getPosition()).getBlock() != Blocks.WATER;
		} else {
			if(! this.onGround) {
				return false;
			}
			double y1 = this.getPosY();
			double y2 = MathHelper.floor(y1);
			BlockPos pos = (Math.abs(y1 - y2) <= 0.01D ? this.getPosition().down() : this.getPosition());
			Block current = world.getBlockState(pos).getBlock();
			return ! PlantUtil.getPlantSuitBlock(getPlantEnumName()).stream().anyMatch((block) -> {return block == current;});
		}
	}
	
	/**
	 * some zombie can attack but can not target.
	 * such as digger zombie.
	 */
	public boolean checkCanPlantTarget(LivingEntity entity){
		return EntityUtil.checkCanEntityAttack(this, entity) && this.canPlantTarget(entity);
	}
	
	/**
	 * use to extends for specific plants.
	 */
	protected boolean canPlantTarget(LivingEntity entity) {
		if(entity instanceof DiggerZombieEntity) {
			return ((DiggerZombieEntity) entity).getAnimTime() == DiggerZombieEntity.MAX_ANIM_TIME;
		} else if(entity instanceof BalloonZombieEntity) {
			return ! ((BalloonZombieEntity) entity).hasBalloon();
		}
		return true;
	}
	
	/**
	 * use for shroom's sleep ,need check for later coffee bean update
	 */
	protected boolean shouldPlantRegularSleep() {
		if(this.getPlantEnumName().isShroomPlant) {
			return world.isDaytime();
		}
		return false;
	}
	
	@Override
	public boolean attackEntityFrom(DamageSource source, float amount) {
		if(source instanceof PVZDamageSource) {
			this.hurtResistantTime=0;
		}
		amount = this.pumpkinReduceDamage(source, amount);
		return super.attackEntityFrom(source, amount);
	}

	@Override
	public boolean attackEntityAsMob(Entity entityIn) {
		entityIn.hurtResistantTime=0;
		return super.attackEntityAsMob(entityIn);
	}
	
	/**
	 * lock the movement of plant
	 */
	protected boolean shouldLockXZ() {
		return true;
	}
	
	@Override
	public void applyEntityCollision(Entity entityIn) {
		if (!this.isRidingSameEntity(entityIn)){
            if (!entityIn.noClip && !this.noClip){
            	if(entityIn instanceof PVZPlantEntity&&!EntityUtil.checkCanEntityAttack(this, entityIn)) {
            		this.attackEntityFrom(DamageSource.CRAMMING, 6.0F);
            		entityIn.attackEntityFrom(DamageSource.CRAMMING, 6.0F);
            	}
                double d0 = entityIn.getPosX() - this.getPosX();
                double d1 = entityIn.getPosZ() - this.getPosZ();
                double d2 = MathHelper.absMax(d0, d1);
                if (d2 >= 0.009999999776482582D){//collide from out to in,add velocity to out
                    d2 = (double)MathHelper.sqrt(d2);
                    d0 = d0 / d2;
                    d1 = d1 / d2;
                    double d3 = 1.0D / d2;
                    if (d3 > 1.0D){
                        d3 = 1.0D;
                    }
                    d0 = d0 * d3;
                    d1 = d1 * d3;
                    d0 = d0 * 0.05000000074505806D;
                    d1 = d1 * 0.05000000074505806D;
                    d0 = d0 * (double)(1.0F - this.entityCollisionReduction);
                    d1 = d1 * (double)(1.0F - this.entityCollisionReduction);
                    if (!entityIn.isBeingRidden()){
                        entityIn.addVelocity(d0, 0.0D, d1);
                    }
                }else {
                	if(this instanceof PVZPlantEntity && entityIn instanceof PVZPlantEntity) {
                	    this.attackEntityFrom(DamageSource.CRAMMING, 10);
                	    entityIn.attackEntityFrom(DamageSource.CRAMMING, 10);
                	}
                }
            }
        }
	}
	
	@Override
	public boolean canBePushed() {
		return false;
	}
	
	@Override
	public boolean isPushedByWater() {
		return ! this.getPlantEnumName().isWaterPlant;
	}
	
	@Override
	protected void collideWithNearbyEntities() {
		List<LivingEntity> list = this.world.getEntitiesWithinAABB(LivingEntity.class, this.getBoundingBox());
		if (!list.isEmpty()){
            int i = this.world.getGameRules().getInt(GameRules.MAX_ENTITY_CRAMMING);
            if (i > 0 && list.size() > i - 1 && this.rand.nextInt(4) == 0){
                int j = 0;
                for (int k = 0; k < list.size(); ++k){
                    if (!((Entity)list.get(k)).isPassenger()){
                        ++j;
                    }
                }
                if (j > i - 1){
                    this.attackEntityFrom(DamageSource.CRAMMING, 6.0F);
                }
            }
            for (int l = 0; l < list.size(); ++l){
                LivingEntity target = list.get(l);
                if(target != this && shouldCollideWithEntity(target)) {//can collide with
                    this.collideWithEntity(target);
                }
            }
        }
	}
	
	/**
	 * common plants collide with common plants,
	 * mobs who target them,tombstone.
	 */
	protected boolean shouldCollideWithEntity(LivingEntity target) {
		if(target instanceof PVZPlantEntity) {
			if(! this.canCollideWithPlant || ! ((PVZPlantEntity) target).canCollideWithPlant) {
				return false;
			}
			if(target instanceof SquashEntity) {
				return !EntityUtil.checkCanEntityAttack(this, target);
			}
			if(target instanceof SpikeWeedEntity) {
				return !EntityUtil.checkCanEntityAttack(this, target);
			}
			return true;
		}
		if(target instanceof MobEntity) {
			if(((MobEntity) target).getAttackTarget() == this) {
				return true;
			}
			if(target instanceof TombStoneEntity) {
				return true;
			}
		}
		return false;
	}
	
	@Override
	public boolean isInvulnerableTo(DamageSource source) {
		return this.isPlantImmuneTo(source) && source != DamageSource.OUT_OF_WORLD && !source.isCreativePlayer();
	}
	
	/**
	 * a function that replace isinvulnerable
	 */
	public boolean isPlantImmuneTo(DamageSource source) {
		if(this.isPlantInSuperMode()) {
			return true;
		}
		return false;
	}
	
	@Override
	public boolean canBreatheUnderwater() {
		return this.getPlantEnumName().isWaterPlant;
	}
	
	@Nullable
	public Plants getUpgradePlantType() {
		return null;
	}
	
	public void onPlantBeCharmed() {
		if(! this.canBeCharmed) return ;
		this.setCharmed(! this.isCharmed());
	}
	
	@Override
	public void writeAdditional(CompoundNBT compound) {
		super.writeAdditional(compound);
		compound.putInt("plant_weak_time", this.weakTime);
		compound.putInt("plant_super_time", this.getSuperTime());
		compound.putInt("plant_lvl", this.getPlantLvl());
		if (this.getOwnerUUID().isPresent()) {
	         compound.putString("OwnerUUID", this.getOwnerUUID().get().toString());
	    } else {
	         compound.putString("OwnerUUID", "");
	    }
        compound.putInt("plant_attack_time", this.getAttackTime());
        compound.putInt("plant_gold_time", this.getGoldTime());
        compound.putInt("plant_boost_time", this.getBoostTime());
        compound.putBoolean("is_plant_charmed", this.isCharmed());
        compound.putInt("plant_sleep_time", this.getSleepTime());
        compound.putInt("plant_live_tick", this.getLiveTick());
        this.outerPlant.ifPresent((plant) -> {
        	compound.putInt("outer_plant_type", plant.ordinal());
        });
        compound.putFloat("pumpkin_life", this.getPumpkinLife());
        compound.putInt("plant_sun_cost", this.plantSunCost);
        compound.putInt("outer_sun_cost", this.outerSunCost);
        compound.putBoolean("immune_to_weak", this.isImmuneToWeak);
	}
	
	@Override
	public void readAdditional(CompoundNBT compound) {
		super.readAdditional(compound);
		if(compound.contains("plant_weak_time")) {
			this.weakTime = compound.getInt("plant_weak_time");
		}
		if(compound.contains("plant_super_time")) {
			this.setSuperTime(compound.getInt("plant_super_time"));
		}
		if(compound.contains("plant_lvl")) {
			this.setPlantLvl(compound.getInt("plant_lvl"));
		}
		String s;
	    if (compound.contains("OwnerUUID", 8)) {
	        s = compound.getString("OwnerUUID");
	    } else {
	        String s1 = compound.getString("Owner");
	        s = PreYggdrasilConverter.convertMobOwnerIfNeeded(this.getServer(), s1);
	    }
	    if (!s.isEmpty()) {
	        try {
	            this.setOwnerUUID(UUID.fromString(s));
	        } catch (Throwable var4) {
	        }
	    }
	    if(compound.contains("plant_attack_time")) {
			this.setAttackTime(compound.getInt("plant_attack_time"));
		}
	    if(compound.contains("plant_gold_time")) {
			this.setGoldTime(compound.getInt("plant_gold_time"));
		}
	    if(compound.contains("plant_boost_time")) {
			this.setBoostTime(compound.getInt("plant_boost_time"));
		}
	    if(compound.contains("is_plant_charmed")) {
			this.setCharmed(compound.getBoolean("is_plant_charmed"));
		}
	    if(compound.contains("plant_sleep_time")) {
			this.setSleepTime(compound.getInt("plant_sleep_time"));
		}
	    if(compound.contains("plant_live_tick")) {
	    	this.setLiveTick(compound.getInt("plant_live_tick"));
	    }
	    if(compound.contains("pumpkin_life")) {
	    	this.setPumpkinLife(compound.getFloat("pumpkin_life"));
	    }
        if(compound.contains("outer_plant_type")) {
        	this.outerPlant = Optional.of(Plants.values()[compound.getInt("outer_plant_type")]);
        }
        if(compound.contains("plant_sun_cost")) {
	    	this.plantSunCost = compound.getInt("plant_sun_cost");
	    }
        if(compound.contains("outer_sun_cost")) {
	    	this.outerSunCost = compound.getInt("outer_sun_cost");
	    }
        if(compound.contains("immune_to_weak")) {
	    	this.isImmuneToWeak = compound.getBoolean("immune_to_weak");
	    }
    }
	
	/**
	 * use to start plant super mode 
	 */
	public void startSuperMode(boolean first) {
		this.setSuperTime(this.getSuperTimeLength());
		this.heal(this.getMaxHealth());
		this.setLiveTick(0);
		if(first) {
			if(this.getOwnerUUID().isPresent()) {
			    PlayerEntity player = this.world.getPlayerByUuid(this.getOwnerUUID().get());
		        if(player != null) {
		    	    PlayerUtil.addPlantXp(player, this.getPlantEnumName(), 5);
		        }
			}
		    this.outerPlant.ifPresent((plant) -> {
		    	if(plant == Plants.PUMPKIN) {
		    		this.setPumpkinLife(PlantUtil.PUMPKIN_LIFE + PlantUtil.PUMPKIN_SUPER_LIFE);
		    	}
		    });
		}
	}
	
	/**
	 * pumpkin reduce the hurt damage
	 */
	protected float pumpkinReduceDamage(DamageSource source, float amount) {
		if(! world.isRemote) {
			if(this.outerPlant.isPresent() && this.outerPlant.get() == Plants.PUMPKIN) {
				if(this.getPumpkinLife() > amount) { // damage pumpkin health first
					this.setPumpkinLife(this.getPumpkinLife() - amount);
					amount = 0;
				}else {
					amount -= this.getPumpkinLife();
					this.setPumpkinLife(0f);
					this.outerPlant = Optional.empty();
				}
			}
		}
		return amount;
	}
	
	/**
	 * how many tick can plant live
	 */
	public int getMaxLiveTick() {
		int tick = PVZConfig.COMMON_CONFIG.EntitySettings.EntityLiveTick.PlantLiveTick.get();
//		System.out.println(tick);
		return this.getPlantEnumName().isUpgradePlant ? 2 * tick : tick;
	}
	
	public boolean isPlantInSuperMode(){
		return this.getSuperTime() > 0;
	}
	
	/**
	 * check can start super mode currently
	 */
	public boolean canStartSuperMode(){
		return ! this.isPlantSleeping() && this.hasSuperMode() && ! this.isPlantInSuperMode();
	}
	
	private boolean hasSuperMode() {
		return this.getSuperTimeLength()>0;
	}
	
	public boolean isPlantInBoost(){
		return this.getBoostTime()>0;
	}
	
	public int getCoolDownTime(){
		return PlantUtil.getPlantCoolDownTime(getPlantEnumName(),getPlantLvl());
	}
	
	public int getSunCost(){
		return PlantUtil.getPlantSunCost(getPlantEnumName());
	}
	
	@Override
	public Essences getPlantEssenceType() {
		return PlantUtil.getPlantEssenceType(getPlantEnumName());
	}
	
	@Override
	public Ranks getPlantRank(Plants plant) {
		return PlantUtil.getPlantRankByName(plant);
	}
	
	@Override
	public boolean canDespawn(double distanceToClosestPlayer) {
		return false;
	}

	/**
	 * is Plant Sleeping
	 */
	public boolean isPlantSleeping() {
		return this.getSleepTime() > 0;
	}
    
	public void setOuterPlantType(Plants p) {
		this.outerPlant = Optional.of(p);
	}
	
	public Optional<Plants> getOuterPlantType() {
		return this.outerPlant;
	}
	
	public void setImmunneToWeak(boolean is) {
		this.isImmuneToWeak = is;
	}
	
	public void removeOuterPlant() {
		this.outerPlant = Optional.empty();
		this.setPumpkinLife(0);
		this.outerSunCost = 0;
	}
	
	@Override
	protected boolean processInteract(PlayerEntity player, Hand hand) {
		if(! world.isRemote) {
			ItemStack stack = player.getHeldItem(hand);
			if(stack.getItem() instanceof PlantCardItem) {
				PlantCardItem item = (PlantCardItem) stack.getItem();
				if(item.plantType == Plants.PUMPKIN) {
					if(this.outerPlant.isPresent() && this.outerPlant.get() == Plants.PUMPKIN) {
						if(this.getPumpkinLife() < PlantUtil.PUMPKIN_LIFE) {
							PlantCardItem.checkSunAndHealPlant(player, this, item, stack);
						}
					} else {
						PlantCardItem.checkSunAndOuterPlant(player, this, item, stack);
					}
				} else if(item.plantType == Plants.COFFEE_BEAN) {
					PlantCardItem.checkSunAndSummonPlant(player, stack, item, getPosition(), (plantEntity) -> {
						plantEntity.startRiding(this);
					});
				} else if(this.getUpgradePlantType() == item.plantType) {
					PlantCardItem.checkSunAndSummonPlant(player, stack, item, getPosition(), (plantEntity) -> {
						this.remove();
					});
				}
			}
		}
		return true;
	}
	
	public float getCurrentHealth() {
		return this.getHealth() + this.getPumpkinLife();
	}
	
	protected boolean isPlantInStage(int stage) {
		int lvl = this.getPlantLvl();
		if(stage == 1) return lvl <= 6;
		if(stage == 2) return 7 <= lvl && lvl <= 13;
		if(stage == 3) return 14 <= lvl && lvl <= 20;
		return false;
	}
	
	public int getBoostTime(){
    	return dataManager.get(BOOST_TIME);
    }
    
    public void setBoostTime(int time){
    	dataManager.set(BOOST_TIME, time);
    }
    
    public boolean isCharmed(){
    	return dataManager.get(IS_CHARMED);
    }
    
    public void setCharmed(boolean is){
    	dataManager.set(IS_CHARMED,is);
    }
    
    public int getGoldTime(){
    	return dataManager.get(GOLD_TIME);
    }
    
    public void setGoldTime(int cd){
    	dataManager.set(GOLD_TIME, cd);
    }
    
    public int getAttackTime(){
    	return dataManager.get(ATTACK_TIME);
    }
    
    public void setAttackTime(int cd){
    	dataManager.set(ATTACK_TIME, cd);
    }
    
    public int getSleepTime(){
    	return dataManager.get(SLEEP_TIME);
    }
    
    public void setSleepTime(int cd){
    	dataManager.set(SLEEP_TIME, cd);
    }
    
	public void setPlantLvl(int lvl){
		dataManager.set(PLANT_LVL, lvl);
	}
	
	public int getPlantLvl(){
		return dataManager.get(PLANT_LVL);
	}
	
    public Optional<UUID> getOwnerUUID(){
        return dataManager.get(OWNER_UUID);
    }

    public void setOwnerUUID(UUID uuid){
        this.dataManager.set(OWNER_UUID, Optional.ofNullable(uuid));
    }
	
    public void setSuperTime(int time){
		dataManager.set(SUPER_TIME, time);
	}
	
	public int getSuperTime(){
		return dataManager.get(SUPER_TIME);
	}
	
	public int getLiveTick() {
		return this.dataManager.get(LIVE_TICK);
	}
	
	public void setLiveTick(int tick) {
		this.dataManager.set(LIVE_TICK, tick);
	}
	
	public float getPumpkinLife(){
		return this.dataManager.get(PUMPKIN_LIFE);
	}
	
	public void setPumpkinLife(float life){
		this.dataManager.set(PUMPKIN_LIFE, life);
	}
	
	@Override
	protected SoundEvent getHurtSound(DamageSource damageSourceIn) {
		if(damageSourceIn instanceof PVZDamageSource && ((PVZDamageSource)damageSourceIn).getPVZDamageType() == PVZDamageType.EAT) return SoundRegister.PLANT_HURT.get();
		return super.getHurtSound(damageSourceIn);
	}
	
	protected SoundEvent getSpawnSound() {
		return this.getPlantEnumName().isWaterPlant ? SoundRegister.PLANT_IN_WATER.get() : SoundRegister.PLANT_ON_GROUND.get();
	}
	
}
