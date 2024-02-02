package com.leobeliik.mycustomtweaks.mixins;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.inventory.CreativeModeInventoryScreen;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.CraftingMenu;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.violetmoon.quark.base.handler.InventoryTransferHandler;

@Mixin(InventoryTransferHandler.class)
public class invTransMixin {

    @Inject(method = "Lorg/violetmoon/quark/base/handler/InventoryTransferHandler;accepts(Lnet/minecraft/world/inventory/AbstractContainerMenu;Lnet/minecraft/world/entity/player/Player;)Z",
            at = @At("RETURN"), remap = false)
    private static boolean accepts(AbstractContainerMenu container, Player player, CallbackInfoReturnable cir) {
        return !(container instanceof CraftingMenu) && !(Minecraft.getInstance().screen instanceof CreativeModeInventoryScreen) && cir.getReturnValueZ();
    }
}
