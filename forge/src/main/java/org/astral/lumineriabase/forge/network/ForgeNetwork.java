package org.astral.lumineriabase.forge.network;

import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.simple.SimpleChannel;
import org.astral.lumineriabase.Constants;
import org.astral.lumineriabase.forge.network.packets.*;

public class ForgeNetwork {
    private static final String PROTOCOL_VERSION = "1";

    @SuppressWarnings("removal")
    public static final SimpleChannel CHANNEL = NetworkRegistry.newSimpleChannel(
            new ResourceLocation(Constants.MODID, "main"),
            () -> PROTOCOL_VERSION,
            PROTOCOL_VERSION::equals,
            PROTOCOL_VERSION::equals
    );

    private static int packetId = 0;
    private static int id() { return packetId++; }

    @SuppressWarnings("unused")
    public static void init(FMLCommonSetupEvent event) {
        CHANNEL.registerMessage(id(), OpenLoginScreenPacket.class, OpenLoginScreenPacket::encode, OpenLoginScreenPacket::new, OpenLoginScreenPacket::handle);
        CHANNEL.registerMessage(id(), ClientLoginAttemptPacket.class, ClientLoginAttemptPacket::encode, ClientLoginAttemptPacket::new, ClientLoginAttemptPacket::handle);
        CHANNEL.registerMessage(id(), SignedVelocityPacket.class, SignedVelocityPacket::encode, SignedVelocityPacket::new, SignedVelocityPacket::handle);
        CHANNEL.registerMessage(id(), SyncPacket.class, SyncPacket::encode, SyncPacket::new, SyncPacket::handle);
    }
}