package com.leobeliik.mycustomtweaks.mixins;

import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Multimap;
import com.hollingsworth.arsnouveau.common.perk.StarbunclePerk;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.ForgeMod;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;
import static com.hollingsworth.arsnouveau.common.perk.StarbunclePerk.PERK_STEP_UUID;

@Mixin(StarbunclePerk.class)
public class stepMixin {

    @Inject(method = "Lcom/hollingsworth/arsnouveau/common/perk/StarbunclePerk;getModifiers(Lnet/minecraft/world/entity/EquipmentSlot;Lnet/minecraft/world/item/ItemStack;I)Lcom/google/common/collect/Multimap;",
            at = @At(value = "INVOKE_ASSIGN", target = "Lcom/google/common/collect/ImmutableMultimap$Builder;build()Lcom/google/common/collect/ImmutableMultimap;"), locals = LocalCapture.CAPTURE_FAILHARD,
            remap = false)
    public Multimap<Attribute, AttributeModifier> modifyStepArs(EquipmentSlot pEquipmentSlot, ItemStack stack, int slotValue, CallbackInfoReturnable cir, ImmutableMultimap.Builder<Attribute, AttributeModifier> modifiers) {
        System.out.println("SLOT " + slotValue);
        if (slotValue == 2) {
            modifiers.put(ForgeMod.STEP_HEIGHT_ADDITION.get(), new AttributeModifier(PERK_STEP_UUID, "StarbuncleStepPerk", 1.0, AttributeModifier.Operation.ADDITION));
            return modifiers.build();
        }
        return (Multimap<Attribute, AttributeModifier>) cir.getReturnValue();
    }
}