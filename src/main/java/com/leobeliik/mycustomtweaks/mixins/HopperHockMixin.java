package com.leobeliik.mycustomtweaks.mixins;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.world.item.BundleItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import vazkii.botania.common.block.flower.functional.HopperhockBlockEntity;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;

@Mixin(HopperhockBlockEntity.class)
public class HopperHockMixin {

    @Inject(method = "Lvazkii/botania/common/block/flower/functional/HopperhockBlockEntity;getFilterForInventory(Lnet/minecraft/world/level/Level;Lnet/minecraft/core/BlockPos;Z)Ljava/util/List;",
            at = @At(value = "RETURN"), remap = false)
    private static List<ItemStack> getBundleFiltering(Level level, BlockPos pos, boolean recursiveForDoubleChests, CallbackInfoReturnable cir) {
        List<ItemStack> list = (List<ItemStack>) cir.getReturnValue();
        for (ItemStack stack : new ArrayList<>(list)) {
            if (stack.getItem() instanceof BundleItem bundle) {
                CompoundTag tag = stack.getTag();
                list.remove(stack); //should it pick up bundles?
                if (tag != null) {
                    ListTag items = tag.getList("Items", Tag.TAG_COMPOUND);
                    IntStream.range(0, items.size()).mapToObj(i -> ItemStack.of(items.getCompound(i))).forEach(list::add);
                }
            }
        }
        return list;
    }
}
