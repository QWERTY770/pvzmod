package com.hungteen.pvz.render.layer;

import com.hungteen.pvz.entity.plant.assist.MagnetShroomEntity;
import com.hungteen.pvz.utils.enums.MetalTypes;
import com.mojang.blaze3d.matrix.MatrixStack;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.ItemRenderer;
import net.minecraft.client.renderer.entity.IEntityRenderer;
import net.minecraft.client.renderer.entity.layers.LayerRenderer;
import net.minecraft.client.renderer.entity.model.EntityModel;
import net.minecraft.client.renderer.model.IBakedModel;
import net.minecraft.client.renderer.model.ItemCameraTransforms;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@SuppressWarnings("deprecation")
@OnlyIn(Dist.CLIENT)
public class MetalItemLayer <T extends MagnetShroomEntity> extends LayerRenderer<T, EntityModel<T>> {

	private ItemRenderer itemRenderer = Minecraft.getInstance().getItemRenderer();
	
    public MetalItemLayer(IEntityRenderer<T, EntityModel<T>> entityRendererIn) {
		super(entityRendererIn);
	}
    
	@Override
	public void render(MatrixStack matrixStackIn, IRenderTypeBuffer bufferIn, int packedLightIn,
			MagnetShroomEntity entityIn, float limbSwing, float limbSwingAmount, float partialTicks,
			float ageInTicks, float netHeadYaw, float headPitch) {
		if(entityIn.getMetalType() == MetalTypes.EMPTY) {
			return ;
		}
		matrixStackIn.push();
		matrixStackIn.scale(-1, -1, 1);
		float percent = entityIn.getAttackTime() * 1.0F / entityIn.getAttackCD();
		matrixStackIn.scale(percent, percent, percent);
		ItemStack itemstack = entityIn.getMetalRenderItem();
		IBakedModel ibakedmodel = this.itemRenderer.getItemModelWithOverrides(itemstack, entityIn.world, (LivingEntity) null);
		this.itemRenderer.renderItem(itemstack, ItemCameraTransforms.TransformType.GROUND, false, matrixStackIn,
				bufferIn, packedLightIn, OverlayTexture.NO_OVERLAY, ibakedmodel);
		matrixStackIn.pop();
	}

}
