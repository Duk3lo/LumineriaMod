package org.astral.lumineriabase.forge.network.packets;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;
import org.astral.lumineriabase.forge.setup.ForgeConfig;
import org.astral.lumineriabase.platform.Services;
import org.jetbrains.annotations.NotNull;

import java.util.function.Supplier;

public class RouterHandshakePacket {
    private final String key;

    public RouterHandshakePacket(String key) { this.key = key; }
    public RouterHandshakePacket(@NotNull FriendlyByteBuf buf) { this.key = buf.readUtf(256); }
    public void encode(@NotNull FriendlyByteBuf buf) { buf.writeUtf(key, 256); }

    public void handle(@NotNull Supplier<NetworkEvent.Context> ctxSupplier) {
        NetworkEvent.Context ctx = ctxSupplier.get();
        ctx.enqueueWork(() -> {
            if (!key.equals(ForgeConfig.routingKey)) {
                Services.PLATFORM.saveRoutingKey(key);
            }
        });
        ctx.setPacketHandled(true);
    }
}