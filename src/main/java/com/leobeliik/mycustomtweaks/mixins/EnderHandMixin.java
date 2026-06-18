package com.leobeliik.mycustomtweaks.mixins;

import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.entity.SlotAccess;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ChestMenu;
import net.minecraft.world.inventory.ClickAction;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import vazkii.botania.api.mana.ManaItemHandler;
import vazkii.botania.common.item.EnderHandItem;

@Mixin(EnderHandItem.class)
public class EnderHandMixin extends Item {

	@Shadow
	@Final
	private static int COST_SELF;

	public EnderHandMixin(Properties properties) {
		super(properties);
	}

	@Override
	public boolean overrideOtherStackedOnMe(ItemStack hand, ItemStack toInsert, Slot slot, ClickAction clickAction, Player player, SlotAccess cursorAccess) {
		if (toInsert.isEmpty() && clickAction == ClickAction.SECONDARY && !player.hasContainerOpen() && ManaItemHandler.instance().requestManaExact(hand, player, COST_SELF, false)) {
			if (!player.level().isClientSide) {
				player.openMenu(new SimpleMenuProvider((windowId, playerInv, p) ->
						ChestMenu.threeRows(windowId, playerInv, p.getEnderChestInventory()), hand.getHoverName()));
				ManaItemHandler.instance().requestManaExact(hand, player, COST_SELF, true);
			}
			player.playSound(SoundEvents.ENDER_CHEST_OPEN, 1F, 1F);
			return true;
		}
		return false;
	}
}
