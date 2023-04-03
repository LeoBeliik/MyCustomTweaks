package com.leobeliik.mycustomtweaks.mixins;

import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import vazkii.botania.api.mana.BurstProperties;
import vazkii.botania.common.item.lens.LensGravity;

@Mixin(LensGravity.class)
public class GravityLensMixin {

    @Inject(method = "Lvazkii/botania/common/item/lens/LensGravity;apply(Lnet/minecraft/world/item/ItemStack;Lvazkii/botania/api/mana/BurstProperties;)V",
            at = @At("TAIL"), remap = false)
    public void applyGravity(ItemStack stack, BurstProperties props, CallbackInfo ci) {
        props.gravity = 0.00325F;
    }
}
