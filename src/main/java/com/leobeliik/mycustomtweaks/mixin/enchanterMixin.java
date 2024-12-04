package com.leobeliik.mycustomtweaks.mixin;

import de.dafuqs.spectrum.blocks.enchanter.EnchanterBlockEntity;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.world.World;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.UUID;

@Mixin(EnchanterBlockEntity.class)
public abstract class enchanterMixin {

    @Shadow
    public abstract UUID getOwnerUUID();

    @Inject(method = "Lde/dafuqs/spectrum/blocks/enchanter/EnchanterBlockEntity;getRequiredExperienceToEnchantCenterItem(Lde/dafuqs/spectrum/blocks/enchanter/EnchanterBlockEntity;)I",
            at = @At("RETURN"), remap = false)
    private static void showMeXPAmount(@NotNull EnchanterBlockEntity enchanterBlockEntity, CallbackInfoReturnable cir) {
        World world = MinecraftClient.getInstance().world;
        if (world != null) {
            PlayerEntity player = world.getPlayerByUuid(enchanterBlockEntity.getOwnerUUID());
            if (player != null) {
                player.sendMessage(Text.literal("This shit will cost you " + cir.getReturnValueI() + " experience!!"), true);
            }
        }
    }
}
