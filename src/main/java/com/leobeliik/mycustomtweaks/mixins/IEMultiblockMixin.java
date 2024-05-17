package com.leobeliik.mycustomtweaks.mixins;

import blusunrize.immersiveengineering.common.register.IEBlocks;
import net.minecraft.world.level.block.state.BlockBehaviour;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import java.util.function.Supplier;

@Mixin(IEBlocks.class)
public class IEMultiblockMixin {
    @Shadow @Final private static Supplier<BlockBehaviour.Properties> METAL_PROPERTIES_NO_OVERLAY;
    @Shadow
    public static final Supplier<BlockBehaviour.Properties> METAL_PROPERTIES_NO_OCCLUSION = () -> METAL_PROPERTIES_NO_OVERLAY.get().noOcclusion().forceSolidOn();

}
