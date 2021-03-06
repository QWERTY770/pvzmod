package com.hungteen.pvz.entity.bullet;

import java.util.UUID;

import javax.annotation.Nullable;

import com.hungteen.pvz.PVZConfig;
import com.hungteen.pvz.entity.plant.base.PlantShooterEntity;
import com.hungteen.pvz.utils.EntityUtil;

import it.unimi.dsi.fastutil.ints.IntOpenHashSet;
import net.minecraft.block.Block;
import net.minecraft.block.BushBlock;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.IProjectile;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.projectile.ProjectileHelper;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.NBTUtil;
import net.minecraft.network.IPacket;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.EntityRayTraceResult;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RayTraceContext;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.network.NetworkHooks;

public abstract class AbstractBulletEntity extends Entity implements IProjectile {

	protected LivingEntity owner;
	private UUID ownerId;
	protected IntOpenHashSet hitEntities;
	
	public AbstractBulletEntity(EntityType<?> type, World worldIn) {
		super(type, worldIn);
	}

	public AbstractBulletEntity(EntityType<?> type, World worldIn, LivingEntity livingEntityIn) {
		this(type, worldIn);
		this.owner = livingEntityIn;
		this.ownerId = livingEntityIn.getUniqueID();
	}
	
	@Override
	protected void registerData() {
	}

	/**
	 * Checks if the entity is in range to render.
	 */
	@OnlyIn(Dist.CLIENT)
	public boolean isInRangeToRenderDist(double distance) {
		double d0 = this.getBoundingBox().getAverageEdgeLength() * 4.0D;
		if (Double.isNaN(d0)) {
			d0 = 4.0D;
		}

		d0 = d0 * 64.0D;
		return distance < d0 * d0;
	}

	/**
	 * Updates the entity motion clientside, called by packets from the server
	 */
	@OnlyIn(Dist.CLIENT)
	public void setVelocity(double x, double y, double z) {
		this.setMotion(x, y, z);
		if (this.prevRotationPitch == 0.0F && this.prevRotationYaw == 0.0F) {
			float f = MathHelper.sqrt(x * x + z * z);
			this.rotationYaw = (float) (MathHelper.atan2(x, z) * (double) (180F / (float) Math.PI));
			this.rotationPitch = (float) (MathHelper.atan2(y, (double) f) * (double) (180F / (float) Math.PI));
			this.prevRotationYaw = this.rotationYaw;
			this.prevRotationPitch = this.rotationPitch;
			this.setLocationAndAngles(this.getPosX(), this.getPosY(), this.getPosZ(), this.rotationYaw,
					this.rotationPitch);
		}
	}

	/**
	 * Called to update the entity's position/logic.
	 */
	public void tick() {
		super.tick();
		if (! world.isRemote && this.ticksExisted >= this.getMaxLiveTick()) {
			this.remove();
		}
		if(this.ticksExisted <= 10) {
			this.recalculateSize();
		}
		//on hit
		Vec3d start = this.getPositionVec();
		Vec3d end = start.add(this.getMotion());
		RayTraceResult result = this.world.rayTraceBlocks(new RayTraceContext(start, end, RayTraceContext.BlockMode.COLLIDER, RayTraceContext.FluidMode.NONE, this));
		if(result.getType() != RayTraceResult.Type.MISS) {// hit something
			end = result.getHitVec();
		}
		EntityRayTraceResult entityRay = this.rayTraceEntities(start, end);
		if(entityRay != null) {
			result = entityRay;
		}
		if(result != null && result.getType() != RayTraceResult.Type.MISS && !net.minecraftforge.event.ForgeEventFactory.onProjectileImpact(this, result)) {//on hit 
//			System.out.println(result.getType());
			this.onImpact(result);
		}
		//move
		Vec3d vec3d = this.getMotion();
		double d0 = this.getPosX() + vec3d.x;
		double d1 = this.getPosY() + vec3d.y;
		double d2 = this.getPosZ() + vec3d.z;
		float f = MathHelper.sqrt(horizontalMag(vec3d));
		this.rotationYaw = (float) (MathHelper.atan2(vec3d.x, vec3d.z) * (double) (180F / (float) Math.PI));
		for (this.rotationPitch = (float) (MathHelper.atan2(vec3d.y, (double) f)
				* (double) (180F / (float) Math.PI)); this.rotationPitch
						- this.prevRotationPitch < -180.0F; this.prevRotationPitch -= 360.0F) {
			;
		}
		while (this.rotationPitch - this.prevRotationPitch >= 180.0F) {
			this.prevRotationPitch += 360.0F;
		}
		while (this.rotationYaw - this.prevRotationYaw < -180.0F) {
			this.prevRotationYaw -= 360.0F;
		}
		while (this.rotationYaw - this.prevRotationYaw >= 180.0F) {
			this.prevRotationYaw += 360.0F;
		}
		this.rotationPitch = MathHelper.lerp(0.2F, this.prevRotationPitch, this.rotationPitch);
		this.rotationYaw = MathHelper.lerp(0.2F, this.prevRotationYaw, this.rotationYaw);
		float f1;
		if (this.isInWater()) {
			for (int i = 0; i < 4; ++i) {
				this.world.addParticle(ParticleTypes.BUBBLE, d0 - vec3d.x * 0.25D, d1 - vec3d.y * 0.25D,
						d2 - vec3d.z * 0.25D, vec3d.x, vec3d.y, vec3d.z);
			}
			f1 = 0.8F;
		} else {
			f1 = 0.99F;
		}
		this.setMotion(vec3d.scale((double) f1));
		if (!this.hasNoGravity()) {
			Vec3d vec3d1 = this.getMotion();
			this.setMotion(vec3d1.x, vec3d1.y - (double) this.getGravityVelocity(), vec3d1.z);
		}
		this.setPosition(d0, d1, d2);
	}

