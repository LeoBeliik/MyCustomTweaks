package com.leobeliik.mycustomtweaks.mixins;

import mezz.jei.common.gui.ingredients.RecipeSlot;
import mezz.jei.common.gui.recipes.layout.IRecipeLayoutInternal;
import mezz.jei.common.gui.recipes.layout.RecipeTransferButton;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import vazkii.botania.network.serverbound.IndexKeybindRequestPacket;
import vazkii.botania.xplat.ClientXplatAbstractions;

import java.util.List;
import java.util.Optional;
import java.util.stream.IntStream;

@Mixin(RecipeTransferButton.class)
public class RecipeButtonMixin {

    private Object btn = this;
    @Shadow @Final private IRecipeLayoutInternal<?> recipeLayout;

    @Inject(method = "Lmezz/jei/common/gui/recipes/layout/RecipeTransferButton;onRelease(DD)V",
    at = @At("TAIL"), remap = false)
    private void onClokc(double mouseX, double mouseY, CallbackInfo ci) {
        if (!((RecipeTransferButton) btn).active) {
            List<RecipeSlot> recipe = recipeLayout.getRecipeSlots().getSlots();
            IntStream.range(1, recipe.size()).mapToObj(recipe::get).map(recipeSlot -> {
                return recipeSlot.getDisplayedItemStack().stream().findFirst();
            }).filter(Optional::isPresent).map(stack -> stack.get().copy()).forEach(requested -> {
                requested.setCount(1);
                ClientXplatAbstractions.INSTANCE.sendToServer(new IndexKeybindRequestPacket(requested));
            });
        } else {
            ((RecipeTransferButton) btn).onPress();
        }
    }
}
