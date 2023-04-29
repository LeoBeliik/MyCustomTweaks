package com.leobeliik.mycustomtweaks.mixins;

import com.simibubi.create.content.contraptions.components.saw.SawTileEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(SawTileEntity.class)
public class SawTileMixin {

    @Inject(method = "Lcom/simibubi/create/content/contraptions/components/saw/SawTileEntity;shouldRun()Z", at = @At("RETURN"), remap = false)
    private boolean shouldRun(CallbackInfoReturnable cir) {
        return false;
    }
}
