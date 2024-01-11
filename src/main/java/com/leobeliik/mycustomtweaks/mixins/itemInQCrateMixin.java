package com.leobeliik.mycustomtweaks.mixins;

import net.dries007.tfc.common.capabilities.size.ItemSizeManager;
import net.dries007.tfc.common.capabilities.size.Size;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.items.ItemStackHandler;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.violetmoon.quark.addons.oddities.capability.CrateItemHandler;

@Mixin(CrateItemHandler.class)
public class itemInQCrateMixin extends ItemStackHandler {

    @Override
    public boolean isItemValid(int slot, @NotNull ItemStack stack) {
        return ItemSizeManager.get(stack).getSize(stack).isSmallerThan(Size.VERY_LARGE);
    }
}
