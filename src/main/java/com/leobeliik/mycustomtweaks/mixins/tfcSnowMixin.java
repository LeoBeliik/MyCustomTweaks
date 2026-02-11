package com.leobeliik.mycustomtweaks.mixins;

import net.dries007.tfc.common.blocks.SnowPileBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SnowLayerBlock;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(SnowPileBlock.class)
public class tfcSnowMixin {

    @Inject(method = "Lnet/dries007/tfc/common/blocks/SnowPileBlock;placeSnowPile(Lnet/minecraft/world/level/LevelAccessor;Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/state/BlockState;Z)V",
            at = @At("TAIL"), remap = false)
    private static void placeSnowPile(LevelAccessor level, BlockPos pos, BlockState state, boolean byPlayer, CallbackInfo ci) {
        if (state.getBlock() instanceof SnowLayerBlock snow) {
            int layer = state.getValue(SnowLayerBlock.LAYERS);
            if (layer < 8) {
                level.setBlock(pos, state.setValue(SnowLayerBlock.LAYERS, layer + 1), Block.UPDATE_CLIENTS | Block.UPDATE_KNOWN_SHAPE | Block.UPDATE_ALL);
            }
        }
    }
}
