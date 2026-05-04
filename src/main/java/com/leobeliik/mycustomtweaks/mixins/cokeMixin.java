package com.leobeliik.mycustomtweaks.mixins;

import blusunrize.immersiveengineering.api.crafting.CokeOvenRecipe;
import blusunrize.immersiveengineering.api.multiblocks.blocks.env.IMultiblockContext;
import blusunrize.immersiveengineering.common.blocks.multiblocks.logic.CokeOvenLogic;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import static blusunrize.immersiveengineering.common.blocks.multiblocks.logic.CokeOvenLogic.INPUT_SLOT;

@Mixin(CokeOvenLogic.class)
public class cokeMixin {
    @Inject(method = "getRecipe", at = @At("RETURN"), cancellable = true, remap = false)
    private void requireDoubleIngredients(IMultiblockContext<CokeOvenLogic.State> context, CallbackInfoReturnable<CokeOvenRecipe> cir) {
        CokeOvenRecipe recipe = cir.getReturnValue();
        if (recipe != null) {
            ItemStack inputStack = context.getState().getInventory().getStackInSlot(INPUT_SLOT);
            if (inputStack.is(ItemTags.LOGS_THAT_BURN) && inputStack.getCount() < (recipe.input.getCount() * 2)) {
                cir.setReturnValue(null);
            }
        }
    }

    @Redirect(method = "tickServer", at = @At(value = "INVOKE",
            target = "Lnet/minecraft/world/item/ItemStack;grow(I)V", ordinal = 0), remap = false)
    private void doubleItemConsumption(ItemStack instance, int amount) {
        instance.grow(instance.is(ItemTags.LOGS_THAT_BURN) ? amount * 2 : amount);
    }
}
