package com.leobeliik.mycustomtweaks.mixins;

import net.dries007.tfc.common.blockentities.rotation.WaterWheelBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(WaterWheelBlockEntity.class)
public class waterwheelBEMixin {

    @Inject(method = "Lnet/dries007/tfc/common/blockentities/rotation/WaterWheelBlockEntity;calculateFlowRateAndObstruction(Lnet/minecraft/world/level/Level;Lnet/minecraft/core/BlockPos;Lnet/minecraft/core/Direction$Axis;)Ljava/lang/Float;",
            at = @At("RETURN"), remap = false)
    private static Float calculateFlowRateAndObstruction(Level level, BlockPos pos, Direction.Axis axis, CallbackInfoReturnable cir) {
        if (cir.getReturnValue() != null && cir.getReturnValueF() < 0) {
            return cir.getReturnValueF() * -1F;
        }
        return cir.getReturnValueF();
    }
}
