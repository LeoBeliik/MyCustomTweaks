package com.leobeliik.mycustomtweaks.mixins;

import net.minecraft.core.BlockPos;
import net.minecraft.core.GlobalPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import vazkii.botania.api.internal.ManaBurst;
import vazkii.botania.common.block.BotaniaBlocks;
import vazkii.botania.common.block.block_entity.mana.ManaSpreaderBlockEntity;
import vazkii.botania.common.entity.ManaBurstEntity;
import vazkii.botania.common.helper.EntityHelper;
import vazkii.botania.common.item.BotaniaItems;
import vazkii.botania.common.item.lens.BoreLens;
import vazkii.botania.common.item.lens.LensItem;
import vazkii.botania.xplat.BotaniaConfig;

import java.util.List;
import java.util.Optional;

@Mixin(BoreLens.class)
public class BoreMixin {

	@Shadow
	public static boolean canHarvest(int harvestLevel, BlockState state) {
		throw new UnsupportedOperationException("Implemented via mixin");
	}

	@Overwrite
	public boolean collideBurst(ManaBurst burst, HitResult rtr, boolean isManaBlock, boolean shouldKill, ItemStack stack) {
		Entity entity = burst.entity();
		Level level = entity.level();
		Entity player = burst.entity().getOwner();

		if (level.isClientSide || rtr.getType() != HitResult.Type.BLOCK) {
			return false;
		}

		BlockPos collidePos = ((BlockHitResult) rtr).getBlockPos();
		BlockState state = level.getBlockState(collidePos);

		ItemStack composite = ((LensItem) stack.getItem()).getCompositeLens(stack);
		boolean warpItems = !composite.isEmpty() && composite.is(BotaniaItems.WARP_LENS);
		ItemStack sourceLens = burst.getSourceLens();
		boolean canWarp = warpItems || sourceLens.is(BotaniaItems.WARP_LENS);

		if (canWarp && (state.is(BotaniaBlocks.FORCE_RELAY) || state.is(Blocks.PISTON)
				|| state.is(Blocks.MOVING_PISTON) || state.is(Blocks.PISTON_HEAD))) {
			return false;
		}
		if (!entity.mayInteract(level, collidePos)) {
			return true;
		}

		int harvestLevel = BotaniaConfig.common().harvestLevelBore();

		BlockEntity tile = level.getBlockEntity(collidePos);

		float hardness = state.getDestroySpeed(level, collidePos);
		int mana = burst.getMana();

		Optional<GlobalPos> source = burst.getBurstSourcePosition();
		if (!isManaBlock && canHarvest(harvestLevel, state) && hardness != -1 && (burst.isFake() || mana >= 24)) {
			if (!burst.hasAlreadyCollidedAt(collidePos) && !burst.isFake()) {
				List<ItemStack> items = Block.getDrops(state, (ServerLevel) level, collidePos, tile);

				if (!level.destroyBlock(collidePos, false, entity)) {
					return true;
				}

				boolean sourceless = source.isEmpty() || !burst.isBurstSourceDimension(level);
				boolean doWarp = warpItems && !sourceless;
				Vec3 dropPosition = Vec3.atCenterOf(collidePos);

				if (doWarp) {
					if (level.getBlockEntity(source.get().pos()) instanceof ManaSpreaderBlockEntity spreader) {
						Vec3 sourceVec = Vec3.atCenterOf(source.get().pos());
						float xRot = spreader.getRotationY();
						float yRot = -(spreader.getRotationX() + 90F);
						Vec3 inverseSpreaderDirection = ManaBurstEntity.calculateBurstVelocity(xRot, yRot).normalize().reverse();
						dropPosition = sourceVec.add(inverseSpreaderDirection);
					}
				} else if (player != null && player.isAlive()) {
					dropPosition = player.position();
				}

				if (level.getGameRules().getBoolean(GameRules.RULE_DOBLOCKDROPS)) {
					for (ItemStack stack_ : items) {
						ItemEntity itemEntity = new ItemEntity(level, dropPosition.x, dropPosition.y, dropPosition.z, stack_);
						itemEntity.setDefaultPickUpDelay();
						level.addFreshEntity(itemEntity);
						EntityHelper.addTeleportTicketIfFarAway(itemEntity, collidePos);
					}
				}

				burst.setMana(mana - 24);
			}

			shouldKill = false;
		}

		return shouldKill;
	}
}
