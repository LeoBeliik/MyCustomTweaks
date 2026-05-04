package com.leobeliik.mycustomtweaks.mixins;

import net.dries007.tfc.common.blocks.rotation.CrankshaftBlock;
import net.dries007.tfc.util.Helpers;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.tags.TagKey;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.phys.BlockHitResult;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(CrankshaftBlock.class)
public abstract class shaftMixin {

    private static final TagKey<Item> STEEL_RODS = TagKey.create(Registries.ITEM, Helpers.resourceLocation("c", "rods/wrought_iron"));

    @Shadow
    @Final
    public static EnumProperty<CrankshaftBlock.Part> PART;

    @Shadow
    protected abstract BlockPos getPartnerPos(BlockPos pos, BlockState state);

    @Overwrite
    protected ItemInteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hitResult) {
        ItemStack held = player.getItemInHand(hand);
        if (state.getValue(PART) == CrankshaftBlock.Part.BASE && Helpers.isItem(held.getItem(), STEEL_RODS)) {
            BlockPos partnerPos = this.getPartnerPos(pos, state);
            BlockState stateAt = level.getBlockState(partnerPos);
            if (stateAt.canBeReplaced() && stateAt.getFluidState().isEmpty()) {
                level.setBlockAndUpdate(partnerPos, (BlockState)state.setValue(PART, CrankshaftBlock.Part.SHAFT));
                if (!player.isCreative()) {
                    held.shrink(1);
                }

                return ItemInteractionResult.sidedSuccess(level.isClientSide);
            }
        }

        return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
    }
}
