package com.leobeliik.mycustomtweaks;

import blusunrize.immersiveengineering.common.blocks.wooden.WoodenCrateBlockEntity;
import com.mojang.blaze3d.platform.InputConstants;
import com.mojang.blaze3d.platform.Window;
import net.dries007.tfc.common.TFCEffects;
import net.dries007.tfc.common.blockentities.ThatchBedBlockEntity;
import net.dries007.tfc.common.capabilities.Capabilities;
import net.dries007.tfc.common.fluids.Alcohol;
import net.dries007.tfc.common.fluids.TFCFluids;
import net.dries007.tfc.common.items.FluidContainerItem;
import net.mehvahdjukaar.moonlight.api.map.type.MapDecorationType;
import net.mehvahdjukaar.moonlight.api.misc.DataObjectReference;
import net.mehvahdjukaar.moonlight.core.map.MapDataInternal;
import net.mehvahdjukaar.supplementaries.Supplementaries;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.OptionInstance;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.player.Input;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.Containers;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.entity.monster.Creeper;
import net.minecraft.world.entity.player.Player;
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
import net.minecraftforge.client.event.*;
import net.minecraftforge.client.gui.overlay.VanillaGuiOverlay;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.living.LivingEntityUseItemEvent;
import net.minecraftforge.event.entity.player.PlayerContainerEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.event.entity.player.PlayerSleepInBedEvent;
import net.minecraftforge.event.entity.player.PlayerWakeUpEvent;
import net.minecraftforge.event.level.BlockEvent;
import net.minecraftforge.event.level.ExplosionEvent;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.InterModComms;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.InterModEnqueueEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.RegistryObject;
import top.theillusivec4.curios.api.SlotTypeMessage;
import top.theillusivec4.curios.api.SlotTypePreset;

import static net.dries007.tfc.common.TFCEffects.EFFECTS;
import static net.dries007.tfc.common.TFCEffects.register;

@Mod(MyCustomTweaks.MODID)
public class MyCustomTweaks {
    public static final String MODID = "mycustomtweaks";
    public static final RegistryObject<MobEffect> INSOMNIA = register("insomnia", () -> new TFCEffects.TFCMobEffect(MobEffectCategory.BENEFICIAL, 0));
    private long time;
    private static final KeyMapping placementKey = placementKey();

    public MyCustomTweaks() {
        MinecraftForge.EVENT_BUS.register(this);
        IEventBus bus = FMLJavaModLoadingContext.get().getModEventBus();
        bus.addListener(this::onInterModComms);
        DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> bus.addListener(this::keyRegistry));
    }

    private void onInterModComms(InterModEnqueueEvent event) {
        InterModComms.sendTo("curios", SlotTypeMessage.REGISTER_TYPE, () -> SlotTypePreset.BACK.getMessageBuilder().build());
    }

    @OnlyIn(Dist.CLIENT)
    private static KeyMapping placementKey() {
        return new KeyMapping(
                String.valueOf("enable Additional Placements"),
                InputConstants.Type.KEYSYM,
                InputConstants.UNKNOWN.getValue(),
                "key.categories.misc");
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

    @SubscribeEvent(priority = EventPriority.LOW)
    public void onPlayerInteract(PlayerInteractEvent.RightClickBlock event) {
        Player player = event.getEntity();
        if (!event.getLevel().isClientSide || player.isDiscrete() || event.isCanceled() || event.getResult() == Event.Result.DENY || event.getUseBlock() == Event.Result.DENY)
            return;

        Level world = event.getLevel();
        BlockPos pos = event.getPos();
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
        event.register(placementKey);
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

        //additional placements
        if (placementKey.isDown()) {
            shouldAP = !shouldAP;
            addPlayerTag(player, shouldAP);
        }

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
}
