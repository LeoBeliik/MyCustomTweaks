package com.leobeliik.mycustomtweaks.mixins;


import net.dries007.tfc.common.capabilities.size.ItemSizeManager;
import net.dries007.tfc.common.capabilities.size.Size;
import net.mehvahdjukaar.supplementaries.common.block.tiles.SackBlockTile;
import net.mehvahdjukaar.supplementaries.common.utils.MiscUtils;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(SackBlockTile.class)
public class itemInSackMixin {

    @Inject(at = @At("RETURN"), method = "Lnet/mehvahdjukaar/supplementaries/common/block/tiles/SackBlockTile;canPlaceItem(ILnet/minecraft/world/item/ItemStack;)Z", remap = false)

    public boolean canPlaceItem(int index, ItemStack stack, CallbackInfoReturnable cir) {
        return cir.getReturnValueZ() && ItemSizeManager.get(stack).getSize(stack).isSmallerThan(Size.VERY_LARGE);
    }
}
