package com.leobeliik.mycustomtweaks.mixin;

import de.dafuqs.spectrum.compat.claims.GenericClaimModsCompat;
import de.dafuqs.spectrum.items.trinkets.RadiancePinItem;
import de.dafuqs.spectrum.items.trinkets.SpectrumTrinketItem;
import de.dafuqs.spectrum.registries.SpectrumBlocks;
import de.dafuqs.spectrum.registries.SpectrumSoundEvents;
import dev.emi.trinkets.api.SlotReference;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.LightBlock;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import static de.dafuqs.spectrum.items.trinkets.RadiancePinItem.*;

@Mixin(RadiancePinItem.class)
public class radiantPinMixin extends SpectrumTrinketItem {

    public radiantPinMixin(Settings settings, Identifier unlockIdentifier) {
        super(settings, unlockIdentifier);
    }

    @Overwrite(remap = false)
    public void tick(ItemStack stack, SlotReference slot, LivingEntity entity) {
        super.tick(stack, slot, entity);
        World world = entity.getWorld();
        if (!world.isClient && world.getTime() % 20L == 0L) {
            if (entity instanceof PlayerEntity) {
                PlayerEntity playerEntity = (PlayerEntity) entity;
                if (playerEntity.isSpectator()) {
                    return;
                }
            }

            BlockPos pos = entity.getBlockPos();
            if (!GenericClaimModsCompat.canPlaceBlock(world, pos, entity)) {
                return;
            }

            if (!world.isOutOfHeightLimit(pos) && world.getLightLevel(pos) <= 7 && !world.isSkyVisible(pos)) {
                BlockState currentState = world.getBlockState(pos);
                boolean placed = false;
                if (currentState.isAir()) {
                    world.setBlockState(pos, LIGHT_BLOCK_STATE, 3);
                    placed = true;
                } else if (currentState.equals(Blocks.WATER.getDefaultState())) {
                    world.setBlockState(pos, LIGHT_BLOCK_STATE_WATER, 3);
                    placed = true;
                } else if (currentState.isOf(SpectrumBlocks.DECAYING_LIGHT_BLOCK)) {
                    if ((Boolean) currentState.get(LightBlock.WATERLOGGED)) {
                        world.setBlockState(pos, LIGHT_BLOCK_STATE_WATER, 3);
                    } else {
                        world.setBlockState(pos, LIGHT_BLOCK_STATE, 3);
                    }

                    placed = true;
                }

                if (placed) {
                    sendSmallLightCreatedParticle((ServerWorld) world, pos);
                    world.playSound((PlayerEntity) null, entity.getX() + 0.5, entity.getY() + 0.5, entity.getZ() + 0.5, SpectrumSoundEvents.RADIANCE_STAFF_PLACE, SoundCategory.PLAYERS, 0.08F, 0.9F + world.random.nextFloat() * 0.2F);
                }
            }
        }

    }
}
