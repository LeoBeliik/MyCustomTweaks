package com.leobeliik.mycustomtweaks.mixins;

import journeymap.client.command.CmdTeleportWaypoint;
import net.minecraft.client.Minecraft;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;

@Mixin(CmdTeleportWaypoint.class)
public class noTeleportyMixin {

    @Overwrite(remap = false)
    public static boolean isPermitted(Minecraft mc) {
        return false;
    }
}
