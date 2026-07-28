package org.astral.lumineriabase.neoforge.network.payloads;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import org.astral.lumineriabase.Constants;
import org.jetbrains.annotations.NotNull;

public record PremiumJoinResultPayload(boolean attempted) implements CustomPacketPayload {
    public static final Type<PremiumJoinResultPayload> TYPE =
            new Type<>(ResourceLocation.fromNamespaceAndPath(Constants.MODID, "premium_join_result"));

    public static final StreamCodec<RegistryFriendlyByteBuf, PremiumJoinResultPayload> STREAM_CODEC =
            StreamCodec.composite(
                    StreamCodec.of(FriendlyByteBuf::writeBoolean, RegistryFriendlyByteBuf::readBoolean),
                    PremiumJoinResultPayload::attempted,
                    PremiumJoinResultPayload::new
            );

    @Override
    public @NotNull Type<? extends CustomPacketPayload> type() { return TYPE; }
}