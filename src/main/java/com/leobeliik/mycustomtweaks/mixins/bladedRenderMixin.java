package com.leobeliik.mycustomtweaks.mixins;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.dries007.tfc.client.RenderHelpers;
import net.dries007.tfc.client.render.blockentity.AxleBlockEntityRenderer;
import net.dries007.tfc.client.render.blockentity.BladedAxleBlockEntityRenderer;
import net.dries007.tfc.util.Helpers;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.core.Direction;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;

@Mixin(BladedAxleBlockEntityRenderer.class)
public class bladedRenderMixin {

    @Overwrite
    public static void renderBlade(PoseStack stack, MultiBufferSource bufferSource, Direction.Axis axis, int packedLight, int packedOverlay, float rotationAngle) {
        TextureAtlasSprite sprite = RenderHelpers.blockTexture(Helpers.identifier("block/metal/smooth/wrought_iron"));
        VertexConsumer buffer = bufferSource.getBuffer(RenderType.cutout());
        stack.pushPose();
        AxleBlockEntityRenderer.applyRotation(stack, axis, rotationAngle);
        RenderHelpers.renderTexturedCuboid(stack, buffer, sprite, packedLight, packedOverlay, 0.4375F, 0.625F, 0.375F, 0.5625F, 1.09375F, 0.625F, false);
        stack.popPose();
    }
}
