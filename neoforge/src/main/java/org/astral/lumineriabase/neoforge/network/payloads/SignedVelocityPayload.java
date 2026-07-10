package org.astral.lumineriabase.neoforge.network.payloads;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufInputStream;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.DataInputStream;
import java.io.IOException;
import java.util.UUID;

public record SignedVelocityPayload(UUID playerId, String source, String result, @Nullable String modified) implements CustomPacketPayload {


    public static final Type<SignedVelocityPayload> TYPE = new Type<>(ResourceLocation.fromNamespaceAndPath("signedvelocity", "main"));

    public static final StreamCodec<ByteBuf, SignedVelocityPayload> STREAM_CODEC = StreamCodec.of(
            (buf, payload) -> {  },
            buf -> {
                try (DataInputStream in = new DataInputStream(new ByteBufInputStream(buf))) {
                    UUID uuid = UUID.fromString(in.readUTF());
                    String source = in.readUTF();
                    String result = in.readUTF();
                    String modified = null;
                    if ("MODIFY".equalsIgnoreCase(result)) {
                        modified = in.readUTF();
                    }
                    return new SignedVelocityPayload(uuid, source, result, modified);
                } catch (IOException e) {
                    throw new RuntimeException("Error decodificando SignedVelocityPayload", e);
                }
            }
    );

    @Override
    public @NotNull Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}