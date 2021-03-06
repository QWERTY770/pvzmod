package com.hungteen.pvz.register;

import java.util.UUID;

import com.hungteen.pvz.PVZMod;
import com.hungteen.pvz.potion.PVZEffect;
import com.hungteen.pvz.utils.enums.Colors;

import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.AttributeModifier.Operation;
import net.minecraft.potion.Effect;
import net.minecraft.potion.EffectType;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

public class EffectRegister {

	public static final DeferredRegister<Effect> EFFECTS = new DeferredRegister<>(ForgeRegistries.POTIONS, PVZMod.MOD_ID);

	public static final UUID COLD_EFFECT_UUID = UUID.fromString("968019bc-e212-11ea-87d0-0242ac130003");
	public static final UUID FROZEN_EFFECT_UUID = UUID.fromString("293e07aa-e213-11ea-87d0-0242ac130003");
	public static final UUID EXCITE_EFFECT_UUID = UUID.fromString("a211dbca-19f9-11eb-adc1-0242ac120002");
	public static final UUID LIGHT_EYE_EFFECT_UUID = UUID.fromString("aa7a51c2-3e73-11eb-b378-0242ac130002");
			
	public static final RegistryObject<Effect> COLD_EFFECT = EFFECTS.register("cold", ()->{
		return new PVZEffect(EffectType.HARMFUL, Colors.IRIS_BLUE)
		.addAttributesModifier(SharedMonsterAttributes.MOVEMENT_SPEED, 
		COLD_EFFECT_UUID.toString(), -0.05f, Operation.MULTIPLY_TOTAL);
	});
	
	public static final RegistryObject<Effect> FROZEN_EFFECT = EFFECTS.register("frozen", ()->{
		return new PVZEffect(EffectType.HARMFUL, Colors.ELECTRIC_BLUE)
		.addAttributesModifier(SharedMonsterAttributes.MOVEMENT_SPEED,
		FROZEN_EFFECT_UUID.toString(), -1f, Operation.MULTIPLY_TOTAL);
	});
	
	public static final RegistryObject<Effect> EXCITE_EFFECT = EFFECTS.register("excite", ()->{
		return new PVZEffect(EffectType.BENEFICIAL, Colors.GOLD_YELLOW);
	});
	
	public static final RegistryObject<Effect> LIGHT_EYE_EFFECT = EFFECTS.register("light_eye", ()->{
		return new PVZEffect(EffectType.BENEFICIAL, Colors.LITTLE_YELLOW1);
	});
	
}
