package org.astral.lumineriabase.forge.network.packets;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkEvent;
import org.astral.lumineriabase.auth.ServerAuthManager;
import org.jetbrains.annotations.NotNull;

import java.util.function.Supplier;

public class ClientLoginAttemptPacket {
    private final String password;

    public ClientLoginAttemptPacket(String password) {
        this.password = password;
    }

    public ClientLoginAttemptPacket(@NotNull FriendlyByteBuf buf) {
        this.password = buf.readUtf();
    }

    public void encode(@NotNull FriendlyByteBuf buf) {
        buf.writeUtf(password);
    }

    public void handle(@NotNull Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            ServerPlayer player = ctx.get().getSender();
            if (player != null) {
                ServerAuthManager.processLoginAttempt(player, password);
            }
        });
        ctx.get().setPacketHandled(true);
    }
}