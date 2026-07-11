package org.astral.lumineriabase.forge.network.packets;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;
import org.astral.lumineriabase.client.ClientActionExecutor;
import org.astral.lumineriabase.forge.network.ForgeNetwork;
import org.jetbrains.annotations.NotNull;

import java.util.function.Supplier;

public class OpenLoginScreenPacket {
    private final boolean isRegistered;
    private final String message;

    public OpenLoginScreenPacket(boolean isRegistered, String message) {
        this.isRegistered = isRegistered;
        this.message = message;
    }

    public OpenLoginScreenPacket(@NotNull FriendlyByteBuf buf) {
        this.isRegistered = buf.readBoolean();
        this.message = buf.readUtf();
    }

    public void encode(@NotNull FriendlyByteBuf buf) {
        buf.writeBoolean(isRegistered);
        buf.writeUtf(message);
    }

    public void handle(@NotNull Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            if ("CLOSE_AND_RELOAD".equals(message)) {
                ClientActionExecutor.clearPendingLogin();
            } else {
                ClientActionExecutor.openLoginScreen(isRegistered, message, (password) -> {
                    ForgeNetwork.CHANNEL.sendToServer(new ClientLoginAttemptPacket(password));
                });
            }
        });
        ctx.get().setPacketHandled(true);
    }
}