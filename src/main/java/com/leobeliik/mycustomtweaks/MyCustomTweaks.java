package com.leobeliik.mycustomtweaks;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.monster.Creeper;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.event.level.ExplosionEvent;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

@Mod(MyCustomTweaks.MODID)
public class MyCustomTweaks {
    static final String MODID = "mycustomtweaks";

    public MyCustomTweaks() {
        MinecraftForge.EVENT_BUS.register(this);
        IEventBus bus = FMLJavaModLoadingContext.get().getModEventBus();
    }

    @SubscribeEvent
    public void onCreeperExplode(ExplosionEvent.Start event) {
        if (event.getExplosion().getExploder() instanceof Creeper creeper) {
            event.setCanceled(true);
            float f = creeper.isPowered() ? 2.0F : 1.0F;
            event.getLevel().explode(null, creeper.getX(), creeper.getY(), creeper.getZ(), 3f * f, false, Level.ExplosionInteraction.TNT);
        }
    }

    @SubscribeEvent(priority = EventPriority.LOW)
    public void onPlayerInteract(PlayerInteractEvent.RightClickBlock event) {
        Player player = event.getEntity();
        if (!event.getLevel().isClientSide || player.isDiscrete() || event.isCanceled() || event.getResult() == Event.Result.DENY || event.getUseBlock() == Event.Result.DENY)
            return;

        Level world = event.getLevel();
        BlockPos pos = event.getPos();
    }

}
