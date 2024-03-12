package com.leobeliik.mixin;

import journeymap.client.command.CmdTeleportWaypoint;
import net.minecraft.client.MinecraftClient;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;

@Mixin(CmdTeleportWaypoint.class)
public class noTeleportyMixin {

    @Overwrite(remap = false)
    public static boolean isPermitted(MinecraftClient mc) {
        return false;
    }
}
