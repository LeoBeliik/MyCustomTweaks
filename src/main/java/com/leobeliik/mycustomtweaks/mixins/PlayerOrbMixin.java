package com.leobeliik.mycustomtweaks.mixins;

import com.llamalad7.mixinextras.injector.v2.WrapWithCondition;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Slice;

@Mixin(Player.class)
public class PlayerOrbMixin {
	@WrapWithCondition(
			method = "aiStep",
			at = @At(
					value = "INVOKE",
					target = "Lnet/minecraft/world/entity/player/Player;touch(Lnet/minecraft/world/entity/Entity;)V",
					ordinal = 0
			),
			slice = @Slice(
					from = @At(
							value = "INVOKE",
							target = "Lnet/minecraft/Util;getRandom(Ljava/util/List;Lnet/minecraft/util/RandomSource;)Ljava/lang/Object;"
					)
			)
	)
	private boolean shouldPickupXp(Player instance, Entity entity) {
		// should only be XP orbs at this point, but just to be sure we always allow other entity types
		return instance.takeXpDelay < 5 || entity.getType() != EntityType.EXPERIENCE_ORB;
	}
}
