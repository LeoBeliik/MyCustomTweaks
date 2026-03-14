package com.leobeliik.mycustomtweaks.mixins;

import net.dries007.tfc.util.Helpers;
import net.dries007.tfc.util.calendar.Calendars;
import net.dries007.tfc.util.calendar.ICalendar;
import net.dries007.tfc.util.climate.ClimateModel;
import net.dries007.tfc.util.climate.OverworldClimateModel;
import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.LightLayer;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

//TODO FOR TESTING ONLY!
@Mixin(OverworldClimateModel.class)
public abstract class fogmixin implements ClimateModel {

    @Shadow
    protected abstract RandomSource seededRandom(long day, long salt);

    @Overwrite
    public float getFog(@NotNull LevelReader level, @NotNull BlockPos pos) {
        ICalendar calendar = Calendars.get(level);
        RandomSource random = this.seededRandom(calendar.getTotalCalendarDays(), 129341623413L);
        float fogModifier = random.nextFloat() * 2;
        float hourOfDay = 24.0F * calendar.getCalendarFractionOfDay();
        float scaledTime;
        if (4.0F <= hourOfDay && hourOfDay < 6.0F) {
            scaledTime = Mth.map(hourOfDay, 4.0F, 6.0F, 0.0F, 1.0F);
        } else if (6.0F <= hourOfDay && hourOfDay < 10.0F) {
            scaledTime = 1.0F;
        } else if (10.0F <= hourOfDay && hourOfDay < 12.0F) {
            scaledTime = Mth.map(hourOfDay, 10.0F, 12.0F, 1.0F, 0.0F);
        } else {
            scaledTime = 0.0F;
        }

        float rainfall = this.getInstantRainfall(level, pos);
        float rainfallModifier = Mth.clampedMap(rainfall, 150.0F, 300.0F, 0.0F, 1.0F);
        float skylightModifier = Mth.clampedMap((float) level.getBrightness(LightLayer.SKY, pos), 0.0F, 10.0F, 0.0F, 1.0F);
        return Helpers.easeInOutCubic(scaledTime) * fogModifier * rainfallModifier * skylightModifier;
    }
}
