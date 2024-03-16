package com.leobeliik.mycustomtweaks;

import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.screens.Screen;
import net.minecraftforge.client.event.ScreenEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.loading.FMLLoader;

@Mod(MyCustomTweaks.MODID)
public class MyCustomTweaks {
    public static final String MODID = "mycustomtweaks";

    public MyCustomTweaks() {
        MinecraftForge.EVENT_BUS.register(this);
        IEventBus bus = FMLJavaModLoadingContext.get().getModEventBus();
    }

    @SubscribeEvent
    public void tomPlzRclick(ScreenEvent.MouseButtonPressed event) {
        if (FMLLoader.getLoadingModList().getModFileById("storagemodstoragemod") != null) {
            Screen screen = event.getScreen();
            if (screen.toString().contains("TerminalScreen")) {
                for (GuiEventListener child : screen.children()) {
                    if (child instanceof EditBox) {
                        child.setFocused(child.isMouseOver(event.getMouseX(), event.getMouseY()));
                        break;
                    }
                }
            }
        }
    }

}
