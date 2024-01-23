package com.leobeliik.mycustomtweaks.mixins;

import net.dries007.tfc.common.fluids.TFCFluids;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.material.FluidState;
import net.minecraftforge.common.extensions.IForgeFluidState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(IForgeFluidState.class)
public interface pumpMixin {

    @Shadow
    FluidState self();

    @Overwrite(remap = false)
    default boolean canConvertToSource(Level level, BlockPos pos) {
        return !this.self().getType().isSame(TFCFluids.SPRING_WATER.getSource()) && this.self().getType().canConvertToSource(this.self(), level, pos);
    }
}
