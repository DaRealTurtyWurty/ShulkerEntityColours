package dev.turtywurty.shulkercolours.network;

import dev.turtywurty.shulkercolours.ShulkerColours;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.simple.SimpleChannel;

public class PacketManager {
    private static final String PROTOCOL_VERSION = "1";

    public static final SimpleChannel INSTANCE = NetworkRegistry.newSimpleChannel(
            new ResourceLocation(ShulkerColours.MODID, "main"),
            () -> PROTOCOL_VERSION,
            PROTOCOL_VERSION::equals,
            PROTOCOL_VERSION::equals);

    public static void init() {
        int index = 0;

        INSTANCE.messageBuilder(CSyncShulkerColourPacket.class, index++, NetworkDirection.PLAY_TO_CLIENT)
                .encoder(CSyncShulkerColourPacket::encode)
                .decoder(CSyncShulkerColourPacket::new)
                .consumerMainThread(CSyncShulkerColourPacket::handle)
                .add();

        INSTANCE.messageBuilder(SRequestShulkerColourPacket.class, index++, NetworkDirection.PLAY_TO_SERVER)
                .encoder(SRequestShulkerColourPacket::encode)
                .decoder(SRequestShulkerColourPacket::new)
                .consumerMainThread(SRequestShulkerColourPacket::handle)
                .add();

        ShulkerColours.LOGGER.info("Registered {} packets for mod '{}'", index, ShulkerColours.MODID);
    }
}
