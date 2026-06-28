package com.leobeliik.mycustomtweaks;

import net.minecraft.core.registries.Registries;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BaseContainerBlockEntity;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.common.util.TriState;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;
import vazkii.botania.common.item.BotaniaItems;
import vazkii.botania.common.item.rod.SkiesRodItem;

@Mod(MyCustomTweaks.MODID)
public class MyCustomTweaks {
	public static final String MODID = "mycustomtweaks";
	//private static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, MODID);

	public MyCustomTweaks(IEventBus modEventBus, ModContainer modContainer) {
		NeoForge.EVENT_BUS.register(this);
	}

	@SubscribeEvent
	public void onShatBreak(PlayerEvent.BreakSpeed event) {
		ItemStack stack = event.getEntity().getItemInHand(InteractionHand.MAIN_HAND);
		Level level = event.getEntity().level();
		if (stack.is(BotaniaItems.TERRA_TRUNCATOR) && event.getState().is(BlockTags.MINEABLE_WITH_SHOVEL)) {
			int efficiency = EnchantmentHelper.getItemEnchantmentLevel(
					level.registryAccess().lookupOrThrow(Registries.ENCHANTMENT).getOrThrow(Enchantments.EFFICIENCY),
					stack);
			efficiency = efficiency == 0 ? 9 : (efficiency + 1) * 5; //numbers based on my ass
			event.setNewSpeed(event.getOriginalSpeed() * efficiency);
		}
	}

	@SubscribeEvent
	public void onCorporeaSparkUse(PlayerInteractEvent.RightClickBlock event) {
		BlockEntity be = event.getLevel().getBlockEntity(event.getHitVec().getBlockPos());
		if ((be instanceof BaseContainerBlockEntity)
				&& (event.getItemStack().is(BotaniaItems.CORPOREA_SPARK) || event.getItemStack().is(BotaniaItems.MASTER_CORPOREA_SPARK))) {
			event.setUseBlock(TriState.FALSE);
		}
	}

	@SubscribeEvent
	public void onUseTornado(PlayerInteractEvent.RightClickItem event) {
		Player player = event.getEntity();
		ItemStack itemstack = event.getItemStack();

		if (!player.isFallFlying() && player.getItemBySlot(EquipmentSlot.CHEST).canElytraFly(player)) {
			Level level = player.level();
			if (itemstack.getItem() instanceof SkiesRodItem tornado) {
				if (!tornado.isBarVisible(itemstack)) {
					player.startFallFlying();
					player.jumpFromGround();
				}
			}
		}
	}
}
