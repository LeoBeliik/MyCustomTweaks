package com.leobeliik.mycustomtweaks;

import com.tiviacz.travelersbackpack.handlers.EntityItemHandler;
import dan200.computercraft.api.ComputerCraftAPI;
import de.dafuqs.spectrum.blocks.decoration.WandLightBlock;
import de.dafuqs.spectrum.registries.SpectrumItems;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.player.PlayerBlockBreakEvents;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Items;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MyCustomTweaks implements ModInitializer {
    public static final Logger LOGGER = LoggerFactory.getLogger("mycustomtweaks");

    public MyCustomTweaks() {
    }

    public void onInitialize() {
        ComputerCraftCompat.register();
        PlayerBlockBreakEvents.AFTER.register(new lightBreak());
    }

    public class ComputerCraftCompat {
        static void register() {
            ComputerCraftAPI.registerGenericSource(new ColorPickerPeripheral());
        }
    }

    public class lightBreak implements PlayerBlockBreakEvents.After {

        @Override
        public void afterBlockBreak(World world, PlayerEntity playerEntity, BlockPos blockPos, BlockState blockState, @Nullable BlockEntity blockEntity) {
            if (blockState.getBlock() instanceof WandLightBlock lightBlock) {
                world.spawnEntity(new ItemEntity(world, blockPos.getX(), blockPos.getY(), blockPos.getZ(),
                        SpectrumItems.SHIMMERSTONE_GEM.getDefaultStack(), 0, 1, 0));
            }
        }
    }
}