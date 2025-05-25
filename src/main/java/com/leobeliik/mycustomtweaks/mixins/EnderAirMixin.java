package com.leobeliik.mycustomtweaks.mixins;

import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.gameevent.GameEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import vazkii.botania.common.item.BotaniaItems;
import vazkii.botania.common.item.material.EnderAirItem;
import static vazkii.botania.common.item.material.EnderAirItem.pickupFromEntity;

@Mixin(EnderAirItem.class)
public class EnderAirMixin {

    @Overwrite(remap = false)
    public static InteractionResultHolder<ItemStack> onPlayerInteract(Player player, Level world, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        if (!stack.isEmpty() && stack.is(BotaniaItems.vial)) {
            if ((world.dimension() != Level.END) && !pickupFromEntity(world, player.getBoundingBox().inflate((double)1.0F))) {
                return InteractionResultHolder.pass(stack);
            } else {
                if (!world.isClientSide) {
                    ItemStack enderAir = new ItemStack(BotaniaItems.enderAirBottle);
                    player.getInventory().placeItemBackInInventory(enderAir);
                    stack.shrink(1);
                    world.playSound((Player)null, player.blockPosition(), SoundEvents.ITEM_PICKUP, SoundSource.NEUTRAL, 0.5F, 1.0F);
                    world.gameEvent(player, GameEvent.FLUID_PICKUP, player.position());
                }

                return InteractionResultHolder.sidedSuccess(stack, world.isClientSide());
            }
        } else {
            return InteractionResultHolder.pass(stack);
        }
    }
}
