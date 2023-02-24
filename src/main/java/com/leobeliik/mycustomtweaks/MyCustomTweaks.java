package com.leobeliik.mycustomtweaks;

import com.leobeliik.mycustomtweaks.items.PlayerSeedItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BaseContainerBlockEntity;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import vazkii.botania.common.block.block_entity.SimpleInventoryBlockEntity;
import vazkii.botania.common.item.BotaniaItems;

@Mod(MyCustomTweaks.MODID)
public class MyCustomTweaks {
    public static final String MODID = "mycustomtweaks";
    private static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, MODID);

    public MyCustomTweaks() {
        MinecraftForge.EVENT_BUS.register(this);
        IEventBus bus = FMLJavaModLoadingContext.get().getModEventBus();
        Registry();
    }

    private void Registry() {
        ITEMS.register(FMLJavaModLoadingContext.get().getModEventBus());
    }

    @SubscribeEvent
    public void onCorporeaUse(PlayerInteractEvent.RightClickBlock event) {
        if (event.getItemStack().is(Blocks.WITHER_ROSE.asItem()) && event.getEntity().isCrouching()) {
            event.setUseItem(Event.Result.DENY);
        }
        BlockEntity be = event.getLevel().getBlockEntity(event.getHitVec().getBlockPos());
        if (be instanceof BaseContainerBlockEntity || be instanceof SimpleInventoryBlockEntity && event.getItemStack().is(BotaniaItems.corporeaSpark)) {
            event.setUseBlock(Event.Result.DENY);
        }
    }

    public static final RegistryObject<Item> PLAYER_SEED_ITEM = ITEMS.register("player_seed", () ->
            new PlayerSeedItem(new Item.Properties().stacksTo(8)));
}
