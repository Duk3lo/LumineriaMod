package org.astral.lumineriabase.neoforge.network.payloads;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import org.astral.lumineriabase.Constants;
import org.jetbrains.annotations.NotNull;

public record PremiumChallengePayload(String serverId) implements CustomPacketPayload {
    public static final Type<PremiumChallengePayload> TYPE =
            new Type<>(ResourceLocation.fromNamespaceAndPath(Constants.MODID, "premium_challenge"));

    public static final StreamCodec<RegistryFriendlyByteBuf, PremiumChallengePayload> STREAM_CODEC =
            StreamCodec.composite(
                    ByteBufCodecs.stringUtf8(64),
                    PremiumChallengePayload::serverId,
                    PremiumChallengePayload::new
            );

    @Override
    public @NotNull Type<? extends CustomPacketPayload> type() { return TYPE; }
}