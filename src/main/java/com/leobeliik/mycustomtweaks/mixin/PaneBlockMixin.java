package com.leobeliik.mycustomtweaks.mixin;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.IronBarsBlock;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;

import static net.minecraft.world.level.block.Block.isExceptionForConnection;

@Mixin(IronBarsBlock.class)
public abstract class PaneBlockMixin {

	private static final TagKey<Block> VERTICAL_SLABS =
			TagKey.create(Registries.BLOCK, Identifier.fromNamespaceAndPath("clutternomore", "vertical_slabs"));

	@Overwrite
	public final boolean attachsTo(BlockState state, boolean faceSolid) {
		return state.is(VERTICAL_SLABS) || !isExceptionForConnection(state) && faceSolid || state.getBlock() instanceof IronBarsBlock || state.is(BlockTags.WALLS);
	}
}