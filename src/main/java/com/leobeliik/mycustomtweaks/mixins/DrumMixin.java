package com.leobeliik.mycustomtweaks.mixins;

import net.minecraft.core.BlockPos;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.BushBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import vazkii.botania.api.block.IHornHarvestable;
import vazkii.botania.api.block.IHornHarvestable.EnumHornType;
import vazkii.botania.common.block.subtile.functional.SubTileBergamute;
import vazkii.botania.common.item.ItemHorn;
import vazkii.botania.common.item.ModItems;
import vazkii.botania.common.lib.ModTags;
import vazkii.botania.xplat.IXplatAbstractions;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Mixin(ItemHorn.class)
public class DrumMixin {

    @Inject(method = "Lvazkii/botania/common/item/ItemHorn;breakGrass(Lnet/minecraft/world/level/Level;Lnet/minecraft/world/item/ItemStack;Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/entity/LivingEntity;)V",
            at = @At("TAIL"), remap = false)
    private static void breakGrass(Level world, ItemStack stack, BlockPos srcPos, @Nullable LivingEntity user, CallbackInfo ci) {
        EnumHornType type = null;
        if (stack.is(ModItems.grassHorn)) {
            type = EnumHornType.WILD;
        } else if (stack.is(ModItems.leavesHorn)) {
            type = EnumHornType.CANOPY;
        } else if (stack.is(ModItems.snowHorn)) {
            type = EnumHornType.COVERING;
        }

        int range = 12 - type.ordinal() * 3;
        int rangeY = 3 + type.ordinal() * 4;
        List<BlockPos> coords = new ArrayList<>();

        for (BlockPos pos : BlockPos.betweenClosed(srcPos.m_142082_(-range, -rangeY, -range), srcPos.m_142082_(range, rangeY, range))) {
            BlockState state = world.getBlockState(pos);
            Block block = state.getBlock();
            BlockEntity be = world.getBlockEntity(pos);
            IHornHarvestable harvestable = IXplatAbstractions.INSTANCE.findHornHarvestable(world, pos, state, be);

            if (SubTileBergamute.isBergamuteNearby(world, pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5)) {
                continue;
            }
            if (harvestable != null
                    ? harvestable.canHornHarvest(world, pos, stack, type, user)
                    : type == EnumHornType.WILD && (block instanceof BushBlock || block.equals(Blocks.MOSS_CARPET)) && !state.is(ModTags.Blocks.SPECIAL_FLOWERS)
                    || type == EnumHornType.CANOPY && state.is(BlockTags.LEAVES)
                    || type == EnumHornType.COVERING && state.is(Blocks.SNOW)) {
                coords.add(pos.immutable());
            }
        }

        Collections.shuffle(coords, world.random);

        int count = Math.min(coords.size(), 32 + type.ordinal() * 16);
        for (int i = 0; i < count; i++) {
            BlockPos currCoords = coords.get(i);
            BlockState state = world.getBlockState(currCoords);
            BlockEntity be = world.getBlockEntity(currCoords);
            IHornHarvestable harvestable = IXplatAbstractions.INSTANCE.findHornHarvestable(world, currCoords, state, be);

            if (harvestable != null && harvestable.hasSpecialHornHarvest(world, currCoords, stack, type, user)) {
                harvestable.harvestByHorn(world, currCoords, stack, type, user);
            } else {
                world.destroyBlock(currCoords, true);
            }
        }
    }
}
