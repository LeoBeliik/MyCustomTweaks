package com.leobeliik.mycustomtweaks.mixins;


import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.ExperienceOrb;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;
import vazkii.botania.api.BotaniaAPI;
import vazkii.botania.client.fx.SparkleParticleData;
import vazkii.botania.common.helper.MathHelper;
import vazkii.botania.common.item.equipment.bauble.RingOfMagnetizationItem;
import vazkii.botania.common.lib.BotaniaTags;
import vazkii.botania.mixin.ItemEntityAccessor;
import vazkii.botania.xplat.XplatAbstractions;

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

@Mixin(RingOfMagnetizationItem.class)
public class MagnetRingMixin {

    @Inject(method = "Lvazkii/botania/common/item/equipment/bauble/RingOfMagnetizationItem;onWornTick(Lnet/minecraft/world/item/ItemStack;Lnet/minecraft/world/entity/LivingEntity;)V",
    at = @At(value = "INVOKE_ASSIGN", target = "Lnet/minecraft/world/level/Level;getEntitiesOfClass(Ljava/lang/Class;Lnet/minecraft/world/phys/AABB;)Ljava/util/List;"),
    locals = LocalCapture.CAPTURE_FAILHARD, cancellable = true)
    public void onMagnetTick(ItemStack stack, LivingEntity living, CallbackInfo CIR, int cooldown, double x, double y, double z, int range, List<ItemEntity> items) {

        List<ExperienceOrb> experience = living.level.getEntitiesOfClass(ExperienceOrb.class, new AABB(x - (double)range, y - (double)range, z - (double)range, x + (double)range, y + (double)range, z + (double)range));

        int pulled = 0;

        for (ExperienceOrb orb : experience) {
            if (pulled > 200) {
                break;
            }

            MathHelper.setEntityMotionFromVector(orb, new Vec3(x, y, z), 1F);
            if (living.level.isClientSide) {
                boolean red = living.level.random.nextBoolean();
                float r = red ? 1.0F : 0.0F;
                float b = red ? 0.0F : 1.0F;
                SparkleParticleData data = SparkleParticleData.sparkle(1.0F, r, 0.0F, b, 3);
                living.level.addParticle(data, orb.getX(), orb.getY(), orb.getZ(), 0.0, 0.0, 0.0);
            }

            ++pulled;
        }
    }
}
