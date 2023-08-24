package com.leobeliik.mycustomtweaks;

import com.simibubi.create.AllSoundEvents;
import com.simibubi.create.content.decoration.placard.PlacardBlock;
import com.simibubi.create.content.decoration.placard.PlacardBlockEntity;
import lilypuree.decorative_blocks.blocks.SupportBlock;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraftforge.client.event.ScreenEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

import java.util.regex.Pattern;

import static com.simibubi.create.content.decoration.placard.PlacardBlock.updateNeighbours;

@Mod(MyCustomTweaks.MODID)
public class MyCustomTweaks {
    static final String MODID = "mycustomtweaks";
    //private static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, MODID);

    private static final Pattern TORCH_PATTERN = Pattern.compile("(?:(?:(?:[A-Z-_.:]|^)torch)|(?:(?:[a-z-_.:]|^)Torch))(?:[A-Z-_.:]|$)");
    private int poweredTicks;
    private PlacardBlockEntity tickBE;


    public MyCustomTweaks() {
        MinecraftForge.EVENT_BUS.register(this);
        IEventBus bus = FMLJavaModLoadingContext.get().getModEventBus();
    }

    @SubscribeEvent
    public void onItemUse(PlayerInteractEvent.RightClickBlock event) {
        if (event.getItemStack().is(Blocks.WITHER_ROSE.asItem()) && event.getEntity().isCrouching()) {
            event.setUseItem(Event.Result.DENY);
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
        if (block.getBlock() instanceof PlacardBlock pla) {
            pla.onBlockEntityUse(level, pos, pte -> {
                ItemStack inBlock = pte.getHeldItem();
                if (inBlock.isEmpty()) {
                    return null;
                } else {
                    if (player.isShiftKeyDown())
                        return InteractionResult.PASS;
                    if (level.isClientSide)
                        return InteractionResult.SUCCESS;

                    tickBE = pte;
                    AllSoundEvents.CONFIRM.play(level, null, pos, 1, 1);
                    level.setBlock(pos, block.setValue(BlockStateProperties.POWERED, true), 3);
                    updateNeighbours(block, level, pos);
                    this.poweredTicks = 19;
                    pte.notifyUpdate();
                    return InteractionResult.SUCCESS;
                }
            });
        }
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

    @SubscribeEvent
    public void onTick(TickEvent event) {
        if (tickBE != null) {
            Level level = tickBE.getLevel();
            BlockPos pos = tickBE.getBlockPos();
            if (level.isClientSide)
                return;
            if (poweredTicks == 0)
                return;

            poweredTicks--;
            if (poweredTicks > 0)
                return;

            BlockState blockState = tickBE.getBlockState();
            level.setBlock(pos, blockState.setValue(PlacardBlock.POWERED, false), 3);
            PlacardBlock.updateNeighbours(blockState, level, pos);
        }
    }


    @SubscribeEvent
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
    }
}
