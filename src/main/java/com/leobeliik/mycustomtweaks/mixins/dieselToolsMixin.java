package com.leobeliik.mycustomtweaks.mixins;

import blusunrize.immersiveengineering.common.items.DieselToolItem;
import blusunrize.immersiveengineering.common.register.IEFluids;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(DieselToolItem.class)
public class dieselToolsMixin {

    @Redirect(method = "Lblusunrize/immersiveengineering/common/items/DieselToolItem;consumeDurability(Lnet/minecraft/world/item/ItemStack;Lnet/minecraft/world/level/Level;Lnet/minecraft/world/level/block/state/BlockState;Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/entity/LivingEntity;)V",
            at = @At(value = "INVOKE", target = "Lnet/neoforged/neoforge/fluids/capability/IFluidHandler;drain(ILnet/neoforged/neoforge/fluids/capability/IFluidHandler$FluidAction;)Lnet/neoforged/neoforge/fluids/FluidStack;"),
            remap = false)
    public FluidStack altFuel(IFluidHandler handler, int i, IFluidHandler.FluidAction action, ItemStack stack, Level level,
                              BlockState state, BlockPos pos, LivingEntity entity) {
        int cost = handler.getFluidInTank(handler.getTanks()).getFluid().defaultFluidState().is(IEFluids.CREOSOTE.getStill()) ? 20 : 1;
        return handler.drain(cost, IFluidHandler.FluidAction.EXECUTE);
    }
}
