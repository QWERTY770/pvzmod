package com.hungteen.pvz.render.entity.creature;

import com.hungteen.pvz.entity.creature.FoodieZombieEntity;
import com.hungteen.pvz.model.entity.animal.FoodieZombieModel;
import com.hungteen.pvz.render.entity.PVZCreatureRender;
import com.hungteen.pvz.render.layer.fullskin.SunLightLayer;
import com.hungteen.pvz.utils.StringUtil;

import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class FoodieZombieRender extends PVZCreatureRender<FoodieZombieEntity>{

	public FoodieZombieRender(EntityRendererManager renderManagerIn) {
		super(renderManagerIn, new FoodieZombieModel(), 0f);
		this.addLayer(new SunLightLayer<>(this));
	}
	
	@Override
	protected float getScaleByEntity(FoodieZombieEntity entity) {
		float sz=0.5f;
		if(entity.isChild()) sz*=0.5f;
		return sz;
	}
	
	@Override
	public ResourceLocation getEntityTexture(FoodieZombieEntity entity) {
		return StringUtil.prefix("textures/entity/zombie/poolday/snorkel_zombie.png");
	}

}
