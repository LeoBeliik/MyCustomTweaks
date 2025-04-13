package com.leobeliik.mycustomtweaks.mixins;

import com.mna.api.blocks.tile.TileEntityWithInventory;
import com.mna.api.tools.MATags;
import com.mna.blocks.runeforging.RuneforgeBlock;
import com.mna.blocks.tileentities.RuneForgeTile;
import com.mna.recipes.RecipeInit;
import com.mna.recipes.arcanefurnace.ArcaneFurnaceRecipe;
import com.mna.tools.ContainerTools;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(RuneForgeTile.class)
public class RuneForgeMixin extends TileEntityWithInventory {

    @Shadow
    private ItemStack __cachedRecipeOutput;

    @Shadow
    private ArcaneFurnaceRecipe __cachedRecipe;

    @Shadow
    private int burnTime;

    @Shadow
    @Final
    private static ResourceLocation tag_blacklist;

    @Shadow
    private boolean isRepairing;

    public RuneForgeMixin(BlockEntityType<?> tileEntityTypeIn, BlockPos pos, BlockState state, int inventorySize) {
        super(tileEntityTypeIn, pos, state, inventorySize);
    }

    @Overwrite(remap = false)
    private boolean hasMultiUpgrade() {
        return false;
    }

    @Overwrite(remap = false)
    private boolean hasRepairUpgrade() {
        return false;
    }

    @Overwrite(remap = false)
    private boolean cacheRecipe() {
        CraftingContainer inv = ContainerTools.createTemporaryContainer(this.getItem(0));
        ArcaneFurnaceRecipe smeltRecipe = (ArcaneFurnaceRecipe) this.getLevel().getRecipeManager().getRecipeFor((RecipeType) RecipeInit.ARCANE_FURNACE_TYPE.get(), inv, this.level).orElse((Object) null);
        this.__cachedRecipeOutput = null;
        ItemStack ingredient = this.getItem(0);
        if (this.hasRepairUpgrade()) {
            if (!MATags.isItemEqual(ingredient, tag_blacklist)) {
                this.isRepairing = true;
                return true;
            } else {
                return false;
            }
        } else {
            boolean ret = false;
            if (smeltRecipe != null) {
                this.__cachedRecipe = smeltRecipe;
                this.__cachedRecipeOutput = smeltRecipe.getResultItem();
                this.__cachedRecipeOutput.setCount(this.__cachedRecipeOutput.getCount() * this.getItem(0).getCount());
                this.burnTime = smeltRecipe.getBurnTime() * this.getItem(0).getCount();
                ret = true;
            }

            if ((Integer) this.getBlockState().getValue(RuneforgeBlock.MATERIAL) == 1) {
                this.burnTime /= 8;
            }

            if ((Boolean) this.getBlockState().getValue(RuneforgeBlock.SPEED)) {
                this.burnTime /= 6;
            }

            if (ret && this.burnTime < 1) {
                this.burnTime = 1;
            }

            if (!MATags.isItemEqual(ingredient, tag_blacklist) && ret && this.hasMultiUpgrade()) {
                this.__cachedRecipeOutput.setCount(Math.min(this.__cachedRecipeOutput.getCount() * 2, 64));
            }

            return ret;
        }
    }
}
