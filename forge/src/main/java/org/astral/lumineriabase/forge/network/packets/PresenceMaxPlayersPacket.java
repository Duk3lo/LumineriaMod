package org.astral.lumineriabase.forge.network.packets;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;
import org.astral.lumineriabase.client.ClientPresenceState;
import org.jetbrains.annotations.NotNull;

import java.util.function.Supplier;

public class PresenceMaxPlayersPacket {
    private final int maxPlayers;

    public PresenceMaxPlayersPacket(int maxPlayers) { this.maxPlayers = maxPlayers; }
    public PresenceMaxPlayersPacket(@NotNull FriendlyByteBuf buf) { this.maxPlayers = buf.readVarInt(); }
    public void encode(@NotNull FriendlyByteBuf buf) { buf.writeVarInt(maxPlayers); }

    public void handle(@NotNull Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> ClientPresenceState.setServerMaxPlayers(maxPlayers));
        ctx.get().setPacketHandled(true);
    }
}
