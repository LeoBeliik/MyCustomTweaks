package com.leobeliik.mycustomtweaks;

import blusunrize.immersiveengineering.common.blocks.wooden.WoodenCrateBlockEntity;
import com.leobeliik.mycustomtweaks.items.PlayerSeedItem;
import lilypuree.decorative_blocks.blocks.SupportBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.world.Containers;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.monster.Creeper;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BaseContainerBlockEntity;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.event.world.ExplosionEvent;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import slimeknights.tconstruct.tools.TinkerTools;
import vazkii.arl.block.be.SimpleInventoryBlockEntity;
import vazkii.botania.client.gui.ItemsRemainingRenderHandler;
import vazkii.botania.common.helper.PlayerHelper;
import vazkii.botania.common.item.ModItems;
import vazkii.botania.common.item.rod.ItemTornadoRod;

import java.util.regex.Pattern;

@Mod(MyCustomTweaks.MODID)
public class MyCustomTweaks {
    static final String MODID = "mycustomtweaks";
    private static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, MODID);

    private static final Pattern TORCH_PATTERN = Pattern.compile("(?:(?:(?:[A-Z-_.:]|^)torch)|(?:(?:[a-z-_.:]|^)Torch))(?:[A-Z-_.:]|$)");


    public MyCustomTweaks() {
        MinecraftForge.EVENT_BUS.register(this);
        IEventBus bus = FMLJavaModLoadingContext.get().getModEventBus();
        Registry();
    }

    private void Registry() {
        ITEMS.register(FMLJavaModLoadingContext.get().getModEventBus());
    }

    @SubscribeEvent
    public void onCreeperExplode(ExplosionEvent.Start event) {
        if (event.getExplosion().getExploder() instanceof Creeper creeper) {
            event.setCanceled(true);
            float f = creeper.isPowered() ? 2.0F : 1.0F;
            event.getWorld().explode(null, creeper.getX(), creeper.getY(), creeper.getZ(), 3 * f, false, Explosion.BlockInteraction.BREAK);
        }
    }

    @SubscribeEvent
    public void onItemUse(PlayerInteractEvent.RightClickBlock event) {
        if (event.getItemStack().is(Blocks.WITHER_ROSE.asItem()) && event.getEntity().isCrouching()) {
            event.setUseItem(Event.Result.DENY);
        }

        BlockEntity be = event.getWorld().getBlockEntity(event.getHitVec().getBlockPos());
        if ((be instanceof BaseContainerBlockEntity || be instanceof SimpleInventoryBlockEntity)
                && (event.getItemStack().is(ModItems.corporeaSpark) || event.getItemStack().is(ModItems.corporeaSparkMaster))) {
            event.setUseBlock(Event.Result.DENY);
        }
    }

    @SubscribeEvent
    public void onUseTornado(PlayerInteractEvent.RightClickItem event) {
        Player player = (Player) event.getEntity();
        ItemStack itemstack = event.getItemStack();

        if (!player.isFallFlying() && player.getItemBySlot(EquipmentSlot.CHEST).canElytraFly(player)) {
            Level level = player.level;
            if (itemstack.getItem() instanceof ItemTornadoRod tornado) {
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
    public void onBlockBreak(BlockEvent.BreakEvent event) {
        Level level = event.getPlayer().level;
        BlockPos pos = event.getPos();
        if (level.getBlockEntity(pos) instanceof WoodenCrateBlockEntity crate) {
            Containers.dropContents(level, pos, crate);
        }
    }

    @SubscribeEvent
    public void onRClickBlock(PlayerInteractEvent.RightClickBlock event) {
        Player player = (Player) event.getEntity();
        Level level = event.getWorld();
        BlockState block = level.getBlockState(event.getPos());
        ItemStack stack = event.getItemStack();
        UseOnContext ctx = new UseOnContext(player, event.getHand(), event.getHitVec());
        boolean isTICAxe = stack.is(TinkerTools.mattock.asItem()) || stack.is(TinkerTools.handAxe.asItem()) || stack.is(TinkerTools.broadAxe.asItem());

        if (isTICAxe && block.getBlock() instanceof SupportBlock) { //tinker axe on decorative blocks
            if (!ctx.getLevel().isClientSide) {
                SupportBlock.onSupportActivation(block, level, event.getPos(), player, ctx.getClickLocation());
            } else {
                player.swing(event.getHand());
            }
        }
    }

    public static final RegistryObject<Item> PLAYER_SEED_ITEM = ITEMS.register("player_seed", () ->
            new PlayerSeedItem(new Item.Properties().tab(CreativeModeTab.TAB_MISC).stacksTo(8)));
}
