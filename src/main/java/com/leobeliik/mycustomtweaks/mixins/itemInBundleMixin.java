package com.leobeliik.mycustomtweaks.mixins;


import net.dries007.tfc.common.component.size.ItemSizeManager;
import net.dries007.tfc.common.component.size.Size;
import net.dries007.tfc.common.component.size.Weight;
import net.minecraft.world.entity.SlotAccess;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ClickAction;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.BundleItem;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(BundleItem.class)
public class itemInBundleMixin {

    @Inject(method = "Lnet/minecraft/world/item/BundleItem;overrideOtherStackedOnMe(Lnet/minecraft/world/item/ItemStack;Lnet/minecraft/world/item/ItemStack;Lnet/minecraft/world/inventory/Slot;Lnet/minecraft/world/inventory/ClickAction;Lnet/minecraft/world/entity/player/Player;Lnet/minecraft/world/entity/SlotAccess;)Z",
            at = @At("HEAD"), cancellable = true)
    public boolean overrideOtherStackedOnMe(ItemStack pStack, ItemStack pOther, Slot pSlot, ClickAction pAction, Player pPlayer, SlotAccess pAccess, CallbackInfoReturnable<Boolean> cir) {
        if (!ItemSizeManager.get(pOther).getSize(pOther).isSmallerThan(Size.NORMAL) && !ItemSizeManager.get(pOther).getWeight(pOther).isSmallerThan(Weight.MEDIUM)) {
            cir.setReturnValue(false);
            cir.cancel();
        }
        return cir.getReturnValueZ();
    }

    @Inject(method = "Lnet/minecraft/world/item/BundleItem;overrideStackedOnOther(Lnet/minecraft/world/item/ItemStack;Lnet/minecraft/world/inventory/Slot;Lnet/minecraft/world/inventory/ClickAction;Lnet/minecraft/world/entity/player/Player;)Z",
            at = @At("HEAD"), cancellable = true)
    public boolean overrideStackedOnOther(ItemStack pStack, Slot pSlot, ClickAction pAction, Player pPlayer, CallbackInfoReturnable<Boolean> cir) {
        ItemStack itemstack = pSlot.getItem();
        if (!ItemSizeManager.get(itemstack).getSize(itemstack).isSmallerThan(Size.NORMAL) && !ItemSizeManager.get(itemstack).getWeight(itemstack).isSmallerThan(Weight.MEDIUM)) {
            cir.setReturnValue(false);
            cir.cancel();
        }
        return cir.getReturnValueZ();
    }
}
