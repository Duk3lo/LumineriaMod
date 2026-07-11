package org.astral.lumineriabase.forge.network.packets;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;
import org.astral.lumineriabase.forge.velocity.VelocityBridgeManager;
import org.jetbrains.annotations.NotNull;
import com.mojang.logging.LogUtils;
import org.slf4j.Logger;

import java.util.UUID;
import java.util.function.Supplier;

public class SignedVelocityPacket {
    private static final Logger LOGGER = LogUtils.getLogger();
    private final UUID playerId;
    private final String source;
    private final String result;
    private final String modified;

    public SignedVelocityPacket(@NotNull FriendlyByteBuf buf) {
        this.playerId = UUID.fromString(buf.readUtf());
        this.source = buf.readUtf();
        this.result = buf.readUtf();
        this.modified = "MODIFY".equalsIgnoreCase(this.result) ? buf.readUtf() : null;
    }

    public void encode(@NotNull FriendlyByteBuf buf) {
        buf.writeUtf(playerId.toString());
        buf.writeUtf(source);
        buf.writeUtf(result);
        if (modified != null) buf.writeUtf(modified);
    }

    public void handle(@NotNull Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            LOGGER.info("[VelocityBridge] Recibido resultado de {}: {} para el jugador {}",
                    source, result, playerId);

            VelocityBridgeManager.handlePayload(this.playerId, this.source, this.result, this.modified);
        });
        ctx.get().setPacketHandled(true);
    }
}