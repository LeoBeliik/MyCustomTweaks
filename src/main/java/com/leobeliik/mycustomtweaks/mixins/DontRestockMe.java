package com.leobeliik.mycustomtweaks.mixins;

import net.minecraft.core.NonNullList;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import vazkii.botania.common.item.BotaniaItems;
import vazkii.quark.content.management.module.AutomaticToolRestockModule;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Mixin(AutomaticToolRestockModule.class)
public class DontRestockMe {


    @Unique
    private List<Item> dontRestock = new ArrayList<>();

    @Redirect(method = "Lvazkii/quark/content/management/module/AutomaticToolRestockModule;switchItems(Lnet/minecraft/world/entity/player/Player;II)V",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/core/NonNullList;size()I"), remap = false)
    private int findReplacement(NonNullList list, Player player, int slot1, int slot2) {
        if (dontRestock.isEmpty()) {
            dontRestock = Arrays.asList(BotaniaItems.exchangeRod, BotaniaItems.cobbleRod, BotaniaItems.dirtRod, BotaniaItems.skyDirtRod);
        }

        Inventory inventory = player.getInventory();
        for (Item item : dontRestock) {
            if (inventory.getItem(slot2).is(item)) {
                return 0;
            }
        }
        return inventory.items.size();
    }
}
