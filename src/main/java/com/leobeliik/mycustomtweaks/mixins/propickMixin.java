package com.leobeliik.mycustomtweaks.mixins;

import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import net.dries007.tfc.common.items.PropickItem;
import net.dries007.tfc.util.Helpers;
import net.minecraft.core.BlockPos;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import static net.dries007.tfc.common.items.PropickItem.getRepresentative;

@Mixin(PropickItem.class)
public abstract class propickMixin {

    @Overwrite
    public static Object2IntMap<Block> scanAreaFor(Level level, BlockPos center, int radius, TagKey<Block> tag) {
        Object2IntMap<Block> results = new Object2IntOpenHashMap<>();

        Player player = level.getNearestPlayer(center.getX(), center.getY(), center.getZ(), 10, false);

        if (player != null && player.isShiftKeyDown()) {
            radius = radius / 2;
        }

        for (BlockPos cursor : BlockPos.betweenClosed(center.getX() - radius, center.getY() - radius, center.getZ() - radius, center.getX() + radius, center.getY() + radius, center.getZ() + radius)) {
            Block block = getRepresentative(level.getBlockState(cursor).getBlock());
            if (Helpers.isBlock(block, tag)) {
                results.mergeInt(block, 1, Integer::sum);
            }
        }

        return results;
    }
}
