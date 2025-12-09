package com.leobeliik.mycustomtweaks;

import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.KeyMapping;
import net.minecraft.core.Holder;
import net.minecraft.network.chat.contents.TranslatableContents;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.monster.Creeper;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BaseContainerBlockEntity;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.client.event.RegisterKeyMappingsEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.common.util.TriState;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;
import net.neoforged.neoforge.event.level.ExplosionEvent;
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
        if (stack.is(BotaniaItems.terraPick) && event.getState().is(BlockTags.MINEABLE_WITH_SHOVEL)) {
            int efficiency = stack.getItem().getEnchantmentLevel(stack, (Holder<Enchantment>) Enchantments.EFFICIENCY);
            efficiency = efficiency == 0 ? 9 : (efficiency + 1) * 5; //numbers based on my ass
            event.setNewSpeed(event.getOriginalSpeed() * efficiency);
        }
    }

    @SubscribeEvent
    public void onCorporeaSparkUse(PlayerInteractEvent.RightClickBlock event) {
        BlockEntity be = event.getLevel().getBlockEntity(event.getHitVec().getBlockPos());
        if ((be instanceof BaseContainerBlockEntity)
                && (event.getItemStack().is(BotaniaItems.corporeaSpark) || event.getItemStack().is(BotaniaItems.corporeaSparkMaster))) {
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
