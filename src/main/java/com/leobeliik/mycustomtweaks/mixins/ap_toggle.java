package com.leobeliik.mycustomtweaks.mixins;

import com.firemerald.additionalplacements.block.interfaces.IPlacementBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import java.util.Arrays;

@Mixin(IPlacementBlock.class)
public interface ap_toggle {

    @Overwrite(remap = false)
    default boolean disablePlacement(BlockPos pos, Level level, Direction direction) {
        Player player = level.getNearestPlayer(pos.getX(), pos.getY(), pos.getZ(), 2.0D, null);
        return player == null || !Arrays.toString(player.getItemInHand(InteractionHand.OFF_HAND).getTags().toArray()).contains("hammer");
    }
}
