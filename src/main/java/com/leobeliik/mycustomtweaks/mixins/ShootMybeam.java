package com.leobeliik.mycustomtweaks.mixins;


import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import slimeknights.tconstruct.library.modifiers.Modifier;
import slimeknights.tconstruct.library.tools.context.ToolAttackContext;
import slimeknights.tconstruct.library.tools.helper.ToolAttackUtil;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;
import tcintegrations.items.modifiers.tool.TerraModifier;
import vazkii.botania.api.mana.ManaItemHandler;
import vazkii.botania.common.entity.EntityManaBurst;
import vazkii.botania.common.handler.ModSounds;

@Mixin(TerraModifier.class)
public class ShootMybeam extends Modifier {

    @Inject(method = "Ltcintegrations/items/modifiers/tool/TerraModifier;afterEntityHit(Lslimeknights/tconstruct/library/tools/nbt/IToolStackView;ILslimeknights/tconstruct/library/tools/context/ToolAttackContext;F)I",
            at = @At("TAIL"), remap = false)
    public int beam(IToolStackView tool, int level, ToolAttackContext context, float damageDealt, CallbackInfoReturnable cir) {
        Player player = context.getPlayerAttacker() != null ? context.getPlayerAttacker() : null;
        if (player != null) {
            EntityManaBurst burst = getBurst(player, player.getMainHandItem());
            player.level.addFreshEntity(burst);
            player.getMainHandItem().hurtAndBreak(1, player, (p) -> {
                p.broadcastBreakEvent(InteractionHand.MAIN_HAND);
            });
        }
        return 0;
    }

    @Unique
    private static EntityManaBurst getBurst(Player player, ItemStack stack) {
        EntityManaBurst burst = new EntityManaBurst(player);
        float motionModifier = 7.0F;
        burst.setColor(2162464);
        burst.setMana(100);
        burst.setStartingMana(100);
        burst.setMinManaLoss(40);
        burst.setManaLossPerTick(4.0F);
        burst.setGravity(0.0F);
        burst.setDeltaMovement(burst.getDeltaMovement().scale((double)motionModifier));
        burst.setSourceLens(stack.copy());
        return burst;
    }
    
}
