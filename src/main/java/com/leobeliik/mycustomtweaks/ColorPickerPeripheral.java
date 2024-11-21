package com.leobeliik.mycustomtweaks;

import dan200.computercraft.api.lua.LuaFunction;
import dan200.computercraft.api.peripheral.GenericPeripheral;
import de.dafuqs.spectrum.api.energy.color.InkColor;
import de.dafuqs.spectrum.blocks.energy.ColorPickerBlockEntity;
import de.dafuqs.spectrum.registries.SpectrumRegistries;

import java.util.HashMap;
import java.util.Map;

public final class ColorPickerPeripheral implements GenericPeripheral {

    @Override
    public String id() {
        return "spectrum:color_picker";
    }

    @LuaFunction(mainThread = true)
    public Map<String, Long> getColors(ColorPickerBlockEntity cp) {
        Map<String, Long> inkStored = new HashMap<>();
        for (InkColor color : SpectrumRegistries.INK_COLORS)
            inkStored.put(color.getColoredInkName().getString(), cp.getEnergyStorage().getEnergy(color));
        return inkStored;
    }

}
