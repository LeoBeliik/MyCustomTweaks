package com.leobeliik.mycustomtweaks.mixins;

import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import vazkii.botania.common.item.equipment.bauble.RingOfMagnetizationItem;
import static com.leobeliik.mycustomtweaks.MyCustomTweaks.magnetKey;

@Mixin(RingOfMagnetizationItem.class)
public class MagnetRingMixin {

    @Redirect(method = "Lvazkii/botania/common/item/equipment/bauble/RingOfMagnetizationItem;onWornTick(Lnet/minecraft/world/item/ItemStack;Lnet/minecraft/world/entity/LivingEntity;)V",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/LivingEntity;isShiftKeyDown()Z"), remap = false)
    private boolean activeMagnet(LivingEntity b) {
        return magnetKey.isDown();
    }
}
