package org.astral.lumineriabase.neoforge.network.payloads;

import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import org.astral.lumineriabase.Constants;
import org.jetbrains.annotations.NotNull;

public record PresenceMaxPlayersPayload(int maxPlayers) implements CustomPacketPayload {
    public static final Type<PresenceMaxPlayersPayload> TYPE =
            new Type<>(ResourceLocation.fromNamespaceAndPath(Constants.MODID, "presence_max_players"));

    public static final StreamCodec<ByteBuf, PresenceMaxPlayersPayload> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.VAR_INT, PresenceMaxPlayersPayload::maxPlayers,
            PresenceMaxPlayersPayload::new
    );

    @Override
    public @NotNull Type<? extends CustomPacketPayload> type() { return TYPE; }
}
