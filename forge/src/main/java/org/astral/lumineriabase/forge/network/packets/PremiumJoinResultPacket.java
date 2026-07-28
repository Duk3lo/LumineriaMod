package org.astral.lumineriabase.forge.network.packets;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkEvent;
import org.astral.lumineriabase.auth.ServerAuthManager;
import org.jetbrains.annotations.NotNull;

import java.util.function.Supplier;

public class PremiumJoinResultPacket {
    private final boolean attempted;
    public PremiumJoinResultPacket(boolean attempted) { this.attempted = attempted; }
    public PremiumJoinResultPacket(@NotNull FriendlyByteBuf buf) { this.attempted = buf.readBoolean(); }
    public void encode(@NotNull FriendlyByteBuf buf) { buf.writeBoolean(attempted); }

    public void handle(@NotNull Supplier<NetworkEvent.Context> ctxSupplier) {
        NetworkEvent.Context ctx = ctxSupplier.get();
        ServerPlayer player = ctx.getSender();
        ctx.enqueueWork(() -> {
            if (player != null) ServerAuthManager.onPremiumJoinResult(player, attempted);
        });
        ctx.setPacketHandled(true);
    }
}