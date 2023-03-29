package com.leobeliik.mycustomtweaks.mixins;

import com.simibubi.create.content.contraptions.components.deployer.DeployerBlock;
import net.minecraft.core.Direction;
import net.minecraft.world.item.context.BlockPlaceContext;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(DeployerBlock.class)
public class DeployerMixin {

    @Inject(method = "Lcom/simibubi/create/content/contraptions/components/deployer/DeployerBlock;getFacingForPlacement(Lnet/minecraft/world/item/context/BlockPlaceContext;)Lnet/minecraft/core/Direction;",
            at = @At("RETURN"), remap = false)
    public Direction getFacingForPlacement(BlockPlaceContext context, CallbackInfoReturnable cir) {
        return Direction.DOWN;
    }
}
