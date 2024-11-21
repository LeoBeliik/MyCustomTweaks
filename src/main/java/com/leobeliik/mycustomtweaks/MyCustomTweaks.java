package com.leobeliik.mycustomtweaks;

import dan200.computercraft.api.ComputerCraftAPI;
import net.fabricmc.api.ModInitializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MyCustomTweaks implements ModInitializer {
    public static final Logger LOGGER = LoggerFactory.getLogger("mycustomtweaks");

    public MyCustomTweaks() {
    }

    public void onInitialize() {
        ComputerCraftCompat.register();
    }

    public class ComputerCraftCompat {
        static void register() {
            ComputerCraftAPI.registerGenericSource(new ColorPickerPeripheral());
        }
    }
}