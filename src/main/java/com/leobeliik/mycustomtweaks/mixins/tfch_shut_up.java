package com.leobeliik.mycustomtweaks.mixins;

import com.lumintorious.tfchomestead.common.entity.HomesteadEntities;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.util.UUID;

@Mixin(HomesteadEntities.class)
public class tfch_shut_up {

    @Redirect(method = "Lcom/lumintorious/tfchomestead/common/entity/HomesteadEntities;resetTradesOnSpawn(Lnet/minecraftforge/event/entity/EntityJoinWorldEvent;)V",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/server/level/ServerPlayer;m_6352_(Lnet/minecraft/network/chat/Component;Ljava/util/UUID;)V"))
    private static void resetTradesOnSpawn(ServerPlayer player, Component component, UUID uuid) {

    }
}
