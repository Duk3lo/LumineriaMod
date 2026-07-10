package org.astral.lumineriabase.neoforge.network.payloads;

import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

public record AuthVelocityPayload(String action, String playerName) implements CustomPacketPayload {
    public static final Type<AuthVelocityPayload> TYPE = new Type<>(ResourceLocation.fromNamespaceAndPath("authmevelocity", "main"));
    public static final StreamCodec<ByteBuf, AuthVelocityPayload> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.STRING_UTF8, AuthVelocityPayload::action,
            ByteBufCodecs.STRING_UTF8, AuthVelocityPayload::playerName,
            AuthVelocityPayload::new
    );
    @Override public @NotNull Type<? extends CustomPacketPayload> type() { return TYPE; }
}