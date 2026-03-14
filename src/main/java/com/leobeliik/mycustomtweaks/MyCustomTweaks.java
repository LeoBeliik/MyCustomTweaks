package com.leobeliik.mycustomtweaks;

import blusunrize.immersiveengineering.common.blocks.wooden.WoodenCrateBlockEntity;
import net.dries007.tfc.client.ClientHelpers;
import net.dries007.tfc.common.blockentities.ThatchBedBlockEntity;
import net.dries007.tfc.common.blocks.soil.HoeOverlayBlock;
import net.dries007.tfc.common.effect.TFCEffects;
import net.dries007.tfc.common.entities.livestock.horse.TFCDonkey;
import net.dries007.tfc.common.entities.livestock.horse.TFCHorse;
import net.dries007.tfc.config.TFCConfig;
import net.dries007.tfc.util.Helpers;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.Containers;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.monster.Creeper;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.client.event.RenderGuiLayerEvent;
import net.neoforged.neoforge.client.gui.VanillaGuiLayers;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.entity.EntityJoinLevelEvent;
import net.neoforged.neoforge.event.entity.player.PlayerWakeUpEvent;
import net.neoforged.neoforge.event.level.BlockEvent;
import net.neoforged.neoforge.event.level.ExplosionEvent;

import java.util.*;

@Mod(MyCustomTweaks.MODID)
public class MyCustomTweaks {
    static final String MODID = "mycustomtweaks";

    public MyCustomTweaks() {
        NeoForge.EVENT_BUS.register(this);
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
        if (event.getExplosion().getIndirectSourceEntity() instanceof Creeper creeper) {
            event.setCanceled(true);
            float f = creeper.isPowered() ? 2.0F : 1.0F;
            event.getLevel().explode(null, creeper.getX(), creeper.getY(), creeper.getZ(), 3f * f, false, Level.ExplosionInteraction.TNT);
        }
    }

    @SubscribeEvent
    public void onPlayerWakingUp(PlayerWakeUpEvent event) {
        Player player = event.getEntity();
        Level level = event.getEntity().level();
        String suffer = "";

        //Look at this boy, full of soup
        if (level.getBlockEntity(player.getOnPos()) instanceof ThatchBedBlockEntity bed) {
            if (new Random().nextInt(3) == 0) {
                suffer = "UGH I need a new bed..";
                player.addEffect(new MobEffectInstance(MobEffects.CONFUSION, 200, 0, false, false, false));
                player.addEffect(new MobEffectInstance(MobEffects.DIG_SLOWDOWN, 6000, 0, false, false, false));
                player.addEffect(new MobEffectInstance(MobEffects.WEAKNESS, 6000, 0, false, false, false));
                if (new Random().nextInt(4) == 0) {
                    suffer = "HOLY FUCK.. this shit broke and my back is killing me.. where the sheeps!!";
                    player.addEffect(new MobEffectInstance(MobEffects.UNLUCK, 12000, 0, false, false, false));
                    player.addEffect(new MobEffectInstance(TFCEffects.OVERBURDENED.holder(), 120, 0, false, false, false));
                    player.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 600, 3, true, true, false));
                }
            }
        }
        if (!suffer.equals(""))
            player.displayClientMessage(Component.literal(suffer), true);
    }

    @SubscribeEvent
    public void onGuiRender(RenderGuiLayerEvent.Pre event) {
        final ResourceLocation id = event.getName();
        if ((id.equals(VanillaGuiLayers.EXPERIENCE_BAR) || id.equals(VanillaGuiLayers.EXPERIENCE_LEVEL))) {
            event.setCanceled(true);
        }
    }

    @SubscribeEvent
    public void onRenderGameOverlayPost(RenderGuiLayerEvent.Post event) {
        final GuiGraphics graphics = event.getGuiGraphics();
        final Minecraft minecraft = Minecraft.getInstance();
        final Player player = minecraft.player;
        if (player != null) {
            final boolean holdingHoe = Helpers.isItem(player.getMainHandItem().getItem(), ItemTags.HOES) || Helpers.isItem(player.getOffhandItem().getItem(), ItemTags.HOES);
            if (event.getName() == VanillaGuiLayers.CROSSHAIR && holdingHoe) {
                if (!TFCConfig.CLIENT.showHoeOverlaysOnlyWhenShifting.get() || !player.isShiftKeyDown()) {
                    render(minecraft, graphics);
                }

            }
        }
    }

    @SubscribeEvent
    public void onDonkeySpawn(EntityJoinLevelEvent event) {
        if (event.loadedFromDisk()) return;

        Random r = new Random();
        if (event.getEntity() instanceof TFCDonkey donkey) {
            AttributeInstance speed = donkey.getAttribute(Attributes.MOVEMENT_SPEED);
            if (speed != null) {
                speed.setBaseValue(r.nextDouble(0.2000, 0.3170));
            }
        } else if (event.getEntity() instanceof TFCHorse horse) {
            AttributeInstance speed = horse.getAttribute(Attributes.MOVEMENT_SPEED);
            if (speed != null) {
                speed.setBaseValue(r.nextDouble(0.2300, 0.3376));
            }
        }
    }

    private static void render(Minecraft minecraft, GuiGraphics graphics) {
        Level world = minecraft.level;
        BlockPos targetedPos = ClientHelpers.getTargetedPos();
        if (world != null && targetedPos != null) {
            BlockState targetedState = world.getBlockState(targetedPos);
            Block var6 = targetedState.getBlock();
            if (var6 instanceof HoeOverlayBlock && Helpers.isBlock(targetedState.getBlock(), BlockTags.CROPS)) {
                HoeOverlayBlock overlayBlock = (HoeOverlayBlock) var6;
                List<Component> lines = new ArrayList<>();
                Objects.requireNonNull(lines);
                overlayBlock.addHoeOverlayInfo(world, targetedPos, targetedState, lines::add, TFCConfig.CLIENT.enableDebug.get());
                if (!lines.isEmpty()) {
                    int x = graphics.guiWidth() / 2 + 3;
                    int y = graphics.guiHeight() / 2 + 8;

                    if (lines.stream().filter(l -> l.getString().toLowerCase(Locale.ROOT).contains("good")).count() == 2) {
                        drawCenteredText(minecraft, graphics, Component.literal("Good"), x, y, 0xff00ff00);
                    } else {
                        drawCenteredText(minecraft, graphics, Component.literal("Bad"), x, y, 0xffdd0303);
                    }
                }

            }
        }

    }

    private static void drawCenteredText(Minecraft minecraft, GuiGraphics graphics, Component text, int x, int y, int color) {
        int textWidth = minecraft.font.width(text) / 2;
        graphics.drawString(minecraft.font, text, x - textWidth, y, color, true);
    }
}
