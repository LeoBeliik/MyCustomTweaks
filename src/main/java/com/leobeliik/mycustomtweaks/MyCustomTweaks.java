package com.leobeliik.mycustomtweaks;

import com.mojang.blaze3d.platform.InputConstants;
import com.mojang.blaze3d.platform.Window;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.OptionInstance;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.player.Input;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.entity.monster.Creeper;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.LadderBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.InputEvent;
import net.minecraftforge.client.event.MovementInputUpdateEvent;
import net.minecraftforge.client.event.RegisterKeyMappingsEvent;
import net.minecraftforge.client.event.RenderGuiOverlayEvent;
import net.minecraftforge.client.gui.overlay.VanillaGuiOverlay;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.event.level.ExplosionEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

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
            event.getLevel().explode(null, creeper.getX(), creeper.getY(), creeper.getZ(), 3f * f, false, Level.ExplosionInteraction.TNT);
        }
    }

    //stolen from quark --------------------------------------------------------------------------------------------------------->

    //ladders
    private static boolean canAttachTo(BlockState state, Block ladder, LevelReader world, BlockPos pos, Direction facing) {
        if (ladder instanceof LadderBlock) {
            return canLadderSurvive(state, world, pos);
        }

        return false;
    }

    public static boolean canLadderSurvive(BlockState state, LevelReader world, BlockPos pos) {
        Direction facing = state.getValue(LadderBlock.FACING);
        Direction opposite = facing.getOpposite();
        BlockPos oppositePos = pos.relative(opposite);
        BlockState oppositeState = world.getBlockState(oppositePos);

        boolean solid = facing.getAxis() != Direction.Axis.Y && oppositeState.isFaceSturdy(world, oppositePos, facing) && !(oppositeState.getBlock() instanceof LadderBlock);
        BlockState topState = world.getBlockState(pos.above());
        return solid || (topState.getBlock() instanceof LadderBlock && (facing.getAxis() == Direction.Axis.Y || topState.getValue(LadderBlock.FACING) == facing));
    }

    public static boolean updateLadder(BlockState state, Direction facing, BlockState facingState, LevelAccessor world, BlockPos currentPos, BlockPos facingPos) {
        return canLadderSurvive(state, world, currentPos);
    }

    @SubscribeEvent
    public void onInteract(PlayerInteractEvent.RightClickBlock event) {
        Player player = event.getEntity();
        InteractionHand hand = event.getHand();
        ItemStack stack = player.getItemInHand(hand);

        if (!stack.isEmpty() && stack.is(Items.LADDER)) {
            Block block = Block.byItem(stack.getItem());
            Level world = event.getLevel();
            BlockPos pos = event.getPos();
            while (world.getBlockState(pos).getBlock() == block) {
                event.setCanceled(true);
                BlockPos posDown = pos.below();

                if (world.isOutsideBuildHeight(posDown))
                    break;

                BlockState stateDown = world.getBlockState(posDown);

                if (stateDown.getBlock() == block)
                    pos = posDown;
                else {
                    boolean water = stateDown.getBlock() == Blocks.WATER;
                    if (water || stateDown.isAir()) {
                        BlockState copyState = world.getBlockState(pos);

                        Direction facing = copyState.getValue(LadderBlock.FACING);
                        if (canAttachTo(copyState, block, world, posDown, facing.getOpposite())) {
                            world.setBlockAndUpdate(posDown, copyState.setValue(BlockStateProperties.WATERLOGGED, water));
                            world.playSound(null, posDown.getX(), posDown.getY(), posDown.getZ(), SoundEvents.LADDER_PLACE, SoundSource.BLOCKS, 1F, 1F);

                            if (!player.getAbilities().instabuild) {
                                stack.shrink(1);

                                if (stack.getCount() <= 0)
                                    player.setItemInHand(hand, ItemStack.EMPTY);
                            }

                            event.setCancellationResult(InteractionResult.sidedSuccess(world.isClientSide));
                        }
                    }
                    break;
                }
            }
        }
    }

    @SubscribeEvent
    public void onPlayerTick(TickEvent.PlayerTickEvent event) {
        if (event.phase == TickEvent.Phase.START) {
            Player player = event.player;
            if (player.onClimbable() && player.level().isClientSide) {
                BlockPos playerPos = player.blockPosition();
                BlockPos downPos = playerPos.below();

                boolean scaffold = player.level().getBlockState(playerPos).isScaffolding(player);
                if (player.isCrouching() == scaffold &&
                        player.zza == 0 &&
                        player.yya <= 0 &&
                        player.xxa == 0 &&
                        player.getXRot() > 70 &&
                        !player.getAbilities().flying &&
                        player.level().getBlockState(downPos).isLadder(player.level(), downPos, player)) {

                    Vec3 move = new Vec3(0, -0.5, 0);
                    AABB target = player.getBoundingBox().move(move);

                    Iterable<VoxelShape> collisions = player.level().getBlockCollisions(player, target);
                    if (!collisions.iterator().hasNext()) {
                        player.setBoundingBox(target);
                        player.move(MoverType.SELF, move);
                    }
                }
            }
        }
    }

    @SubscribeEvent
    @OnlyIn(Dist.CLIENT)
    public void onInput1(MovementInputUpdateEvent event) {
        Player player = event.getEntity();
        if (player.onClimbable() && !player.getAbilities().flying &&
                !player.level().getBlockState(player.blockPosition()).isScaffolding(player)
                && Minecraft.getInstance().screen != null && !(player.zza == 0 && player.getXRot() > 70) && !player.onGround()) {
            Input input = event.getInput();
            if (input != null)
                input.shiftKeyDown = true; // sneaking
        }
    }

    //auto walking


    private boolean autorunning;
    private boolean hadAutoJump;
    private boolean shouldAccept;
    private static final KeyMapping autorunKey = autorunKey();

    @OnlyIn(Dist.CLIENT)
    private void keyRegistry(final RegisterKeyMappingsEvent event) {
        event.register(autorunKey);
    }

    @OnlyIn(Dist.CLIENT)
    private static KeyMapping autorunKey() {
        return new KeyMapping(
                String.valueOf("Auto run key"),
                InputConstants.Type.KEYSYM,
                InputConstants.UNKNOWN.getValue(),
                "key.categories.misc");
    }

    @SubscribeEvent
    @OnlyIn(Dist.CLIENT)
    public void onMouseInput(InputEvent.MouseButton event) {
        acceptInput();
    }

    @SubscribeEvent
    @OnlyIn(Dist.CLIENT)
    public void onKeyInput(InputEvent.Key event) {
        acceptInput();
    }

    @SubscribeEvent
    @OnlyIn(Dist.CLIENT)
    public void drawHUD(RenderGuiOverlayEvent.Post event) {
        if (autorunning && event.getOverlay() == VanillaGuiOverlay.HOTBAR.type()) {
            String message = "AUTOWALKING";

            Minecraft mc = Minecraft.getInstance();
            int w = mc.font.width("OoO" + message + "oOo");

            Window window = event.getWindow();
            int x = (window.getGuiScaledWidth() - w) / 2;
            int y = 10;

            String displayMessage = message;
            int dots = (ClientTicker.ticksInGame / 10) % 2;
            switch (dots) {
                case 0 -> displayMessage = "OoO " + message + " oOo";
                case 1 -> displayMessage = "oOo " + message + " OoO";
            }

            event.getGuiGraphics().drawString(mc.font, displayMessage, x, y, 0xFFFFFFFF);
        }
    }

    private boolean shouldAP = false;
    public static String APtag = "additional_placement";

    @OnlyIn(Dist.CLIENT)
    private void acceptInput() {
        Minecraft mc = Minecraft.getInstance();
        Player player = mc.player;
        if (player == null) return;

        OptionInstance<Boolean> opt = mc.options.autoJump();
        if (mc.options.keyUp.isDown()) {
            if (autorunning)
                opt.set(hadAutoJump);

            autorunning = false;
        } else {
            if (autorunKey.isDown()) {
                if (shouldAccept) {
                    shouldAccept = false;
                    float height = player.getStepHeight();

                    autorunning = !autorunning;

                    if (autorunning) {
                        hadAutoJump = opt.get();

                        if (height < 1)
                            opt.set(true);
                    } else opt.set(hadAutoJump);
                }
            } else shouldAccept = true;
        }
    }

    private static void addPlayerTag(Player player, boolean tag) {
        if (tag) {
            player.addTag(APtag);
            player.displayClientMessage(Component.nullToEmpty("Enabled Additional Placements"), true);
        } else {
            player.removeTag(APtag);
            player.displayClientMessage(Component.nullToEmpty("Disabled Additional Placements"), true);
        }
    }

    @SubscribeEvent
    @OnlyIn(Dist.CLIENT)
    public void onInput(MovementInputUpdateEvent event) {
        Minecraft mc = Minecraft.getInstance();
        if (mc.player != null && autorunning) {
            Input input = event.getInput();
            input.up = true;
            input.forwardImpulse = ((LocalPlayer) event.getEntity()).isMovingSlowly() ? 0.3F : 1F;
        }
    }

    @Mod.EventBusSubscriber(value = Dist.CLIENT)
    public final class ClientTicker {

        static int ticksInGame = 0;
        static float partialTicks = 0;
        static float delta = 0;
        static float total = 0;

        @OnlyIn(Dist.CLIENT)
        private static void calcDelta() {
            float oldTotal = total;
            total = ticksInGame + partialTicks;
            delta = total - oldTotal;
        }

        @SubscribeEvent
        @OnlyIn(Dist.CLIENT)
        public static void renderTick(TickEvent.RenderTickEvent event) {
            if (event.phase == TickEvent.Phase.START)
                partialTicks = event.renderTickTime;
            else calcDelta();
        }

        @SubscribeEvent
        @OnlyIn(Dist.CLIENT)
        public static void clientTickEnd(TickEvent.ClientTickEvent event) {
            if (event.phase == TickEvent.Phase.END) {
                Screen gui = Minecraft.getInstance().screen;
                if (gui == null || !gui.isPauseScreen()) {
                    ticksInGame++;
                    partialTicks = 0;
                }

                calcDelta();
            }
        }
    }

        public static void printMe(Object o) {
            System.out.println(o);
        }
    }
