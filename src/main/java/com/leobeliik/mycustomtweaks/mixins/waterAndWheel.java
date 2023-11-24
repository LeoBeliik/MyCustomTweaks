package com.leobeliik.mycustomtweaks.mixins;

import net.dries007.tfc.common.blockentities.rotation.RotatingBlockEntity;
import net.dries007.tfc.common.blockentities.rotation.WaterWheelBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(RotatingBlockEntity.class)
public interface waterAndWheel {

    @Shadow
    boolean isInvalidInNetwork();

    @Overwrite(remap = false)
    default void destroyIfInvalid(Level level, BlockPos pos) {
        if (this.isInvalidInNetwork() && !(this instanceof WaterWheelBlockEntity)) {
            level.destroyBlock(pos, true);
        }
    }
}
