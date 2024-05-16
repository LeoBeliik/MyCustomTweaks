package com.leobeliik.mycustomtweaks.mixins;

import blusunrize.immersiveengineering.common.blocks.IEBaseBlock;
import blusunrize.immersiveengineering.common.blocks.metal.MetalLadderBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.violetmoon.quark.content.tweaks.module.EnhancedLaddersModule;

@Mixin(EnhancedLaddersModule.class)
public class QuarkLadderMixin {

    @Inject(method = "Lorg/violetmoon/quark/content/tweaks/module/EnhancedLaddersModule;canLadderSurvive(Lnet/minecraft/world/level/block/state/BlockState;Lnet/minecraft/world/level/LevelReader;Lnet/minecraft/core/BlockPos;)Z",
            at = @At("RETURN"), remap = false)
    private static boolean canLadderSurvive(BlockState state, LevelReader world, BlockPos pos, CallbackInfoReturnable cir) {
        if (state.getBlock() instanceof MetalLadderBlock ladder && !ladder.getName().toString().contains("ladder_none")) {
            return true;
        }
        return cir.getReturnValueZ();
    }

}
