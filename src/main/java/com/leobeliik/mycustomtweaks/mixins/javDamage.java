package com.leobeliik.mycustomtweaks.mixins;

import net.dries007.tfc.common.items.JavelinItem;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(JavelinItem.class)
public class javDamage {

    @Inject(method = "Lnet/dries007/tfc/common/items/JavelinItem;getThrownDamage()F",
            at = @At(value = "return"), remap = false)
    public float getThrownDamage(CallbackInfoReturnable cir) {
        return cir.getReturnValueF() + 2f;
    }
}
