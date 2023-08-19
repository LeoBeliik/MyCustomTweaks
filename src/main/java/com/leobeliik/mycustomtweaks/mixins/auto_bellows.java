package com.leobeliik.mycustomtweaks.mixins;

import net.dries007.tfc.common.blocks.devices.IBellowsConsumer;
import net.mehvahdjukaar.supplementaries.common.block.blocks.BellowsBlock;
import net.mehvahdjukaar.supplementaries.common.block.tiles.BellowsBlockTile;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import static net.dries007.tfc.common.blockentities.BellowsBlockEntity.BELLOWS_AIR;

@Mixin(BellowsBlockTile.class)
public class auto_bellows extends BlockEntity {

    public auto_bellows(BlockEntityType<?> pType, BlockPos pPos, BlockState pBlockState) {
        super(pType, pPos, pBlockState);
    }

    @Inject(method = "Lnet/mehvahdjukaar/supplementaries/common/block/tiles/BellowsBlockTile;tickFurnaces(Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/Level;)V",
            at = @At(value = "TAIL"), remap = false)
    private void tickFurnaces(BlockPos pos, Level level, CallbackInfo ci) {
        final Direction direction = getBlockState().getValue(BellowsBlock.FACING);
        for (IBellowsConsumer.Offset offset : IBellowsConsumer.offsets()) {
            final BlockPos airPosition = worldPosition.above(offset.up())
                    .relative(direction, offset.out())
                    .relative(direction.getClockWise(), offset.side());
            final BlockState state = level.getBlockState(airPosition);
            if (state.getBlock() instanceof IBellowsConsumer consumer) {
                if (consumer.canAcceptAir(level, airPosition, state)) {
                    consumer.intakeAir(level, airPosition, state, BELLOWS_AIR);
                }
            }
        }
    }

}
