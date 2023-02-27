package com.leobeliik.mycustomtweaks.mixins;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.BushBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import vazkii.botania.api.block.HornHarvestable;
import vazkii.botania.common.block.flower.functional.BergamuteBlockEntity;
import vazkii.botania.common.item.BotaniaItems;
import vazkii.botania.common.item.HornItem;
import vazkii.botania.common.lib.BotaniaTags;
import vazkii.botania.xplat.XplatAbstractions;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Mixin(HornItem.class)
public class DrumMixin {

    @Inject(method = "Lvazkii/botania/common/item/HornItem;breakGrass(Lnet/minecraft/world/level/Level;Lnet/minecraft/world/item/ItemStack;Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/entity/LivingEntity;)V",
            at = @At("TAIL"), remap = false)
    private static void breakGrass(Level world, ItemStack stack, BlockPos srcPos, @Nullable LivingEntity user, CallbackInfo ci) {
        HornHarvestable.EnumHornType type = null;
        if (stack.is(BotaniaItems.grassHorn)) {
            type = HornHarvestable.EnumHornType.WILD;
        } else if (stack.is(BotaniaItems.leavesHorn)) {
            type = HornHarvestable.EnumHornType.CANOPY;
        } else if (stack.is(BotaniaItems.snowHorn)) {
            type = HornHarvestable.EnumHornType.COVERING;
        }

        int range = 12 - type.ordinal() * 3;
        int rangeY = 3 + type.ordinal() * 4;
        List<BlockPos> coords = new ArrayList<>();

        for (BlockPos pos : BlockPos.betweenClosed(srcPos.offset(-range, -rangeY, -range),
                srcPos.offset(range, rangeY, range))) {
            BlockState state = world.getBlockState(pos);
            Block block = state.getBlock();
            BlockEntity be = world.getBlockEntity(pos);
            HornHarvestable harvestable = XplatAbstractions.INSTANCE.findHornHarvestable(world, pos, state, be);

            if (BergamuteBlockEntity.isBergamuteNearby(world, pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5)) {
                continue;
            }

            if (harvestable != null
                    ? harvestable.canHornHarvest(world, pos, stack, type, user)
                    : type == HornHarvestable.EnumHornType.WILD && (block instanceof BushBlock || block == Blocks.MOSS_CARPET) && !state.is(BotaniaTags.Blocks.SPECIAL_FLOWERS)) {
                coords.add(pos.immutable());
            }
        }

        Collections.shuffle(coords);

        int count = Math.min(coords.size(), 32 + type.ordinal() * 16);
        for (int i = 0; i < count; i++) {
            BlockPos currCoords = coords.get(i);
            BlockState state = world.getBlockState(currCoords);
            BlockEntity be = world.getBlockEntity(currCoords);
            HornHarvestable harvestable = XplatAbstractions.INSTANCE.findHornHarvestable(world, currCoords, state, be);

            if (harvestable != null && harvestable.hasSpecialHornHarvest(world, currCoords, stack, type, user)) {
                harvestable.harvestByHorn(world, currCoords, stack, type, user);
            } else {
                world.destroyBlock(currCoords, true);
            }
        }
    }
}
