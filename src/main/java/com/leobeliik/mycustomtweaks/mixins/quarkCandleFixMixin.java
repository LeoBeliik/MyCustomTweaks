package com.leobeliik.mycustomtweaks.mixins;

import net.minecraft.core.BlockPos;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.violetmoon.quark.addons.oddities.block.be.AbstractEnchantingTableBlockEntity;
import org.violetmoon.quark.addons.oddities.block.be.MatrixEnchantingTableBlockEntity;
import org.violetmoon.quark.addons.oddities.module.MatrixEnchantingModule;
import org.violetmoon.quark.base.Quark;

import java.util.Map;

@Mixin(MatrixEnchantingTableBlockEntity.class)
public abstract class quarkCandleFixMixin extends AbstractEnchantingTableBlockEntity {
    public quarkCandleFixMixin(BlockEntityType<?> tileEntityTypeIn, BlockPos pos, BlockState state) {
        super(tileEntityTypeIn, pos, state);
    }

    @Shadow
    protected abstract boolean isAirGap(int j, int k, boolean allowWater, boolean allowShortBlock);

    @Shadow
    protected abstract float getEnchantPowerAt(Level world, BlockPos pos);

    @Shadow
    public int bookshelfPower;

    @Shadow
    @Final
    public Map<Enchantment, Integer> influences;

    @Shadow
    public int enchantability;

    @Overwrite(remap = false)
    public void updateEnchantPower() {
        boolean allowWater = MatrixEnchantingModule.allowUnderwaterEnchanting;
        boolean allowShort = MatrixEnchantingModule.allowShortBlockEnchanting;
        float power = 0.0F;
        ItemStack item = this.getItem(0);
        this.influences.clear();

        for (int j = -1; j <= 1; ++j) {
            for (int k = -1; k <= 1; ++k) {
                if (this.isAirGap(j, k, allowWater, allowShort)) {
                    power += this.getEnchantPowerAt(this.level, this.worldPosition.offset(k * 2, 0, j * 2));
                    power += this.getEnchantPowerAt(this.level, this.worldPosition.offset(k * 2, 1, j * 2));
                    if (k != 0 && j != 0) {
                        power += this.getEnchantPowerAt(this.level, this.worldPosition.offset(k * 2, 0, j));
                        power += this.getEnchantPowerAt(this.level, this.worldPosition.offset(k * 2, 1, j));
                        power += this.getEnchantPowerAt(this.level, this.worldPosition.offset(k, 0, j * 2));
                        power += this.getEnchantPowerAt(this.level, this.worldPosition.offset(k, 1, j * 2));
                    }
                }
            }
        }

        this.bookshelfPower = Math.min((int) power, MatrixEnchantingModule.maxBookshelves);

        if (!item.isEmpty()) {
            this.enchantability = Quark.ZETA.itemExtensions.get(item).getEnchantmentValueZeta(item);
        }

    }
}
