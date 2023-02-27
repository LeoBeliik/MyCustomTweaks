package com.leobeliik.mycustomtweaks.mixins;

import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.SlotAccess;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ClickAction;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import vazkii.botania.common.handler.BotaniaSounds;
import vazkii.botania.common.helper.ItemNBTHelper;
import vazkii.botania.common.item.AssemblyHaloItem;
import vazkii.botania.common.item.ManufactoryHaloItem;
import java.util.List;

@Mixin(ManufactoryHaloItem.class)
public class ManuHaloMixin extends AssemblyHaloItem {
    @Unique
    private static final String TAG_INACTIVE = "inactive";

    public ManuHaloMixin(Properties props) {
        super(props);
    }

    @Inject(method = "Lvazkii/botania/common/item/ManufactoryHaloItem;inventoryTick(Lnet/minecraft/world/item/ItemStack;Lnet/minecraft/world/level/Level;Lnet/minecraft/world/entity/Entity;IZ)V",
            at = @At(value = "HEAD"), cancellable = true)
    public void inventoryTick(ItemStack stack, Level world, Entity entity, int pos, boolean equipped, CallbackInfo ci) {
        if (ItemNBTHelper.getBoolean(stack, TAG_INACTIVE, true)) {
            ci.cancel();
        }
    }

    @Unique
    @Override
    public boolean overrideOtherStackedOnMe(ItemStack stack, ItemStack cursor, Slot slot, ClickAction click, Player player, SlotAccess access) {
        Level world = player.getLevel();
        if (click == ClickAction.SECONDARY && slot.allowModification(player) && cursor.isEmpty()) {
            toggleActive(stack, player, world);
            access.set(cursor);
            return true;
        }
        return false;
    }

    @Override
    public void appendHoverText(ItemStack stack, Level world, List<Component> stacks, TooltipFlag flags) {
        if (ItemNBTHelper.getBoolean(stack, TAG_INACTIVE, false)) {
            stacks.add(Component.translatable("botaniamisc.inactive"));
        } else {
            stacks.add(Component.translatable("botaniamisc.active"));
        }
    }

    @Unique
    private void toggleActive(ItemStack stack, Player player, Level world) {
        ItemNBTHelper.setBoolean(stack, TAG_INACTIVE, !ItemNBTHelper.getBoolean(stack, TAG_INACTIVE, false));
        world.playSound(player, player.getX(), player.getY(), player.getZ(), BotaniaSounds.altarCraft, SoundSource.NEUTRAL, 1F, 1.5F);
    }

    @Override
    public boolean isBarVisible(ItemStack stack) {
        return ItemNBTHelper.getBoolean(stack, TAG_INACTIVE, true);
    }
}
