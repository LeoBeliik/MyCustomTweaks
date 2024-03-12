package com.leobeliik.mixin;

import de.dafuqs.spectrum.energy.InkStorage;
import de.dafuqs.spectrum.energy.color.InkColor;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;

@Mixin(InkStorage.class)
public interface betterInk {
    @Overwrite(remap = false)
    static long transferInk(@NotNull InkStorage source, @NotNull InkStorage destination, @NotNull InkColor color) {
        if (!destination.accepts(color)) {
            return 0L;
        } else {
            long sourceAmount = source.getEnergy(color);
            if (sourceAmount > 0L) {
                long destinationRoom = destination.getRoom(color);
                if (destinationRoom > 0L) {
                    long destinationAmount = destination.getEnergy(color);
                    if (sourceAmount > 0L) {
                        long transferAmount = Math.min(100L, sourceAmount);
                        destination.addEnergy(color, transferAmount);
                        source.drainEnergy(color, transferAmount);
                        return transferAmount;
                    }
                }
            }

            return 0L;
        }
    }
}
