package com.leobeliik.mycustomtweaks;

import blusunrize.immersiveengineering.common.blocks.wooden.WoodenCrateBlockEntity;
import com.mojang.blaze3d.platform.InputConstants;
import net.dries007.tfc.common.TFCEffects;
import net.dries007.tfc.common.blockentities.ThatchBedBlockEntity;
import net.minecraft.client.KeyMapping;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.Containers;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.monster.Creeper;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.player.PlayerSleepInBedEvent;
import net.minecraftforge.event.entity.player.PlayerWakeUpEvent;
import net.minecraftforge.event.level.BlockEvent;
import net.minecraftforge.event.level.ExplosionEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.RegistryObject;
import static net.dries007.tfc.common.TFCEffects.register;

@Mod(MyCustomTweaks.MODID)
public class MyCustomTweaks {
    public static final String MODID = "mycustomtweaks";
    public static final RegistryObject<MobEffect> INSOMNIA = register("insomnia", () -> new TFCEffects.TFCMobEffect(MobEffectCategory.BENEFICIAL, 0));
    private long time;

    public MyCustomTweaks() {
        MinecraftForge.EVENT_BUS.register(this);
        IEventBus bus = FMLJavaModLoadingContext.get().getModEventBus();
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
        if (event.getExplosion().getExploder() instanceof Creeper creeper) {
            event.setCanceled(true);
            float f = creeper.isPowered() ? 2.0F : 1.0F;
            event.getLevel().explode(null, creeper.getX(), creeper.getY(), creeper.getZ(), 3f * f, false, Level.ExplosionInteraction.TNT);
        }
    }

    @SubscribeEvent
    public void onPlayerWakingUp(PlayerWakeUpEvent event) {
        Player player = event.getEntity();
        Level level = event.getEntity().level();

        if (time != 0) {
            if (level instanceof ServerLevel) {
                long currentTime = level.getDayTime();
                ((ServerLevel) level).setDayTime(currentTime - ((currentTime - time) / 2));
                player.addEffect(new MobEffectInstance(INSOMNIA.get(), 6000, -1, false, false, false));
                player.displayClientMessage(Component.nullToEmpty("You struggle to keep sleeping"), true);
            }
            time = 0;
        }
    }

    @SubscribeEvent
    public void onPlayerSleeping(PlayerSleepInBedEvent event) {
        Player player = event.getEntity();
        Level level = player.level();
        if (level.getBlockEntity(event.getPos()) instanceof ThatchBedBlockEntity) {
            if (player.hasEffect(INSOMNIA.get())) {
                player.displayClientMessage(Component.nullToEmpty("You can't go to sleep now"), true);
                event.setCanceled(true);
            } else {
                time = level.getDayTime();
            }
        }
    }
}
