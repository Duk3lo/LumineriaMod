package org.astral.lumineriabase.forge.network.packets;

import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
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

    @SuppressWarnings("UnstableApiUsage")
    public void encode(@NotNull FriendlyByteBuf buf) {
        ByteArrayDataOutput out = ByteStreams.newDataOutput();
        out.writeUTF(this.action);
        out.writeUTF(this.playerName);
        buf.writeBytes(out.toByteArray());
    }

    public void handle(@NotNull Supplier<NetworkEvent.Context> ctx) {
        ctx.get().setPacketHandled(true);
    }
}