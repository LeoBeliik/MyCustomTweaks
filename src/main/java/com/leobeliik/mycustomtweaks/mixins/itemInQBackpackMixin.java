package com.leobeliik.mycustomtweaks.mixins;

import net.dries007.tfc.common.component.size.ItemSizeManager;
import net.dries007.tfc.common.component.size.Size;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.violetmoon.quark.addons.oddities.inventory.slot.BackpackSlot;

@Mixin(BackpackSlot.class)
public class itemInQBackpackMixin {

    @Inject(at = @At("RETURN"), method = "Lorg/violetmoon/quark/addons/oddities/inventory/slot/BackpackSlot;mayPlace(Lnet/minecraft/world/item/ItemStack;)Z")
    public boolean mayPlace(@NotNull ItemStack stack, CallbackInfoReturnable cir) {
        return ItemSizeManager.get(stack).getSize(stack).isEqualOrSmallerThan(Size.NORMAL);
    }
}
