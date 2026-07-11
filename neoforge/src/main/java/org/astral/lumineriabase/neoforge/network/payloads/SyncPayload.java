package org.astral.lumineriabase.neoforge.network.payloads;

import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

import java.nio.charset.StandardCharsets;

public record SyncPayload(String key) implements CustomPacketPayload {
    public static final Type<SyncPayload> TYPE = new Type<>(ResourceLocation.fromNamespaceAndPath("lumineriabase", "sync"));

    public static final StreamCodec<ByteBuf, SyncPayload> STREAM_CODEC = StreamCodec.of(
            (buf, p) -> {
                byte[] bytes = p.key().getBytes(StandardCharsets.UTF_8);
                buf.writeBytes(bytes);
            },
            buf -> {
                int readable = buf.readableBytes();
                CharSequence charSequence = buf.readCharSequence(readable, StandardCharsets.UTF_8);
                return new SyncPayload(charSequence.toString().trim());
            }
    );

    @Override
    public @NotNull Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}