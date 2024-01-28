package com.leobeliik.mycustomtweaks.mixins;

import blusunrize.immersiveengineering.common.items.GliderItem;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ElytraItem;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.violetmoon.zeta.item.ext.IZetaItemExtensions;

@Mixin(IZetaItemExtensions.class)
public interface elytraZetaMixin {
    @Overwrite(remap = false)
    default boolean canElytraFlyZeta(ItemStack stack, LivingEntity entity) {
        return stack.getItem() instanceof ElytraItem && ElytraItem.isFlyEnabled(stack) || stack.getItem() instanceof GliderItem;
    }
}
