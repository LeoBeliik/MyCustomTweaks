package com.leobeliik.mycustomtweaks.mixins;

import com.simibubi.create.content.kinetics.base.DirectionalAxisKineticBlock;
import com.simibubi.create.content.kinetics.saw.SawBlock;
import net.minecraft.core.Direction;
import net.minecraft.world.item.context.BlockPlaceContext;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(SawBlock.class)
public class SawMixin extends DirectionalAxisKineticBlock {

    public SawMixin(Properties properties) {
        super(properties);
    }

    @Override
    protected Direction getFacingForPlacement(BlockPlaceContext context) {
        return Direction.UP;
    }
}
