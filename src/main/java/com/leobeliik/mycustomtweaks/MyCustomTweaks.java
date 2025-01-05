package com.leobeliik.mycustomtweaks;

import blusunrize.immersiveengineering.common.blocks.wooden.WoodenCrateBlockEntity;
import com.leobeliik.mycustomtweaks.Network.JustStackIt;
import com.leobeliik.mycustomtweaks.Network.Network;
import com.mojang.blaze3d.platform.InputConstants;
import journeymap.client.waypoint.Waypoint;
import journeymap.client.waypoint.WaypointStore;
import net.dries007.tfc.client.TFCKeyBindings;
import net.dries007.tfc.common.TFCEffects;
import net.dries007.tfc.common.blockentities.ThatchBedBlockEntity;
import net.dries007.tfc.common.blocks.RiverWaterBlock;
import net.dries007.tfc.common.blocks.TFCBlockStateProperties;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.Containers;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.monster.Creeper;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.material.Fluids;
import net.minecraftforge.client.event.ScreenEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.event.entity.player.PlayerSleepInBedEvent;
import net.minecraftforge.event.entity.player.PlayerWakeUpEvent;
import net.minecraftforge.event.level.BlockEvent;
import net.minecraftforge.event.level.ExplosionEvent;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.RegistryObject;

import java.awt.*;

import static net.dries007.tfc.TerraFirmaCraft.MOD_ID;
import static net.dries007.tfc.common.TFCEffects.register;

@Mod(MyCustomTweaks.MODID)
public class MyCustomTweaks {
    public static final String MODID = "mycustomtweaks";
    public static final RegistryObject<MobEffect> INSOMNIA = register("insomnia", () -> new TFCEffects.TFCMobEffect(MobEffectCategory.BENEFICIAL, 0));
    public static final TagKey<Item> SMALL_ORES = ItemTags.create(new ResourceLocation(MOD_ID, "small_ore_pieces"));

    private long time;

    public MyCustomTweaks() {
        MinecraftForge.EVENT_BUS.register(this);
        IEventBus bus = FMLJavaModLoadingContext.get().getModEventBus();
        Network.register();
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
                if (time % 24000 < 18000) {
                    ((ServerLevel) level).setDayTime(currentTime - ((currentTime - time) / 2));
                    player.displayClientMessage(Component.nullToEmpty("You struggle to keep sleeping"), true);
                    player.addEffect(new MobEffectInstance(INSOMNIA.get(), 6000, -1, false, false, false));
                }
            }
            time = 0;
        }
    }

    @SubscribeEvent
    public void onPlayerSleeping(PlayerSleepInBedEvent event) {
        Player player = event.getEntity();
        Level level = player.level();
        if (level.getBlockEntity(event.getPos()) instanceof ThatchBedBlockEntity) {
            if (player.hasEffect(INSOMNIA.get()) && event.getResult().equals(Event.Result.DEFAULT)) {
                player.displayClientMessage(Component.nullToEmpty("You can't go to sleep now"), true);
                event.setResult(Player.BedSleepingProblem.OTHER_PROBLEM);
            } else {
                time = level.getDayTime();
            }
        }
    }

    @SubscribeEvent
    public void onBlockPlaced(BlockEvent.EntityPlaceEvent event) {
        if (event.getBlockSnapshot().getReplacedBlock().getBlock() instanceof RiverWaterBlock) {
            Waterlog(event.getPlacedBlock(), (Level) event.getLevel(), event.getPos());
        }
    }

    @SubscribeEvent
    public void onBlockPlaced(BlockEvent.EntityMultiPlaceEvent event) {
        if (event.getBlockSnapshot().getReplacedBlock().getBlock() instanceof RiverWaterBlock) {
            Waterlog(event.getPlacedBlock(), (Level) event.getLevel(), event.getPos());
        }
    }

    @SubscribeEvent(priority = EventPriority.LOW)
    public void onScreenKey(ScreenEvent.KeyPressed event) {
        if (TFCKeyBindings.STACK_FOOD.isActiveAndMatches(InputConstants.getKey(event.getKeyCode(), event.getScanCode())) && event.getScreen() instanceof AbstractContainerScreen inv) {
            Slot slot = inv.getSlotUnderMouse();
            if (slot != null) {
                Network.sendToServer(new JustStackIt(slot.index));
            }
        }
    }

    @SubscribeEvent
    public void onOrePickup(PlayerInteractEvent.RightClickBlock event) {
        boolean tooClose = false;
        BlockPos pos = event.getHitVec().getBlockPos();
        Item item = event.getLevel().getBlockState(pos).getBlock().asItem();
        String name = item.getDescription().getString();
        if (item.getDefaultInstance().is(SMALL_ORES)) {
            for (Waypoint waypoint : WaypointStore.INSTANCE.getAll()) {
                if (waypoint.getName().equals(name) && waypoint.getBlockPos().closerToCenterThan(pos.getCenter(), 32)) {
                    tooClose = true;
                    break;
                }
            }
            if (!tooClose) {
                WaypointStore.INSTANCE.add(new Waypoint(name, pos, Color.GRAY, Waypoint.Type.Normal, "minecraft:overworld", false));
            }
        }
    }

    private void Waterlog(BlockState block, Level level, BlockPos pos) {
        BlockState state = block.trySetValue(BlockStateProperties.WATERLOGGED, true).trySetValue(TFCBlockStateProperties.WATER, TFCBlockStateProperties.WATER.keyFor(Fluids.WATER));
        level.setBlock(pos, state, 0);
    }
}
