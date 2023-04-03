package com.leobeliik.mycustomtweaks.mixins;

import net.minecraft.core.BlockPos;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.ThrowableProjectile;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import vazkii.botania.api.internal.IManaBurst;
import vazkii.botania.common.entity.EntityManaBurst;
import vazkii.botania.common.item.ItemManaGun;
import vazkii.botania.common.item.equipment.tool.terrasteel.ItemTerraSword;

@Mixin(EntityManaBurst.class)
public abstract class ManaBurstEntityMixin extends ThrowableProjectile implements IManaBurst {


    protected ManaBurstEntityMixin(EntityType<? extends ThrowableProjectile> p_37466_, Level p_37467_) {
        super(p_37466_, p_37467_);
    }

    @Shadow
    public abstract BlockPos getBurstSourceBlockPos();

    @Shadow
    @Final
    private static EntityDataAccessor<Integer> COLOR;

    @Inject(method = "Lvazkii/botania/common/entity/EntityManaBurst;getParticleSize()F", at = @At("RETURN"), remap = false)
    public float getParticleSizeTweak(CallbackInfoReturnable ci) {
        if (entity().getOwner() instanceof Player player) {
            Item item = player.getItemInHand(InteractionHand.MAIN_HAND).getItem();
            if (item instanceof ItemTerraSword || item instanceof ItemManaGun) {
                return 0F;
            }
        }
        return ci.getReturnValueF();
    }

    @Inject(method = "Lvazkii/botania/common/entity/EntityManaBurst;setColor(I)V", at = @At("RETURN"), remap = false)
    public void setColorTweak(CallbackInfo ci) {
        if (entity().getOwner() instanceof Player player && player.getItemInHand(InteractionHand.MAIN_HAND).getItem() instanceof ItemTerraSword) {
            this.entityData.set(COLOR, Mth.hsvToRgb((level.getGameTime() * 2F % 360F) / 360.0F, 1.0F, 1.0F));
        }
    }

}
