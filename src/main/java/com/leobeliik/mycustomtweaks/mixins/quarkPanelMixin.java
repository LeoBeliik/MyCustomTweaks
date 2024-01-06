package com.leobeliik.mycustomtweaks.mixins;


import com.firemerald.additionalplacements.block.VerticalSlabBlock;
import com.firemerald.additionalplacements.block.VerticalStairBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.PipeBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.block.state.properties.Property;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.violetmoon.quark.content.building.module.VerticalSlabsModule;

import java.util.Optional;

import static org.violetmoon.quark.content.building.module.VerticalSlabsModule.verticalSlabTag;

@Mixin(VerticalSlabsModule.class)
public class quarkPanelMixin {

    @Overwrite(remap = false)
    public static BlockState messWithPaneState(LevelAccessor level, BlockPos ourPos, BlockState state) {

        for (Direction dir : PipeBlock.PROPERTY_BY_DIRECTION.keySet()) {
            if (dir.getAxis().isHorizontal()) {
                BooleanProperty prop = (BooleanProperty) PipeBlock.PROPERTY_BY_DIRECTION.get(dir);
                boolean val = state.getValue(prop);
                if (!val) {
                    BlockState adjState = level.getBlockState(ourPos.relative(dir));
                    boolean should = shouldWallConnect(adjState, dir, false);
                    if (should) {
                        state = state.setValue(prop, true);
                    }
                }
            }
        }

        return state;
    }

    @Overwrite(remap = false)
    public static boolean shouldWallConnect(BlockState state, Direction dir, boolean prev) {
        Block block = state.getBlock();
        if (block instanceof VerticalSlabBlock || block instanceof VerticalStairBlock || state.is(verticalSlabTag)) {
            Optional<Property<?>> opt = state.getProperties().stream().filter((p) -> p.getName().equals("type") || p.getName().equals("facing")).findFirst();
            if (opt.isPresent()) {
                Property<?> prop = opt.get();
                if (prop instanceof EnumProperty) {
                    EnumProperty<?> ep = (EnumProperty) prop;
                    Enum<?> val = (Enum) state.getValue(prop);
                    String name = val.name().toLowerCase();
                    Direction vsDir = Direction.byName(name);
                    return vsDir != null && vsDir.getAxis() != dir.getAxis();
                }
            }
        }
        return false;
    }
}
