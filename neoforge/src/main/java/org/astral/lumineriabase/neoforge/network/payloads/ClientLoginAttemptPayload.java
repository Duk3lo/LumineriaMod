package org.astral.lumineriabase.neoforge.network.payloads;

import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import org.astral.lumineriabase.Constants;
import org.jetbrains.annotations.NotNull;

public record ClientLoginAttemptPayload(String password) implements CustomPacketPayload {
    public static final Type<ClientLoginAttemptPayload> TYPE = new Type<>(ResourceLocation.fromNamespaceAndPath(Constants.MODID, "client_login_attempt"));
    public static final StreamCodec<ByteBuf, ClientLoginAttemptPayload> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.STRING_UTF8, ClientLoginAttemptPayload::password,
            ClientLoginAttemptPayload::new
    );
    @Override public @NotNull Type<? extends CustomPacketPayload> type() { return TYPE; }
}