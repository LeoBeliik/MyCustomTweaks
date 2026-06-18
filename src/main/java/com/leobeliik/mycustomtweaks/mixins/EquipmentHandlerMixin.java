package com.leobeliik.mycustomtweaks.mixins;

import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.UnknownNullability;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import vazkii.botania.common.handler.EquipmentHandler;
import vazkii.botania.common.item.*;
import vazkii.botania.common.lib.BotaniaTags;

@Mixin(EquipmentHandler.class)
public class EquipmentHandlerMixin {

	@Shadow
	@UnknownNullability
	public static EquipmentHandler instance;

	@Inject(method = "Lvazkii/botania/common/handler/EquipmentHandler;isAccessory(Lnet/minecraft/world/item/ItemStack;)Z",
			at = @At("RETURN"), remap = false)
	public boolean isAccessory(ItemStack stack, CallbackInfoReturnable cir) {
		return stack.is(BotaniaTags.Items.MANA_USING_ITEMS) || isNiceItem(stack) || cir.getReturnValueZ();
	}

	@Unique
	private boolean isNiceItem(ItemStack stack) {
		Item item = stack.getItem();
		return (item instanceof WandOfTheForestItem || item instanceof FloralObedienceStickItem ||
				item instanceof AssemblyHaloItem || item instanceof ColoredContentsPouchItem ||
				item instanceof AstrolabeItem || item instanceof SextantItem || item instanceof ManaBlasterItem ||
				item instanceof HornItem);
	}
}
