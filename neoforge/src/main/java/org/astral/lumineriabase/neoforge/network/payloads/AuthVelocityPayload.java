package org.astral.lumineriabase.neoforge.network.payloads;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import org.jetbrains.annotations.NotNull;

public record AuthVelocityPayload(String action, String playerName) implements CustomPacketPayload {


    public static final Type<AuthVelocityPayload> TYPE = new Type<>(ResourceLocation.fromNamespaceAndPath("authmevelocity", "main"));

    public static final StreamCodec<FriendlyByteBuf, AuthVelocityPayload> STREAM_CODEC = StreamCodec.of(
            (buf, payload) -> {
                ByteArrayDataOutput out = ByteStreams.newDataOutput();
                out.writeUTF(payload.action());
                out.writeUTF(payload.playerName());
                buf.writeBytes(out.toByteArray());
            },
            buf -> new AuthVelocityPayload("NONE", "UNKNOWN")
    );

    @Override
    public @NotNull Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}