package com.hungteen.pvz.register;

import com.hungteen.pvz.PVZMod;

import net.minecraft.particles.BasicParticleType;
import net.minecraft.particles.ParticleType;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

public class ParticleRegister {

	//dont forget register particle factory
	public static final DeferredRegister<ParticleType<?>> PARTICLE_TYPES = new DeferredRegister<>(ForgeRegistries.PARTICLE_TYPES, PVZMod.MOD_ID);
	
	public static final RegistryObject<BasicParticleType> RED_BOMB = PARTICLE_TYPES.register("red_bomb", ()->{return new BasicParticleType(false);});
	public static final RegistryObject<BasicParticleType> YELLOW_BOMB = PARTICLE_TYPES.register("yellow_bomb", ()->{return new BasicParticleType(false);});
	public static final RegistryObject<BasicParticleType> DIRT_BURST_OUT = PARTICLE_TYPES.register("dirt_burst_out", ()->{return new BasicParticleType(false);});
	public static final RegistryObject<BasicParticleType> YELLOW_FLAME = PARTICLE_TYPES.register("yellow_flame", ()->{return new BasicParticleType(false);});
	public static final RegistryObject<BasicParticleType> BLUE_FLAME = PARTICLE_TYPES.register("blue_flame", ()->{return new BasicParticleType(false);});
	public static final RegistryObject<BasicParticleType> SLEEP = PARTICLE_TYPES.register("sleep", ()->{return new BasicParticleType(false);});
	public static final RegistryObject<BasicParticleType> SPORE = PARTICLE_TYPES.register("spore", ()->{return new BasicParticleType(false);});
	public static final RegistryObject<BasicParticleType> FUME = PARTICLE_TYPES.register("fume", ()->{return new BasicParticleType(false);});
	public static final RegistryObject<BasicParticleType> SNOW_FLOWER = PARTICLE_TYPES.register("snow_flower", ()->{return new BasicParticleType(false);});
	public static final RegistryObject<BasicParticleType> DOOM = PARTICLE_TYPES.register("doom", ()->{return new BasicParticleType(false);});

}
