package com.hungteen.pvz.render.entity.zombie;

import com.hungteen.pvz.entity.zombie.PVZZombieEntity;
import com.mojang.blaze3d.matrix.MatrixStack;

import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.client.renderer.entity.model.EntityModel;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public abstract class PVZZombieRender<T extends PVZZombieEntity> extends MobRenderer<T, EntityModel<T>>{

	public PVZZombieRender(EntityRendererManager rendererManager, EntityModel<T> entityModelIn, float shadowSizeIn) {
		super(rendererManager, entityModelIn, shadowSizeIn);
		this.addZombieLayers();
	}

	@Override
	protected void preRenderCallback(T entitylivingbaseIn, MatrixStack matrixStackIn, float partialTickTime) {
		float sz=getScaleByEntity(entitylivingbaseIn);
		matrixStackIn.scale(sz,sz,sz);
	}
	
	protected void addZombieLayers()
	{
		
	}
	
	protected abstract float getScaleByEntity(T entity);
	
}