	protected void addHitEntity(Entity entity) {
		this.hitEntities.addAll(EntityUtil.getOwnerAndPartsID(entity));
	}
	
	protected boolean checkCanAttack(Entity target){
		return EntityUtil.checkCanEntityAttack(getThrower(), target);
	}
	
	protected boolean shouldHit(Entity target) {
		return EntityUtil.checkCanEntityAttack(getThrower(), target);
	}
	
	/**
	 * shoot bullet such as pea or spore
	 */
	public void shootPea(double dx, double dy, double dz, double speed) {
		double down = this.getShootPeaAngle();
		double dxz = Math.sqrt(dx * dx + dz * dz);
		dy = MathHelper.clamp(dy, - dxz / down, dxz / down);
		double dis = Math.sqrt(dx * dx + dy * dy + dz * dz);
		double vx = dx / dis * speed;
		double vy = dy / dis * speed;
		double vz = dz / dis * speed;
		this.setMotion(vx, vy, vz);
	}
	
	public void shootPeaAnti(double dx, double dy, double dz, double speed) {
		double down = this.getShootPeaAngle();
		double dxz = Math.sqrt(dx * dx + dz * dz);
		dy = MathHelper.clamp(dy, -dxz / down, dxz / down);
		double dis = Math.sqrt(dx * dx + dy * dy + dz * dz);
		double vx = dx / dis * speed;
		double vy = dy / dis * speed;
		double vz = dz / dis * speed;
		this.setMotion(- vx, - vy, - vz);
	}

	/**
	 * get how much angle can shoot by thrower
	 */
	protected double getShootPeaAngle() {
		if (this.getThrower() instanceof PlantShooterEntity) {
			return ((PlantShooterEntity) this.getThrower()).getMaxShootAngle();
		}
		return 10;
	}
	
	/**
	 * Gets the EntityRayTraceResult representing the entity hit
	 */
	@Nullable
	protected EntityRayTraceResult rayTraceEntities(Vec3d startVec, Vec3d endVec) {
		return ProjectileHelper.rayTraceEntities(this.world, this, startVec, endVec, 
				this.getBoundingBox().expand(this.getMotion()).grow(1.0D), (entity) -> {
			return entity.canBeCollidedWith() && shouldHit(entity)
							&& (this.hitEntities == null|| !this.hitEntities.contains(entity.getEntityId()));
		});
	}

	/**
	 * Gets the amount of gravity to apply to the thrown entity with each tick.
	 */
	protected float getGravityVelocity() {
		return 0.03F;
	}

	/**
	 * Called when this EntityThrowable hits a block or entity.
	 */
	protected abstract void onImpact(RayTraceResult result);

	public void writeAdditional(CompoundNBT compound) {
		if (this.ownerId != null) {
			compound.put("owner", NBTUtil.writeUniqueId(this.ownerId));
		}
	}

	/**
	 * (abstract) Protected helper method to read subclass entity data from NBT.
	 */
	public void readAdditional(CompoundNBT compound) {
		this.owner = null;
		if (compound.contains("owner", 10)) {
			this.ownerId = NBTUtil.readUniqueId(compound.getCompound("owner"));
		}
	}

	@SuppressWarnings("deprecation")
	@Nullable
	public LivingEntity getThrower() {
		if ((this.owner == null || this.owner.removed) && this.ownerId != null && this.world instanceof ServerWorld) {
			Entity entity = ((ServerWorld) this.world).getEntityByUuid(this.ownerId);
			if (entity instanceof LivingEntity) {
				this.owner = (LivingEntity) entity;
			} else {
				this.owner = null;
			}
		}
		return this.owner;
	}
	
	protected int getMaxLiveTick() {
		return PVZConfig.COMMON_CONFIG.EntitySettings.EntityLiveTick.BulletLiveTick.get();
	}
	
	protected boolean checkLive(RayTraceResult result) {
		if (result.getType() == RayTraceResult.Type.ENTITY) {// attack entity
			if (EntityUtil.checkCanEntityAttack(getThrower(), ((EntityRayTraceResult) result).getEntity())) {
				return false;
			}
			return true;
		} else if (result.getType() == RayTraceResult.Type.BLOCK) {
			Block block = world.getBlockState(((BlockRayTraceResult) result).getPos()).getBlock();
			if (block instanceof BushBlock) {
				return true;
			}
		}
		return false;
	}

	@Override
	public void shoot(double x, double y, double z, float velocity, float inaccuracy) {
	}
	
	@Override
	public boolean canBeAttackedWithItem() {
		return false;
	}

	public IPacket<?> createSpawnPacket() {
		return NetworkHooks.getEntitySpawningPacket(this);
	}
}
