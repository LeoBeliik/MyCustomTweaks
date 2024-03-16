package com.leobeliik.mycustomtweaks.mixins;

import com.mojang.datafixers.util.Pair;
import net.mehvahdjukaar.supplementaries.common.items.SackItem;
import net.mehvahdjukaar.supplementaries.common.utils.forge.ItemsUtilImpl;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.items.IItemHandler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;

import static net.mehvahdjukaar.supplementaries.common.utils.forge.ItemsUtilImpl.getItemHandler;

@Mixin(ItemsUtilImpl.class)
public abstract class dropMeSackMixin {

    @Overwrite(remap = false)
    public static boolean extractFromContainerItemIntoSlot(Player player, ItemStack containerStack, Slot slot) {
        if (slot.mayPickup(player) && containerStack.getCount() == 1) {
            Pair<IItemHandler, BlockEntity> handlerAndTe = getItemHandler(containerStack, player);
            if (handlerAndTe != null) {
                IItemHandler handler = (IItemHandler)handlerAndTe.getFirst();

                for(int s = 0; s < handler.getSlots(); ++s) {
                    ItemStack selected = handler.getStackInSlot(s);
                    if (!selected.isEmpty()) {
                        ItemStack dropped = handler.extractItem(s, containerStack.getItem() instanceof SackItem ? 64 : 1, false);
                        if (slot.mayPlace(dropped)) {
                            slot.set(dropped);
                            containerStack.getOrCreateTag().put("BlockEntityTag", ((BlockEntity)handlerAndTe.getSecond()).saveWithoutMetadata());
                            return true;
                        }

                        return false;
                    }
                }
            }
        }

        return false;
    }
}
