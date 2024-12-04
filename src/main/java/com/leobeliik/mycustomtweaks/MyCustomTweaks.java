package com.leobeliik.mycustomtweaks;

import com.klikli_dev.modonomicon.client.gui.book.*;
import com.tiviacz.travelersbackpack.handlers.EntityItemHandler;
import dan200.computercraft.api.ComputerCraftAPI;
import de.dafuqs.spectrum.blocks.decoration.DecayingLightBlock;
import de.dafuqs.spectrum.blocks.decoration.WandLightBlock;
import de.dafuqs.spectrum.registries.SpectrumItems;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.screen.v1.ScreenEvents;
import net.fabricmc.fabric.api.client.screen.v1.ScreenKeyboardEvents;
import net.fabricmc.fabric.api.event.player.PlayerBlockBreakEvents;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.EditBox;
import net.minecraft.client.gui.Element;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.option.KeyBinding;
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
        PlayerBlockBreakEvents.AFTER.register((world, playerEntity, blockPos, blockState, blockEntity) -> {
            if (blockState.getBlock() instanceof WandLightBlock lightBlock && !(lightBlock instanceof DecayingLightBlock)) {
                world.spawnEntity(new ItemEntity(world, blockPos.getX(), blockPos.getY(), blockPos.getZ(),
                        SpectrumItems.SHIMMERSTONE_GEM.getDefaultStack(), 0, 0.1, 0));
            }
        });

        ScreenEvents.AFTER_INIT.register((minecraftClient, screen, i, i1) -> {
            KeyBinding inventoryKey = minecraftClient.options.inventoryKey;
            ScreenKeyboardEvents.afterKeyPress(screen).register((s, j, j1, j2) -> {
                if ((s instanceof BookOverviewScreen || s instanceof BookContentScreen) && inventoryKey.matchesKey(j, j1)) {
                    s.close();
                }
            });
        });
    }

    public class ComputerCraftCompat {
        static void register() {
            ComputerCraftAPI.registerGenericSource(new ColorPickerPeripheral());
        }
    }
}