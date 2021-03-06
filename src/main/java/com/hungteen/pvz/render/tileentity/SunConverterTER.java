package com.hungteen.pvz.render.tileentity;

import com.hungteen.pvz.register.ItemRegister;
import com.hungteen.pvz.tileentity.SunConverterTileEntity;
import com.mojang.blaze3d.matrix.MatrixStack;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.ItemRenderer;
import net.minecraft.client.renderer.Vector3f;
import net.minecraft.client.renderer.model.IBakedModel;
import net.minecraft.client.renderer.model.ItemCameraTransforms;
import net.minecraft.client.renderer.tileentity.TileEntityRenderer;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@SuppressWarnings("deprecation")
@OnlyIn(Dist.CLIENT)
public class SunConverterTER extends TileEntityRenderer<SunConverterTileEntity> {

	public SunConverterTER(TileEntityRendererDispatcher rendererDispatcherIn) {
		super(rendererDispatcherIn);
	}

	@Override
	public void render(SunConverterTileEntity tileEntityIn, float partialTicks, MatrixStack matrixStackIn,
			IRenderTypeBuffer bufferIn, int combinedLightIn, int combinedOverlayIn) {
		matrixStackIn.push();
		ItemRenderer itemRenderer = Minecraft.getInstance().getItemRenderer();
        ItemStack stack = new ItemStack(ItemRegister.SUN_COLLECTOR.get());
        float scale = 1.2F;
        matrixStackIn.scale(scale, scale, scale);
        matrixStackIn.translate(0.5D / scale, 1D / scale, 0.5D / scale);
        if(tileEntityIn.array.get(0) == 1) {
        	matrixStackIn.translate(0, (0.1D * MathHelper.sin(tileEntityIn.tickExist / 10F)) / scale, 0);
        	matrixStackIn.rotate(Vector3f.YP.rotation(tileEntityIn.tickExist / 10F));
        }
        IBakedModel ibakedmodel = itemRenderer.getItemModelWithOverrides(stack, tileEntityIn.getWorld(), null);
        itemRenderer.renderItem(stack, ItemCameraTransforms.TransformType.GROUND, true, matrixStackIn, bufferIn, combinedLightIn, combinedOverlayIn, ibakedmodel);
        matrixStackIn.pop();
	}

}
