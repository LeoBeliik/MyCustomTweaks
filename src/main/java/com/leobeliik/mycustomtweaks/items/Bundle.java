package com.leobeliik.mycustomtweaks.items;

import net.dries007.tfc.common.capabilities.size.ItemSizeManager;
import net.dries007.tfc.common.capabilities.size.Size;
import net.minecraft.world.entity.SlotAccess;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ClickAction;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.BundleItem;
import net.minecraft.world.item.ItemStack;
import org.violetmoon.zeta.util.ItemNBTHelper;

public class Bundle extends BundleItem {
    public static final String FILLED = "filled";

    public Bundle(Properties pProperties) {
        super(pProperties);
    }

    @Override
    public boolean overrideStackedOnOther(ItemStack pStack, Slot pSlot, ClickAction pAction, Player pPlayer) {
        if (!ItemSizeManager.get(pStack).getSize(pStack).isSmallerThan(Size.VERY_LARGE)) return false;
        return super.overrideStackedOnOther(pStack, pSlot, pAction, pPlayer);
    }

    @Override
    public boolean overrideOtherStackedOnMe(ItemStack pStack, ItemStack pOther, Slot pSlot, ClickAction pAction, Player pPlayer, SlotAccess pAccess) {
        if (!ItemSizeManager.get(pStack).getSize(pStack).isSmallerThan(Size.VERY_LARGE)) return false;
        return super.overrideOtherStackedOnMe(pStack, pOther, pSlot, pAction, pPlayer, pAccess);
    }

    @Override
    public int getBarWidth(ItemStack pStack) {
        int a = super.getBarWidth(pStack);
        ItemNBTHelper.setInt(pStack, FILLED, a == 13 ? a : 0);
        return a;
    }
}
