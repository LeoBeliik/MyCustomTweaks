package com.leobeliik.mycustomtweaks.mixins;

import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import vazkii.quark.content.tweaks.module.HoeHarvestingModule;

@Mixin(HoeHarvestingModule.class)
public class HoeMixin {

    @Inject(method = "Lvazkii/quark/content/tweaks/module/HoeHarvestingModule;isHoe(Lnet/minecraft/world/item/ItemStack;)Z",
            at = @At("RETURN"), remap = false)
    private static boolean isHoe(ItemStack itemStack, CallbackInfoReturnable cir) {
        return itemStack.getShareTag() != null && itemStack.getShareTag().toString().contains("double/hoe") || cir.getReturnValueZ();
    }
}
