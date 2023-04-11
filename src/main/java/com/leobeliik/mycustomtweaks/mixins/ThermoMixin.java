package com.leobeliik.mycustomtweaks.mixins;


import blusunrize.immersiveengineering.api.utils.CapabilityReference;
import blusunrize.immersiveengineering.api.utils.DirectionUtils;
import blusunrize.immersiveengineering.common.blocks.IEBaseBlockEntity;
import blusunrize.immersiveengineering.common.blocks.metal.ThermoelectricGenBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FluidState;
import net.minecraftforge.energy.IEnergyStorage;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Map;
import java.util.function.BiFunction;

@Mixin(ThermoelectricGenBlockEntity.class)
public class ThermoMixin extends IEBaseBlockEntity {

    @Unique
    private int tik = 0;

    @Shadow
    @Final
    private Map<Direction, CapabilityReference<IEnergyStorage>> energyWrappers;

    @Shadow
    @Final
    private Map<Direction, BiFunction<Level, Block, Integer>> temperatureGetters;

    @Shadow private int energyOutput;

    public ThermoMixin(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    @Inject(method = "Lblusunrize/immersiveengineering/common/blocks/metal/ThermoelectricGenBlockEntity;tickServer()V", at = @At("TAIL"), remap = false)
    public void tickServer(CallbackInfo ci) {
        if (level == null || energyOutput < 1) return;
        tik++;
        if (tik % 24000 == 0) {
            for (Direction fd : DirectionUtils.VALUES) {
                BlockPos pos = worldPosition.relative(fd);
                BlockState state = level.getBlockState(pos);
                FluidState f = state.getFluidState();
                int temp;
                if (!f.isEmpty()) {
                    temp = f.getFluidType().getTemperature(f, level, pos);
                    switch (temp) {
                        case 300 -> swapBlock(pos, Blocks.AIR.defaultBlockState());
                        case 1300 -> swapBlock(pos, Blocks.MAGMA_BLOCK.defaultBlockState());
                        default -> {}
                    }
                } else {
                    temp = temperatureGetters.get(fd).apply(level, state.getBlock());
                    switch (temp) {
                        case 200 -> swapBlock(pos, Blocks.PACKED_ICE.defaultBlockState());
                        case 240 -> swapBlock(pos, Blocks.ICE.defaultBlockState());
                        case 260 -> swapBlock(pos, Blocks.SNOW_BLOCK.defaultBlockState());
                        case 273 -> swapBlock(pos, Blocks.WATER.defaultBlockState());
                        case 1000 -> swapBlock(pos, Blocks.OBSIDIAN.defaultBlockState());
                        default -> {}
                    }
                }
            }
        }
    }

    @Inject(method = "Lblusunrize/immersiveengineering/common/blocks/metal/ThermoelectricGenBlockEntity;onNeighborBlockChange(Lnet/minecraft/core/BlockPos;)V",
    at = @At("TAIL"), remap = false)
    public void onNeighborBlockChange(BlockPos pos, CallbackInfo ci) {
        tik = 0;
    }


    private void swapBlock(BlockPos pos, BlockState blockState) {
        if (level != null) {
            int i = blockState.is(Blocks.AIR) || blockState.is(Blocks.MAGMA_BLOCK) ? 3 : 0;
            level.destroyBlock(pos, false);
            level.setBlock(pos, blockState, i);
        }
    }

    @Override
    public void readCustomNBT(CompoundTag nbt, boolean descPacket) {

    }

    @Override
    public void writeCustomNBT(CompoundTag nbt, boolean descPacket) {

    }
}
