package com.leobeliik.mycustomtweaks;

import org.slf4j.Logger;

import com.mojang.logging.LogUtils;

import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.ModContainer;

@Mod(MyCustomTweaks.MODID)
public class MyCustomTweaks {
    public static final String MODID = "mycustomtweaks";
    public static final Logger LOGGER = LogUtils.getLogger();

    public MyCustomTweaks(IEventBus modEventBus, ModContainer modContainer) {
    }
}
