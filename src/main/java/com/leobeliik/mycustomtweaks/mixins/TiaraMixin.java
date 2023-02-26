package com.leobeliik.mycustomtweaks.mixins;

import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import vazkii.botania.common.item.equipment.bauble.FlugelTiaraItem;

@Mixin(FlugelTiaraItem.class)
public class TiaraMixin {

    @Redirect(method = "Lvazkii/botania/common/item/equipment/bauble/FlugelTiaraItem;onWornTick(Lnet/minecraft/world/item/ItemStack;Lnet/minecraft/world/entity/LivingEntity;)V",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/player/Player;isSprinting()Z"))
    public boolean noDash(Player player) {
        return false;
    }
}
