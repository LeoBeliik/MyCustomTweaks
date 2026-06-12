package com.leobeliik.mycustomtweaks.mixins;

import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import vazkii.botania.common.handler.EquipmentHandler;
import vazkii.botania.common.lib.BotaniaTags;

@Mixin(EquipmentHandler.class)
public class EquipmentHandlerMixin {

	@Inject(method = "Lvazkii/botania/common/handler/EquipmentHandler;isAccessory(Lnet/minecraft/world/item/ItemStack;)Z",
			at = @At("RETURN"), remap = false)
	public boolean isAccessory(ItemStack stack, CallbackInfoReturnable cir) {
		return stack.is(BotaniaTags.Items.MANA_USING_ITEMS) || cir.getReturnValueZ();
	}
}
