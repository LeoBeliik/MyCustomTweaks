package com.leobeliik.mycustomtweaks.mixins;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.dries007.tfc.client.RenderHelpers;
import net.dries007.tfc.client.render.blockentity.CrankshaftBlockEntityRenderer;
import net.dries007.tfc.common.blockentities.rotation.CrankshaftBlockEntity;
import net.dries007.tfc.common.blocks.TFCBlocks;
import net.dries007.tfc.common.blocks.rotation.ConnectedAxleBlock;
import net.dries007.tfc.common.blocks.rotation.CrankshaftBlock;
import net.dries007.tfc.common.blocks.rotation.FluidPumpBlock;
import net.dries007.tfc.util.Helpers;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.ModelBlockRenderer;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.client.model.data.ModelData;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(CrankshaftBlockEntityRenderer.class)
public class shaftRenderMixin {

    @Shadow
    @Final
    public static ModelResourceLocation WHEEL_MODEL;

    @Shadow
    @Final
    public static ResourceLocation PUMP_TEXTURE;

    @Overwrite
    public void render(CrankshaftBlockEntity crankshaft, float partialTick, PoseStack stack, MultiBufferSource bufferSource, int packedLight, int packedOverlay) {
        Level level = crankshaft.getLevel();
        BlockPos pos = crankshaft.getBlockPos();
        BlockState state = crankshaft.getBlockState();
        if (state.getBlock() instanceof CrankshaftBlock && level != null) {
            Direction face = (Direction)state.getValue(CrankshaftBlock.FACING);
            CrankshaftBlock.Part part = (CrankshaftBlock.Part)state.getValue(CrankshaftBlock.PART);
            if (part == CrankshaftBlock.Part.SHAFT && crankshaft.getRotationNode().rotation() == null) {
                BlockEntity mainPart = level.getBlockEntity(pos.relative(face, -1));
                if (!(mainPart instanceof CrankshaftBlockEntity)) {
                    return;
                }

                crankshaft = (CrankshaftBlockEntity)mainPart;
            }

            VertexConsumer buffer = bufferSource.getBuffer(RenderType.cutout());
            float rotationAngle = CrankshaftBlockEntity.calculateRealRotationAngle(crankshaft, face, partialTick);
            RandomSource random = RandomSource.create();
            stack.pushPose();
            stack.translate(0.5F, 0.5F, 0.5F);
            stack.mulPose(Axis.YP.rotationDegrees(180.0F - 90.0F * (float)face.get2DDataValue()));
            if (part == CrankshaftBlock.Part.BASE) {
                stack.mulPose(Axis.XP.rotation(rotationAngle + (float)Math.PI));
                stack.translate(-0.5F, -0.5F, -0.5F);
                ModelBlockRenderer modelRenderer = Minecraft.getInstance().getBlockRenderer().getModelRenderer();
                BakedModel baked = Minecraft.getInstance().getModelManager().getModel(WHEEL_MODEL);
                modelRenderer.tesselateWithAO(level, baked, crankshaft.getBlockState(), crankshaft.getBlockPos(), stack, buffer, true, random, (long)packedLight, packedOverlay, ModelData.EMPTY, RenderType.cutout());
                BlockState adjacentAxleState = level.getBlockState(crankshaft.getBlockPos().relative(face.getCounterClockWise()));
                Block var19 = adjacentAxleState.getBlock();
                if (var19 instanceof ConnectedAxleBlock) {
                    ConnectedAxleBlock axleBlock = (ConnectedAxleBlock)var19;
                    if (crankshaft.getRotationNode().isConnectedToNetwork()) {
                        ResourceLocation axleTexture = axleBlock.getAxleTextureLocation();
                        TextureAtlasSprite axleSprite = RenderHelpers.blockTexture(axleTexture);
                        RenderHelpers.renderTexturedCuboid(stack, buffer, axleSprite, packedLight, packedOverlay, 0.0F, 0.375F, 0.375F, 0.375F, 0.625F, 0.625F, false);
                    }
                }
            } else {
                stack.translate(-0.5F, -0.5F, -0.5F);
                TextureAtlasSprite rodSprite = RenderHelpers.blockTexture(Helpers.identifier("block/metal/smooth/wrought_iron"));
                CrankshaftBlockEntity.ShaftMovement movement = CrankshaftBlockEntity.calculateShaftMovement(rotationAngle);
                float pistonLength = 1.03125F;
                float armLength = 0.75F;
                float armRadius = 0.0625F;
                float boxRadius = 0.125F;
                stack.translate(0.5625F, 0.5F, movement.lengthEH());
                RenderHelpers.renderTexturedCuboid(stack, buffer, rodSprite, packedLight, packedOverlay, -0.125F, -0.125F, -0.125F, 0.125F, 0.125F, 0.125F);
                RenderHelpers.renderTexturedCuboid(stack, buffer, rodSprite, packedLight, packedOverlay, -0.0625F, -0.0625F, -1.03125F, 0.0625F, 0.0625F, 0.0625F);
                BlockPos pumpPos = pos.relative(face);
                BlockState pumpState = level.getBlockState(pumpPos);
                if (pumpState.getBlock() == TFCBlocks.STEEL_PUMP.get() && face == pumpState.getValue(FluidPumpBlock.FACING)) {
                    RenderHelpers.renderTexturedCuboid(stack, buffer, RenderHelpers.blockTexture(PUMP_TEXTURE), packedLight, packedOverlay, -0.125F, -0.125F, -1.125F, 0.125F, 0.125F, -0.875F);
                }

                stack.mulPose(Axis.XP.rotation(movement.raiseAngle()));
                RenderHelpers.renderTexturedCuboid(stack, buffer, rodSprite, packedLight, packedOverlay, -0.0625F, -0.0625F, -0.0625F, 0.0625F, 0.0625F, 0.8125F);
            }

            stack.popPose();
        }
    }
}
