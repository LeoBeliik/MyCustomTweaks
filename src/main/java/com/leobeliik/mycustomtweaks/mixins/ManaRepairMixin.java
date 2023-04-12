package com.leobeliik.mycustomtweaks.mixins;

import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import vazkii.botania.common.impl.mana.ManaItemHandlerImpl;
import java.util.Arrays;

@Mixin(ManaItemHandlerImpl.class)
public class ManaRepairMixin {

    @Redirect(method = "Lvazkii/botania/common/impl/mana/ManaItemHandlerImpl;requestManaExact(Lnet/minecraft/world/item/ItemStack;Lnet/minecraft/world/entity/player/Player;IZ)Z",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/ItemStack;isEmpty()Z"))
    public boolean requestManaExactForTool(ItemStack stack) {
        String[] ban = {"manasteel", "elementium", "terra", "glass", "sword", "bow", "manaweave"};
        return Arrays.stream(ban).anyMatch(s -> stack.getDescriptionId().contains(s));
    }
}
