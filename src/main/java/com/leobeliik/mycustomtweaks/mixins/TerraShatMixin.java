package com.leobeliik.mycustomtweaks.mixins;

import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import vazkii.botania.common.helper.ItemNBTHelper;
import vazkii.botania.common.item.equipment.tool.terrasteel.TerraShattererItem;

@Mixin(TerraShattererItem.class)
public abstract class TerraShatMixin {

    @Inject(method = "Lvazkii/botania/common/item/equipment/tool/terrasteel/TerraShattererItem;isTipped(Lnet/minecraft/world/item/ItemStack;)Z",
    at = @At("RETURN"), remap = false)
    private static boolean isTipped(ItemStack stack, CallbackInfoReturnable cir) {
        return cir.getReturnValueZ() && ItemNBTHelper.getBoolean(stack, "enabled", false);
    }
}
