package com.leobeliik.mycustomtweaks.mixins;

import blusunrize.immersiveengineering.api.IEApi;
import net.dries007.tfc.common.capabilities.size.ItemSizeManager;
import net.dries007.tfc.common.capabilities.size.Size;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(IEApi.class)
public class itemInIECrateMixin {

    @Inject(at = @At("RETURN"), method = "Lblusunrize/immersiveengineering/api/IEApi;isAllowedInCrate(Lnet/minecraft/world/item/ItemStack;)Z", remap = false)
    private static boolean isAllowedInCrate(ItemStack stack, CallbackInfoReturnable cir) {
        return cir.getReturnValueZ() && ItemSizeManager.get(stack).getSize(stack).isSmallerThan(Size.VERY_LARGE);
    }
}
