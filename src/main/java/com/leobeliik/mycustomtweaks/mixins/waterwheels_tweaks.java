package com.leobeliik.mycustomtweaks.mixins;

import blusunrize.immersiveengineering.common.blocks.wooden.WatermillBlockEntity;
import com.lumintorious.tfchomestead.common.entity.HomesteadEntities;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.UUID;

@Mixin(WatermillBlockEntity.class)
public class waterwheels_tweaks {

    @Inject(method = "Lblusunrize/immersiveengineering/common/blocks/wooden/WatermillBlockEntity;getPower()D",
            at = @At(value = "RETURN"), remap = false)
    private double getPower(CallbackInfoReturnable cir) {
        double power = cir.getReturnValueD();
        return power == 0 ? 0D : power > 0 ? 13D : -13D;
    }
}
