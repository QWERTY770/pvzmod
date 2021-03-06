package com.hungteen.pvz.model.entity.zombie.poolday;

import com.hungteen.pvz.entity.zombie.poolday.BobsleZombieEntity;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;

import net.minecraft.client.renderer.entity.model.EntityModel;
import net.minecraft.client.renderer.model.ModelRenderer;
import net.minecraft.util.math.MathHelper;

// Made with Blockbench 3.6.6
// Exported for Minecraft version 1.15
// Paste this class into your mod and generate all required imports


public class BobsleZombieModel extends EntityModel<BobsleZombieEntity> {
	private final ModelRenderer total;
	private final ModelRenderer head;
	private final ModelRenderer right_hand;
	private final ModelRenderer left_hand;
	private final ModelRenderer body;
	private final ModelRenderer right_leg;
	private final ModelRenderer left_leg;

	public BobsleZombieModel() {
		textureWidth = 256;
		textureHeight = 256;

		total = new ModelRenderer(this);
		total.setRotationPoint(0.0F, 24.0F, 0.0F);
		

		head = new ModelRenderer(this);
		head.setRotationPoint(0.0F, -48.0F, 0.0F);
		total.addChild(head);
		head.setTextureOffset(3, 192).addBox(-6.0F, -12.0F, -6.0F, 12.0F, 12.0F, 12.0F, 0.0F, false);
		head.setTextureOffset(109, 234).addBox(-7.0F, -6.0F, -7.0F, 1.0F, 1.0F, 14.0F, 0.0F, false);
		head.setTextureOffset(59, 199).addBox(-7.0F, -10.0F, -7.0F, 1.0F, 1.0F, 14.0F, 0.0F, false);
		head.setTextureOffset(100, 201).addBox(6.0F, -6.0F, -7.0F, 1.0F, 1.0F, 14.0F, 0.0F, false);
		head.setTextureOffset(142, 200).addBox(6.0F, -10.0F, -7.0F, 1.0F, 1.0F, 14.0F, 0.0F, false);
		head.setTextureOffset(187, 214).addBox(-6.0F, -6.0F, 6.0F, 12.0F, 1.0F, 1.0F, 0.0F, false);
		head.setTextureOffset(222, 217).addBox(-6.0F, -10.0F, 6.0F, 12.0F, 1.0F, 1.0F, 0.0F, false);
		head.setTextureOffset(181, 192).addBox(-6.0F, -10.0F, -7.0F, 12.0F, 5.0F, 1.0F, 0.0F, false);
		head.setTextureOffset(222, 198).addBox(-5.0F, -11.0F, -7.0F, 10.0F, 1.0F, 1.0F, 0.0F, false);
		head.setTextureOffset(134, 187).addBox(-5.0F, -5.0F, -7.0F, 10.0F, 1.0F, 1.0F, 0.0F, false);

		right_hand = new ModelRenderer(this);
		right_hand.setRotationPoint(-9.0F, -46.0F, 0.0F);
		total.addChild(right_hand);
		right_hand.setTextureOffset(77, 224).addBox(-3.0F, -2.0F, -2.0F, 4.0F, 23.0F, 4.0F, 0.0F, false);

		left_hand = new ModelRenderer(this);
		left_hand.setRotationPoint(10.0F, -46.0F, 0.0F);
		total.addChild(left_hand);
		left_hand.setTextureOffset(54, 227).addBox(-2.0F, -2.0F, -2.0F, 4.0F, 23.0F, 4.0F, 0.0F, false);

		body = new ModelRenderer(this);
		body.setRotationPoint(0.0F, -31.0F, 0.0F);
		total.addChild(body);
		body.setTextureOffset(5, 224).addBox(-8.0F, -17.0F, -3.0F, 16.0F, 23.0F, 6.0F, 0.0F, false);

		right_leg = new ModelRenderer(this);
		right_leg.setRotationPoint(-4.0F, -25.0F, 0.0F);
		total.addChild(right_leg);
		right_leg.setTextureOffset(184, 229).addBox(-2.0F, 0.0F, -2.0F, 4.0F, 21.0F, 4.0F, 0.0F, false);
		right_leg.setTextureOffset(149, 238).addBox(-3.0F, 21.0F, -6.0F, 6.0F, 4.0F, 9.0F, 0.0F, false);

		left_leg = new ModelRenderer(this);
		left_leg.setRotationPoint(4.0F, -25.0F, 0.0F);
		total.addChild(left_leg);
		left_leg.setTextureOffset(238, 228).addBox(-2.0F, 0.0F, -2.0F, 4.0F, 21.0F, 4.0F, 0.0F, false);
		left_leg.setTextureOffset(203, 240).addBox(-3.0F, 21.0F, -6.0F, 6.0F, 4.0F, 9.0F, 0.0F, false);
	}

	@Override
	public void setRotationAngles(BobsleZombieEntity entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch){
		this.head.rotateAngleY = netHeadYaw / (180F / (float)Math.PI);
        this.head.rotateAngleX = headPitch / (180F / (float)Math.PI);
        this.right_leg.rotateAngleX = MathHelper.cos(limbSwing * 0.6662F) * 1.4F * limbSwingAmount;
        this.left_leg.rotateAngleX = MathHelper.cos(limbSwing * 0.6662F + (float)Math.PI) * 1.4F * limbSwingAmount;
        this.right_hand.rotateAngleX = MathHelper.cos(limbSwing * 0.6662F) * 1.4F * limbSwingAmount;
        this.left_hand.rotateAngleX = MathHelper.cos(limbSwing * 0.6662F + (float)Math.PI) * 1.4F * limbSwingAmount;
	}

	@Override
	public void render(MatrixStack matrixStack, IVertexBuilder buffer, int packedLight, int packedOverlay, float red, float green, float blue, float alpha){
		total.render(matrixStack, buffer, packedLight, packedOverlay);
	}

	public void setRotationAngle(ModelRenderer modelRenderer, float x, float y, float z) {
		modelRenderer.rotateAngleX = x;
		modelRenderer.rotateAngleY = y;
		modelRenderer.rotateAngleZ = z;
	}
}