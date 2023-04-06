package com.leobeliik.mycustomtweaks.mixins;

import com.mojang.authlib.GameProfile;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.projectile.ThrowableProjectile;
import net.minecraftforge.common.util.FakePlayer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import vazkii.botania.common.item.lens.LensDamage;

@Mixin(LensDamage.class)
public class DamageLensMixin {

    @Redirect(method = "Lvazkii/botania/common/item/lens/LensDamage;updateBurst(Lvazkii/botania/api/internal/IManaBurst;Lnet/minecraft/world/item/ItemStack;)V",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/projectile/ThrowableProjectile;getOwner()Lnet/minecraft/world/entity/Entity;"))
    public Entity updateBurst(ThrowableProjectile projectile) {
        return projectile.getOwner() == null ? new FakePlayer((ServerLevel) projectile.level, new GameProfile(null, "fake")) : projectile.getOwner();
    }
}
