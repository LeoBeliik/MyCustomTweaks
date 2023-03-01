package com.leobeliik.mycustomtweaks.mixins;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.item.ArrowItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;
import vazkii.botania.common.item.equipment.tool.bow.CrystalBowItem;

@Mixin(CrystalBowItem.class)
public class CrystalBowMixin {

    @Inject(method = "Lvazkii/botania/common/item/equipment/tool/bow/CrystalBowItem;releaseUsing(Lnet/minecraft/world/item/ItemStack;Lnet/minecraft/world/level/Level;Lnet/minecraft/world/entity/LivingEntity;I)V",
            at = @At(value = "INVOKE_ASSIGN", target = "Lnet/minecraft/world/item/ArrowItem;createArrow(Lnet/minecraft/world/level/Level;Lnet/minecraft/world/item/ItemStack;Lnet/minecraft/world/entity/LivingEntity;)Lnet/minecraft/world/entity/projectile/AbstractArrow;", ordinal = 0),
            locals = LocalCapture.CAPTURE_FAILHARD)
    public void releaseUsing(@NotNull ItemStack stack, @NotNull Level worldIn, LivingEntity entityLiving, int timeLeft, CallbackInfo ci, Player p, boolean b, ItemStack item, int i, float f, boolean b2, ArrowItem arrow, AbstractArrow abstractarrowentity) {
        abstractarrowentity.setBaseDamage(abstractarrowentity.getBaseDamage() * 2);
    }
}
