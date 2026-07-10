package org.astral.lumineriabase.neoforge.network.payloads;

import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import org.astral.lumineriabase.Constants;
import org.jetbrains.annotations.NotNull;

public record OpenLoginScreenPayload(boolean isRegistered, String message) implements CustomPacketPayload {
    public static final Type<OpenLoginScreenPayload> TYPE = new Type<>(ResourceLocation.fromNamespaceAndPath(Constants.MODID, "open_login"));
    public static final StreamCodec<ByteBuf, OpenLoginScreenPayload> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.BOOL, OpenLoginScreenPayload::isRegistered,
            ByteBufCodecs.STRING_UTF8, OpenLoginScreenPayload::message,
            OpenLoginScreenPayload::new
    );
    @Override public @NotNull Type<? extends CustomPacketPayload> type() { return TYPE; }
}