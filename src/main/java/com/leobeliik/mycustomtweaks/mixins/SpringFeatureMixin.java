package com.leobeliik.mycustomtweaks.mixins;

import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;
import net.minecraft.world.level.levelgen.feature.SpringFeature;
import net.minecraft.world.level.levelgen.feature.configurations.SpringConfiguration;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(SpringFeature.class)
public class SpringFeatureMixin {

    @Inject(method = "place", at = @At("HEAD"), cancellable = true)
    private void canSurvive(FeaturePlaceContext<SpringConfiguration> context, CallbackInfoReturnable<Boolean> cir) {
        cir.setReturnValue(false);
    }

}
