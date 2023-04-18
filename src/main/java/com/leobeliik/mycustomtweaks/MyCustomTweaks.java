package com.leobeliik.mycustomtweaks;

import com.leobeliik.mycustomtweaks.items.PlayerSeedItem;
import com.simibubi.create.content.contraptions.components.deployer.DeployerBlock;
import com.simibubi.create.content.contraptions.components.deployer.DeployerFakePlayer;
import com.simibubi.create.content.contraptions.components.deployer.DeployerTileEntity;
import com.simibubi.create.content.contraptions.components.saw.SawBlock;
import com.simibubi.create.content.contraptions.wrench.WrenchItem;
import com.simibubi.create.content.logistics.trains.management.edgePoint.station.StationTileEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.monster.Creeper;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.AirItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BaseContainerBlockEntity;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.event.level.BlockEvent;
import net.minecraftforge.event.level.ExplosionEvent;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.loading.FMLLoader;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import vazkii.botania.common.block.block_entity.SimpleInventoryBlockEntity;
import vazkii.botania.common.item.BotaniaItems;
import vazkii.botania.common.item.WandOfTheForestItem;
import vazkii.botania.common.item.rod.SkiesRodItem;

import java.util.Arrays;
import java.util.regex.Pattern;

@Mod(MyCustomTweaks.MODID)
public class MyCustomTweaks {
    static final String MODID = "mycustomtweaks";
    private static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, MODID);

    private static final Pattern TORCH_PATTERN = Pattern.compile("(?:(?:(?:[A-Z-_.:]|^)torch)|(?:(?:[a-z-_.:]|^)Torch))(?:[A-Z-_.:]|$)");

    //public static boolean isTetra = false;

    public MyCustomTweaks() {
        MinecraftForge.EVENT_BUS.register(this);
        IEventBus bus = FMLJavaModLoadingContext.get().getModEventBus();
        Registry();
        //isTetra = FMLLoader.getLoadingModList().getModFileById("tetra") != null;
    }

    private void Registry() {
        ITEMS.register(FMLJavaModLoadingContext.get().getModEventBus());
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
    public void onCreeperExplode(ExplosionEvent.Start event) {
        if (event.getExplosion().getExploder() instanceof Creeper creeper) {
            event.setCanceled(true);
            float f = creeper.isPowered() ? 2.0F : 1.0F;
            event.getLevel().explode(null, creeper.getX(), creeper.getY(), creeper.getZ(), 3 * f, false, Explosion.BlockInteraction.BREAK);
        }
    }

    @SubscribeEvent
    public void onItemUse(PlayerInteractEvent.RightClickBlock event) {
        if (event.getItemStack().is(Blocks.WITHER_ROSE.asItem()) && event.getEntity().isCrouching()) {
            event.setUseItem(Event.Result.DENY);
        }
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
            Level level = player.level;
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
    public void tetraPlaceTorch(PlayerInteractEvent.RightClickBlock event) {
        Player player = event.getEntity();
        BlockState block = event.getLevel().getBlockState(event.getPos());
        Item item = event.getItemStack().getItem();
        /*if (isTetra && event.getItemStack().getShareTag() != null) {
            UseOnContext ctx = new UseOnContext(player, event.getHand(), event.getHitVec());
            String tags = event.getItemStack().getShareTag().toString();
            if (tags.contains("_axe_") && block.getBlock() instanceof SupportBlock) { //tetra axe on decorative blocks
                if (!ctx.getLevel().isClientSide) {
                    SupportBlock.onSupportActivation(block, event.getLevel(), event.getPos(), player, ctx.getClickLocation());
                } else {
                    player.swing(event.getHand());
                }
            } else if (tags.contains("_pickaxe_")) {
                if (!player.isCrouching() && (block.getMenuProvider(event.getLevel(), event.getPos()) != null || block.hasBlockEntity())) {
                    return;
                }
                for (int i = 0; i < player.getInventory().getContainerSize(); ++i) {
                    ItemStack stackAt = player.getInventory().getItem(i);
                    if (!stackAt.isEmpty() && TORCH_PATTERN.matcher(stackAt.getItem().getDescriptionId()).find()) {
                        ItemStack displayStack = stackAt.copy();
                        InteractionResult did = PlayerHelper.substituteUse(ctx, stackAt);
                        if (did.consumesAction()) {
                            if (!ctx.getLevel().isClientSide) {
                                ItemsRemainingRenderHandler.send(player, displayStack, TORCH_PATTERN);
                            }
                            player.swing(event.getHand());
                            player.getCooldowns().addCooldown(item, 5);
                        }
                    }
                }
            }
        }*/
        if ((block.getBlock() instanceof DeployerBlock || block.getBlock() instanceof SawBlock)
                && ((item instanceof WrenchItem && !player.isCrouching()) || item instanceof WandOfTheForestItem)) {
            event.setCanceled(true);
        }
        Level level = event.getLevel();
        BlockPos pos = event.getPos();

        if (level.getBlockEntity(pos) instanceof DeployerTileEntity dep && level.getBlockEntity(pos.below(2)) instanceof StationTileEntity
            && item instanceof AirItem && dep.getPlayer() != null && dep.getPlayer().getMainHandItem().getItem() instanceof WrenchItem) {
            dep.changeMode(); //set to punch for the steam n rails station thing
        }
    }

    @SubscribeEvent
    public void noDeployPlacement(BlockEvent.EntityPlaceEvent event) {
        if (event.getEntity() instanceof DeployerFakePlayer) {
            event.setCanceled(true);
        }
    }

    /*@SubscribeEvent
    public void tomPlzRclick(ScreenEvent.MouseButtonPressed event) {
        Screen screen = event.getScreen();
        if (screen.toString().contains("TerminalScreen")) {
            for (GuiEventListener child : screen.children()) {
                if (child instanceof EditBox) {
                    ((EditBox) child).setFocus(child.isMouseOver(event.getMouseX(), event.getMouseY()));
                    break;
                }
            }
        }
    }*/

    public static final RegistryObject<Item> PLAYER_SEED_ITEM = ITEMS.register("player_seed", () ->
            new PlayerSeedItem(new Item.Properties().stacksTo(8)));
}
