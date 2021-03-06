package com.hungteen.pvz.render.layer;

import com.hungteen.pvz.entity.zombie.grassday.NormalZombieEntity;
import com.hungteen.pvz.model.entity.zombie.poolday.DuckyTubeModel;
import com.hungteen.pvz.utils.StringUtil;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;

import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.IEntityRenderer;
import net.minecraft.client.renderer.entity.layers.LayerRenderer;
import net.minecraft.client.renderer.entity.model.EntityModel;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.util.ResourceLocation;

public class DuckyTubeLayer<T extends NormalZombieEntity> extends LayerRenderer<T, EntityModel<T>>{

	public static final ResourceLocation TEXTURE = StringUtil.prefix("textures/entity/zombie/poolday/ducky_tube.png");
	private DuckyTubeModel model = new DuckyTubeModel();
	
	public DuckyTubeLayer(IEntityRenderer<T, EntityModel<T>> entityRendererIn) {
		super(entityRendererIn);
	}

	@Override
	public void render(MatrixStack matrixStackIn, IRenderTypeBuffer bufferIn, int packedLightIn,
			T zombie, float limbSwing, float limbSwingAmount, float partialTicks,
			float ageInTicks, float netHeadYaw, float headPitch) {
		if(!zombie.isInWater()) {
			return ;
		}
		matrixStackIn.push();
		IVertexBuilder builder = bufferIn.getBuffer(RenderType.getEntitySolid(TEXTURE));
		matrixStackIn.translate(0, -1.3f, 0);
		this.model.render(matrixStackIn, builder, packedLightIn, OverlayTexture.NO_OVERLAY);
		matrixStackIn.pop();
	}
	
}
