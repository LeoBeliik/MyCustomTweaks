package com.leobeliik.mycustomtweaks.mixins;

import net.minecraft.world.entity.ExperienceOrb;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import vazkii.botania.client.fx.SparkleParticleData;
import vazkii.botania.common.component.BotaniaDataComponents;
import vazkii.botania.common.helper.MathHelper;
import vazkii.botania.common.item.equipment.bauble.RingOfMagnetizationItem;

import java.util.List;

import static com.leobeliik.mycustomtweaks.MyCustomTweaksClient.magnetKey;
import static vazkii.botania.common.item.equipment.bauble.RingOfMagnetizationItem.DEFAULT_RANGE;

@Mixin(RingOfMagnetizationItem.class)
public class MagnetRingMixin {

	@Redirect(method = "Lvazkii/botania/common/item/equipment/bauble/RingOfMagnetizationItem;onWornTick(Lnet/minecraft/world/item/ItemStack;Lnet/minecraft/world/entity/LivingEntity;)V",
			at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/LivingEntity;isShiftKeyDown()Z"))
	private boolean activeMagnet(LivingEntity b) {
		return magnetKey.isDown();
	}

	@Inject(method = "Lvazkii/botania/common/item/equipment/bauble/RingOfMagnetizationItem;onWornTick(Lnet/minecraft/world/item/ItemStack;Lnet/minecraft/world/entity/LivingEntity;)V",
			at = @At(value = "INVOKE_ASSIGN", target = "Lnet/minecraft/world/item/ItemStack;getOrDefault(Lnet/minecraft/core/component/DataComponentType;Ljava/lang/Object;)Ljava/lang/Object;"))
	public void onMagnetTick(ItemStack stack, LivingEntity living, CallbackInfo ci) {
		double x = living.getX();
		double y = living.getY() + 0.5 * living.getEyeHeight();
		double z = living.getZ();

		int range = stack.getOrDefault(BotaniaDataComponents.RANGE, DEFAULT_RANGE) * 3;

		Level level = living.level();
		List<ExperienceOrb> experience = level.getEntitiesOfClass(ExperienceOrb.class, new AABB(x - (double) range, y - (double) range, z - (double) range, x + (double) range, y + (double) range, z + (double) range));

		int pulled = 0;

		for (ExperienceOrb orb : experience) {
			if (pulled > 200) {
				break;
			}

			MathHelper.setEntityMotionFromVector(orb, new Vec3(x, y, z), 1F);
			if (level.isClientSide) {
				boolean red = level.random.nextBoolean();
				float r = red ? 1.0F : 0.0F;
				float b = red ? 0.0F : 1.0F;
				SparkleParticleData data = SparkleParticleData.sparkle(1.0F, r, 0.0F, b, 3);
				level.addParticle(data, orb.getX(), orb.getY(), orb.getZ(), 0.0, 0.0, 0.0);
			}

			++pulled;
		}
	}
}
