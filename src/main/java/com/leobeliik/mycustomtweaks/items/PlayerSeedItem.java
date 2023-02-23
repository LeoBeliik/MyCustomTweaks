package com.leobeliik.mycustomtweaks.items;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;
import vazkii.botania.client.fx.SparkleParticleData;
import vazkii.botania.common.handler.BotaniaSounds;
import vazkii.botania.common.item.WorldSeedItem;

public class PlayerSeedItem extends WorldSeedItem {
    public PlayerSeedItem(Properties builder) {
        super(builder);
    }

    @Override
    public @NotNull InteractionResultHolder<ItemStack> use(Level level, Player player, @NotNull InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        BlockPos coords = getSurface(level, player);
        if (coords == null) return new InteractionResultHolder<>(InteractionResult.PASS, stack);
        if (level.dimension() == Level.OVERWORLD) {
            if (!level.isClientSide) {
                player.setXRot(0.0F);
                player.setYRot(0.0F);
                player.teleportTo((double) coords.getX() + 0.5, (double) coords.getY() + 0.5, (double) coords.getZ() + 0.5);

                while (!level.noCollision(player, player.getBoundingBox())) {
                    player.teleportTo(player.getX(), player.getY() + 1.0, player.getZ());
                }

                level.playSound(null, player.getX(), player.getY(), player.getZ(), BotaniaSounds.worldSeedTeleport, SoundSource.PLAYERS, 1.0F, 1.0F);
                SparkleParticleData data = SparkleParticleData.sparkle(1.0F, 0.25F, 1.0F, 0.25F, 10);
                ((ServerLevel) level).sendParticles(data, player.getX(), player.getY() + (double) (player.getBbHeight() / 2.0F), player.getZ(), 50, (double) (player.getBbWidth() / 8.0F), (double) (player.getBbHeight() / 4.0F), (double) (player.getBbWidth() / 8.0F), 0.0);
                stack.shrink(1);
            }

            return InteractionResultHolder.sidedSuccess(stack, level.isClientSide());
        } else {
            return new InteractionResultHolder<>(InteractionResult.PASS, stack);
        }
    }

    private BlockPos getSurface(Level level, Player player) {
        if (!level.canSeeSky(player.blockPosition())) {
            for (int i = 0; i < 384; i++) {
                BlockPos pos = new BlockPos(player.getX(), player.getY() + i, player.getZ());
                if (level.canSeeSky(pos) && pos.getY() != player.getY()) {
                    return pos;
                }
            }
        }
        return null;
    }
}
