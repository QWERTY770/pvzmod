package com.hungteen.pvz.render.entity.zombie.other;

import com.hungteen.pvz.entity.zombie.other.MournerZombieEntity;
import com.hungteen.pvz.model.entity.zombie.other.MournerZombieModel;
import com.hungteen.pvz.render.entity.zombie.PVZZombieRender;
import com.hungteen.pvz.utils.StringUtil;

import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.util.ResourceLocation;

public class MournerZombieRender extends PVZZombieRender<MournerZombieEntity>{

	public MournerZombieRender(EntityRendererManager rendererManager) {
		super(rendererManager, new MournerZombieModel(), 0.5f);
	}

	@Override
	protected float getScaleByEntity(MournerZombieEntity entity) {
		return 0.5f;
	}

	@Override
	public ResourceLocation getEntityTexture(MournerZombieEntity entity) {
		return StringUtil.prefix("textures/entity/zombie/other/mourner_zombie.png");
	}

}
