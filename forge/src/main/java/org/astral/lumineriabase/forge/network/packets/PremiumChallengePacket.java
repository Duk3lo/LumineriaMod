package org.astral.lumineriabase.forge.network.packets;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;
import org.astral.lumineriabase.client.ClientPremiumVerifier;
import org.astral.lumineriabase.forge.network.ForgeNetwork;
import org.jetbrains.annotations.NotNull;

import java.util.function.Supplier;

public class PremiumChallengePacket {
    private final String serverId;
    public PremiumChallengePacket(String serverId) { this.serverId = serverId; }
    public PremiumChallengePacket(@NotNull FriendlyByteBuf buf) { this.serverId = buf.readUtf(64); }
    public void encode(@NotNull FriendlyByteBuf buf) { buf.writeUtf(serverId, 64); }

    public void handle(@NotNull Supplier<NetworkEvent.Context> ctxSupplier) {
        NetworkEvent.Context ctx = ctxSupplier.get();
        ctx.enqueueWork(() -> {
            if (ClientPremiumVerifier.isMsaSession()) {
                ClientPremiumVerifier.attemptJoinAsync(serverId,
                        attempted -> ForgeNetwork.CHANNEL.sendToServer(new PremiumJoinResultPacket(attempted)));
            } else {
                ForgeNetwork.CHANNEL.sendToServer(new PremiumJoinResultPacket(false));
            }
        });
        ctx.setPacketHandled(true);
    }
}