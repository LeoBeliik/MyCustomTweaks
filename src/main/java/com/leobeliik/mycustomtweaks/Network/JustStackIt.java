package com.leobeliik.mycustomtweaks.Network;

import net.dries007.tfc.common.capabilities.food.FoodCapability;
import net.dries007.tfc.common.capabilities.food.IFood;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ResultSlot;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.network.NetworkEvent;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.function.Supplier;

public class JustStackIt {
    private final int index;

    public JustStackIt(int index) {
        this.index = index;
    }

    JustStackIt(FriendlyByteBuf buffer) {
        this.index = buffer.readVarInt();
    }

    void encode(FriendlyByteBuf buffer) {
        buffer.writeVarInt(index);
    }

    void handle(Supplier<NetworkEvent.Context> supplier) {
        NetworkEvent.Context context = supplier.get();
        context.enqueueWork(() -> {
            final ServerPlayer player = context.getSender();
            if (player != null) {
                AbstractContainerMenu menu = player.containerMenu;
                if (index < 0 || index >= menu.slots.size()) {
                    return;
                }

                Slot targetSlot = menu.getSlot(index);
                ItemStack targetStack = targetSlot.getItem();
                IFood targetCap = targetStack.getCapability(FoodCapability.CAPABILITY).resolve().orElse(null);

                if (targetCap == null || targetStack.getMaxStackSize() == targetStack.getCount() || targetCap.isRotten()) {
                    return;
                }

                List<Slot> stackableSlots = getStackableSlots(targetSlot, menu.slots);
                int currentAmount = targetStack.getCount();
                int remaining = targetStack.getMaxStackSize() - currentAmount;
                long minCreationDate = targetCap.getCreationDate();

                Iterator<Slot> slotIterator = stackableSlots.iterator();
                while (remaining > 0 && slotIterator.hasNext()) {
                    Slot slot = slotIterator.next();
                    ItemStack stack = slot.getItem();
                    IFood cap = stack.getCapability(FoodCapability.CAPABILITY).resolve().orElse(null);

                    if (cap == null || cap.isRotten()) continue;

                    if (cap.getCreationDate() < minCreationDate) {
                        minCreationDate = cap.getCreationDate();
                    }

                    if (remaining < stack.getCount()) {
                        currentAmount += remaining;
                        stack.shrink(remaining);
                        remaining = 0;
                    } else {
                        currentAmount += stack.getCount();
                        remaining -= stack.getCount();
                        stack.shrink(stack.getCount());
                    }
                }

                targetStack.setCount(currentAmount);
                targetCap.setCreationDate(minCreationDate);

                //menu.slotsChanged(menu.getCraftSlots());
            }
        });
    }

    private List<Slot> getStackableSlots(Slot targetSlot, List<Slot> inventorySlots) {
        List<Slot> stackableSlots = new ArrayList<>();
        for (Slot slot : inventorySlots) {
            if (slot.getSlotIndex() != targetSlot.getSlotIndex() && !(slot instanceof ResultSlot)) {
                ItemStack stack = slot.getItem();
                if (FoodCapability.areStacksStackableExceptCreationDate(targetSlot.getItem(), stack)) {
                    stackableSlots.add(slot);
                }
            }
        }
        stackableSlots.sort(Comparator.comparingInt(slot -> slot.getItem().getCount()));
        return stackableSlots;
    }
}
