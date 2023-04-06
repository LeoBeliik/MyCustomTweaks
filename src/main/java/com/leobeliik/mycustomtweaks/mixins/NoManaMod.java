package com.leobeliik.mycustomtweaks.mixins;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;
import tcintegrations.items.modifiers.traits.ManaModifier;

@Mixin(ManaModifier.class)
public class NoManaMod {

    @Redirect(method = "Ltcintegrations/items/modifiers/traits/ManaModifier;onInventoryTick(Lslimeknights/tconstruct/library/tools/nbt/IToolStackView;ILnet/minecraft/world/level/Level;Lnet/minecraft/world/entity/LivingEntity;IZZLnet/minecraft/world/item/ItemStack;)V",
            at = @At(value = "INVOKE", target = "Lslimeknights/tconstruct/library/tools/nbt/IToolStackView;getDamage()I"), remap = false)
    public int noManaRepair(IToolStackView tool) {
        return 0;
    }

}
