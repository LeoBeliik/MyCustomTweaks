package com.leobeliik.mycustomtweaks.mixins;

import net.minecraft.core.NonNullList;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import vazkii.botania.common.item.BotaniaItems;
import vazkii.quark.content.management.module.AutomaticToolRestockModule;

@Mixin(AutomaticToolRestockModule.class)
public class DontRestockMe {

    @Redirect(method = "Lvazkii/quark/content/management/module/AutomaticToolRestockModule;switchItems(Lnet/minecraft/world/entity/player/Player;II)V",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/core/NonNullList;size()I"), remap = false)
    private int findReplacement(NonNullList list, Player player, int slot1, int slot2) {
        Inventory inventory = player.getInventory();
        return inventory.getItem(slot2).is(BotaniaItems.exchangeRod) ? 0 : inventory.items.size();
    }
}
