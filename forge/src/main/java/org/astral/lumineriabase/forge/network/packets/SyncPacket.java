package org.astral.lumineriabase.forge.network.packets;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;
import org.astral.lumineriabase.forge.events.ClientEvents;
import org.jetbrains.annotations.NotNull;

import java.util.function.Supplier;

public class SyncPacket {
    private final byte[] data;

    public SyncPacket(@NotNull FriendlyByteBuf buf) {
        this.data = new byte[buf.readableBytes()];
        buf.readBytes(this.data);
    }

    public void encode(@NotNull FriendlyByteBuf buf) {
        buf.writeBytes(this.data);
    }

    public void handle(@NotNull Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> ClientEvents.handleSyncPacket(this.data));
        ctx.get().setPacketHandled(true);
    }
}