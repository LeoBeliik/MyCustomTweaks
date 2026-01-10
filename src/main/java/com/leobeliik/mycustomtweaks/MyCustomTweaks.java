package com.leobeliik.mycustomtweaks;

import blusunrize.immersiveengineering.common.blocks.wooden.WoodenCrateBlockEntity;
import net.dries007.tfc.common.blockentities.ThatchBedBlockEntity;
import net.dries007.tfc.common.effect.TFCEffects;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.PageButton;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.Containers;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.monster.Creeper;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.client.event.ScreenEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.entity.player.PlayerWakeUpEvent;
import net.neoforged.neoforge.event.level.BlockEvent;
import net.neoforged.neoforge.event.level.ExplosionEvent;

import java.util.Random;

@Mod(MyCustomTweaks.MODID)
public class MyCustomTweaks {
    static final String MODID = "mycustomtweaks";

    public MyCustomTweaks() {
        NeoForge.EVENT_BUS.register(this);
    }

    @SubscribeEvent
    public void OnBreakEvent(BlockEvent.BreakEvent event) {
        Level level = event.getPlayer().level();
        BlockPos pos = event.getPos();
        //make IE crates drop the items
        if (level.getBlockEntity(pos) instanceof WoodenCrateBlockEntity crate) {
            Containers.dropContents(level, pos, crate);
        }
    }

    @SubscribeEvent
    public void onCreeperExplode(ExplosionEvent.Start event) {
        if (event.getExplosion().getIndirectSourceEntity() instanceof Creeper creeper) {
            event.setCanceled(true);
            float f = creeper.isPowered() ? 2.0F : 1.0F;
            event.getLevel().explode(null, creeper.getX(), creeper.getY(), creeper.getZ(), 3f * f, false, Level.ExplosionInteraction.TNT);
        }
    }

    @SubscribeEvent
    public void onPlayerWakingUp(PlayerWakeUpEvent event) {
        Player player = event.getEntity();
        Level level = event.getEntity().level();
        String suffer = "";

        //Look at this boy, full of soup
        if (level.getBlockEntity(player.getOnPos()) instanceof ThatchBedBlockEntity bed) {
            if (new Random().nextInt(3) == 0) {
                suffer = "UGH I need a new bed..";
                player.addEffect(new MobEffectInstance(MobEffects.CONFUSION, 200, 0, false, false, false));
                player.addEffect(new MobEffectInstance(MobEffects.DIG_SLOWDOWN, 6000, 0, false, false, false));
                player.addEffect(new MobEffectInstance(MobEffects.WEAKNESS, 6000, 0, false, false, false));
                if (new Random().nextInt(4) == 0) {
                    suffer = "HOLY FUCK.. this shit broke and my back is killing me.. where the sheeps!!";
                    player.addEffect(new MobEffectInstance(MobEffects.UNLUCK, 12000, 0, false, false, false));
                    player.addEffect(new MobEffectInstance(TFCEffects.OVERBURDENED.holder(), 120, 0, false, false, false));
                    player.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 600, 3, true, true, false));
                }
            }
        }
        if (!suffer.equals(""))
            player.displayClientMessage(Component.literal(suffer), true);
    }

    @SubscribeEvent(priority = EventPriority.LOWEST) //GuiAtlas
    public void onKeyInput(ScreenEvent.KeyPressed.Pre event) {
        Screen screen = event.getScreen();
        Minecraft minecraft = screen.getMinecraft();
        String screenName = screen.getClass().getName();

        if (minecraft.level != null && minecraft.options.keyInventory.matches(event.getKeyCode(), event.getScanCode()) && screenName.contains("GuiAtlas")) {
            for (GuiEventListener renderable : screen.children()) {
                if (renderable instanceof EditBox searchBar && searchBar.canConsumeInput() || renderable instanceof PageButton) {
                    return;
                }
            }
            screen.onClose();
            event.setCanceled(true);
        }
    }
}
