package org.astral.lumineriabase.neoforge.network.payloads;

import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

import java.nio.charset.StandardCharsets;

public record RouterHandshakePayload(String key) implements CustomPacketPayload {
    public static final Type<RouterHandshakePayload> TYPE = new Type<>(ResourceLocation.fromNamespaceAndPath("lumineriabase", "router"));
    public static final StreamCodec<ByteBuf, RouterHandshakePayload> STREAM_CODEC = StreamCodec.of(
            (buf, p) -> buf.writeBytes(p.key().getBytes(StandardCharsets.UTF_8)),
            buf -> new RouterHandshakePayload(buf.toString(StandardCharsets.UTF_8))
    );
    @Override public @NotNull Type<? extends CustomPacketPayload> type() { return TYPE; }
}