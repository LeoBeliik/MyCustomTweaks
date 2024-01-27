package com.leobeliik.mycustomtweaks.mixins;

import net.dries007.tfc.common.blockentities.rotation.WindmillBlockEntity;
import net.dries007.tfc.common.blocks.rotation.WindmillBlock;
import net.dries007.tfc.util.rotation.Rotation;
import net.dries007.tfc.util.rotation.SourceNode;
import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(WindmillBlockEntity.class)
public class windmillMixin {

    @Shadow
    @Final
    private SourceNode node;

    //@Inject(method = "", at = @At("HEAD"))
    @Overwrite(remap = false)
    public static void clientTick(Level level, BlockPos pos, BlockState state, WindmillBlockEntity windmill) {
        Rotation.Tickable rotation = ((SourceNode) windmill.getRotationNode()).rotation();
        rotation.tick();
        float targetSpeed = Mth.map((float) state.getValue(WindmillBlock.COUNT), 1.0F, 5.0F, 0.015707964F, 0.03926991F * 2);
        float currentSpeed = rotation.speed();
        float nextSpeed = targetSpeed > currentSpeed
                ? Math.min(targetSpeed, currentSpeed + 1.5707964E-4F)
                : Math.max(targetSpeed, currentSpeed - 1.5707964E-4F);
        rotation.setSpeed(nextSpeed);
    }
}
