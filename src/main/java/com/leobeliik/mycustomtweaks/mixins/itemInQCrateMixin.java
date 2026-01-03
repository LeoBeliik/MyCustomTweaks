package com.leobeliik.mycustomtweaks.mixins;

import blusunrize.immersiveengineering.common.blocks.CrateItem;
import net.dries007.tfc.common.component.size.ItemSizeManager;
import net.dries007.tfc.common.component.size.Size;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.items.ItemStackHandler;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(CrateItem.class)
public class itemInQCrateMixin extends ItemStackHandler {

    @Override
    public boolean isItemValid(int slot, @NotNull ItemStack stack) {
        return ItemSizeManager.get(stack).getSize(stack).isSmallerThan(Size.VERY_LARGE);
    }
}