package com.leobeliik.mycustomtweaks.Network;

import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.PacketDistributor;
import net.minecraftforge.network.simple.SimpleChannel;

import static com.leobeliik.mycustomtweaks.MyCustomTweaks.MODID;

public class Network {
    public static SimpleChannel INSTANCE;

    private static int packetId = 0;

    private static int id() {
        return packetId++;
    }

    public static void register() {

        INSTANCE = NetworkRegistry.ChannelBuilder
                .named(new ResourceLocation(MODID))
                .networkProtocolVersion(() -> "1.0")
                .clientAcceptedVersions(s -> true)
                .serverAcceptedVersions(s -> true)
                .simpleChannel();

        INSTANCE.messageBuilder(JustStackIt.class, id(), NetworkDirection.PLAY_TO_SERVER)
                .decoder(JustStackIt::new)
                .encoder(JustStackIt::encode)
                .consumerMainThread(JustStackIt::handle)
                .add();
    }

    public static <MSG> void sendToServer(MSG message) {
        INSTANCE.sendToServer(message);
    }
}
