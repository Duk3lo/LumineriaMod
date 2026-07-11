package org.astral.lumineriabase.forge.network.packets;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;
import org.jetbrains.annotations.NotNull;

import java.util.function.Supplier;

public class AuthVelocityPacket {
    private final String action;
    private final String playerName;

    public AuthVelocityPacket(String action, String playerName) {
        this.action = action;
        this.playerName = playerName;
    }

    public AuthVelocityPacket(@NotNull FriendlyByteBuf buf) {
        this.action = buf.readUtf();
        this.playerName = buf.readUtf();
    }

    public void encode(@NotNull FriendlyByteBuf buf) {
        buf.writeUtf(action);
        buf.writeUtf(playerName);
    }

    public void handle(@NotNull Supplier<NetworkEvent.Context> ctx) {
        ctx.get().setPacketHandled(true);
    }
}