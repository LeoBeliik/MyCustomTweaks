package com.leobeliik.mycustomtweaks;

import blusunrize.immersiveengineering.common.blocks.wooden.WoodenCrateBlockEntity;
import lilypuree.decorative_blocks.blocks.SupportBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.world.Containers;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.monster.Creeper;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.event.level.BlockEvent;
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
        //creeper drop all blocks when explode
        if (event.getExplosion().getExploder() instanceof Creeper creeper) {
            event.setCanceled(true);
            float f = creeper.isPowered() ? 2.0F : 1.0F;
            event.getLevel().explode(null, creeper.getX(), creeper.getY(), creeper.getZ(), 3 * f, false, Explosion.BlockInteraction.BREAK);
        }
    }

    @SubscribeEvent
    public void onItemUse(PlayerInteractEvent.RightClickBlock event) {
        //don't place the rose when used for the spawn dirt thing
        if (event.getItemStack().is(Blocks.WITHER_ROSE.asItem()) && event.getEntity().isCrouching()) {
            event.setUseItem(Event.Result.DENY);
        }
    }

    @SubscribeEvent
    public void OnBreakEvent(BlockEvent.BreakEvent event) {
        //IE crates aren't shulker boxes smh
        Level level = event.getPlayer().getLevel();
        BlockPos pos = event.getPos();
        if (level.getBlockEntity(pos) instanceof WoodenCrateBlockEntity crate) {
            Containers.dropContents(level, pos, crate);
        }
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public void onBlockRclick(PlayerInteractEvent.RightClickBlock event) {
        Player player = event.getEntity();
        Level level = event.getLevel();
        BlockPos pos = event.getPos();
        BlockState block = level.getBlockState(pos);
        Item item = event.getItemStack().getItem();
        InteractionHand hand = event.getHand();
        if (event.getItemStack().getShareTag() != null) {
            UseOnContext ctx = new UseOnContext(player, hand, event.getHitVec());
            String tags = event.getItemStack().getShareTag().toString();
            if ((tags.contains("axe") || tags.contains("adze")) && block.getBlock() instanceof SupportBlock) {
                //tetra axe on decorative blocks
                if (!ctx.getLevel().isClientSide) {
                    SupportBlock.onSupportActivation(block, level, pos, player, ctx.getClickLocation());
                } else {
                    player.swing(hand);
                }
            }
        }
    }

    public static void printMe(Object o) {
        System.out.println(o);
    }

}
