package com.leobeliik.mycustomtweaks;

import com.mojang.blaze3d.platform.InputConstants;
import io.redspace.ironsspellbooks.api.events.ChangeManaEvent;
import io.redspace.ironsspellbooks.api.events.SpellOnCastEvent;
import io.redspace.ironsspellbooks.api.magic.MagicData;
import io.redspace.ironsspellbooks.api.spells.SpellData;
import net.minecraft.client.KeyMapping;
import net.minecraft.network.chat.contents.TranslatableContents;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.monster.Creeper;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BaseContainerBlockEntity;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.RegisterKeyMappingsEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.event.level.ExplosionEvent;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import vazkii.botania.api.BotaniaAPI;
import vazkii.botania.api.mana.ManaItem;
import vazkii.botania.api.mana.ManaItemHandler;
import vazkii.botania.common.block.block_entity.SimpleInventoryBlockEntity;
import vazkii.botania.common.impl.BotaniaAPIImpl;
import vazkii.botania.common.item.BotaniaItems;
import vazkii.botania.common.item.rod.SkiesRodItem;

@Mod(MyCustomTweaks.MODID)
public class MyCustomTweaks {
    public static final String MODID = "mycustomtweaks";
    //private static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, MODID);

    public MyCustomTweaks() {
        MinecraftForge.EVENT_BUS.register(this);
        IEventBus bus = FMLJavaModLoadingContext.get().getModEventBus();
        //bus.addListener(this::onCreativeModeTabBuildContents);
        //ITEMS.register(FMLJavaModLoadingContext.get().getModEventBus());
    }

    /*public static final RegistryObject<Item> TERRASTEEL_TEMPLATE = ITEMS.register("terrasteel_template", () ->
            new Item(new Item.Properties().stacksTo(1)));*/

    /*@SubscribeEvent
    public void onCreativeModeTabBuildContents(BuildCreativeModeTabContentsEvent event) {
        if (event.getTabKey() == CreativeModeTabs.INGREDIENTS)
            event.accept(new ItemStack(TERRASTEEL_TEMPLATE.get()));
    }*/

    @SubscribeEvent
    public void onCreeperExplode(ExplosionEvent.Start event) {
        if (event.getExplosion().getExploder() instanceof Creeper creeper) {
            event.setCanceled(true);
            float f = creeper.isPowered() ? 2.0F : 1.0F;
            event.getLevel().explode(null, creeper.getX(), creeper.getY(), creeper.getZ(), 3f * f, false, Level.ExplosionInteraction.TNT);
        }
    }

    public static final KeyMapping magnetKey = setMagnetKey();

    @Mod.EventBusSubscriber(modid = MODID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
    public class ClientModListener {
        @SubscribeEvent
        public static void keyRegistry(final RegisterKeyMappingsEvent event) {
            event.register(magnetKey);
        }
    }

    @OnlyIn(Dist.CLIENT)
    private static KeyMapping setMagnetKey() {
        return new KeyMapping(
                new TranslatableContents("Toggle Botania Magnet KEY", null, TranslatableContents.NO_ARGS).getKey(),
                InputConstants.Type.KEYSYM,
                InputConstants.UNKNOWN.getValue(),
                "key.categories.misc");
    }

    @SubscribeEvent
    public void onShatBreak(PlayerEvent.BreakSpeed event) {
        ItemStack stack = event.getEntity().getItemInHand(InteractionHand.MAIN_HAND);
        if (stack.is(BotaniaItems.terraPick) && event.getState().is(BlockTags.MINEABLE_WITH_SHOVEL)) {
            int efficiency = stack.getItem().getEnchantmentLevel(stack, Enchantments.BLOCK_EFFICIENCY);
            efficiency = efficiency == 0 ? 9 : (efficiency + 1) * 5; //numbers based on my ass
            event.setNewSpeed(event.getOriginalSpeed() * efficiency);
        }
    }

    @SubscribeEvent
    public void onCorporeaSparkUse(PlayerInteractEvent.RightClickBlock event) {
        BlockEntity be = event.getLevel().getBlockEntity(event.getHitVec().getBlockPos());
        if ((be instanceof BaseContainerBlockEntity || be instanceof SimpleInventoryBlockEntity)
                && (event.getItemStack().is(BotaniaItems.corporeaSpark) || event.getItemStack().is(BotaniaItems.corporeaSparkMaster))) {
            event.setUseBlock(Event.Result.DENY);
        }
    }

    @SubscribeEvent
    public void onUseTornado(PlayerInteractEvent.RightClickItem event) {
        Player player = event.getEntity();
        ItemStack itemstack = event.getItemStack();

        if (!player.isFallFlying() && player.getItemBySlot(EquipmentSlot.CHEST).canElytraFly(player)) {
            Level level = player.level();
            if (itemstack.getItem() instanceof SkiesRodItem tornado) {
                player.startFallFlying();
                player.jumpFromGround();

                if (!level.isClientSide) {
                    tornado.use(level, player, InteractionHand.MAIN_HAND);
                }
                event.setCanceled(true);
                event.setCancellationResult(level.isClientSide ? InteractionResult.SUCCESS : InteractionResult.CONSUME);
            }
        }
    }

    @SubscribeEvent
    public void onSpellCast(ChangeManaEvent event) {
        Player player = event.getEntity();
        ManaItemHandler handler = ManaItemHandler.instance();
        MagicData mData = event.getMagicData();
        SpellData sData = mData.getCastingSpell();
        float oldMana = event.getOldMana();
        float afterCastMana = oldMana - sData.getSpell().getManaCost(sData.getLevel());

        if (mData.isCasting()) {
            if (afterCastMana < 0) {
                event.setNewMana(0);
                event.setResult(Event.Result.DENY);
                return;
            }
            event.setNewMana(afterCastMana);
            event.setCanceled(false);
        } else
            event.setCanceled(true);
        if (handler.requestManaExact(BotaniaItems.alienAntenna.getDefaultInstance(), player, 100, true)) {
            event.setNewMana(oldMana + 10);
            event.setCanceled(false);
        } else
            event.setCanceled(true);
    }

}
