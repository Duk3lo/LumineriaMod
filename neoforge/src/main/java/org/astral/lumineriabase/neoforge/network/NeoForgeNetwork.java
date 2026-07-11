package org.astral.lumineriabase.neoforge.network;

import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.handling.DirectionalPayloadHandler;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;
import org.astral.lumineriabase.Constants;
import org.astral.lumineriabase.auth.ServerAuthManager;
import org.astral.lumineriabase.client.ClientActionExecutor;
import org.astral.lumineriabase.neoforge.network.payloads.*;

import org.astral.lumineriabase.neoforge.velocity.VelocityBridgeManager;
import org.jetbrains.annotations.NotNull;

public class NeoForgeNetwork {

    public static void register(final @NotNull RegisterPayloadHandlersEvent event) {
        final PayloadRegistrar registrar = event.registrar(Constants.MODID);

        registrar.playBidirectional(
                RouterHandshakePayload.TYPE,
                RouterHandshakePayload.STREAM_CODEC,
                new DirectionalPayloadHandler<>(
                        (payload, context) -> context.enqueueWork(() -> {
                            if (!payload.key().equals(org.astral.lumineriabase.neoforge.setup.NeoForgeConfig.routingKey)) {
                                org.astral.lumineriabase.platform.Services.PLATFORM.saveRoutingKey(payload.key());
                            }
                        }),
                        (payload, context) -> {}
                )
        );

        registrar.playToClient(
                OpenLoginScreenPayload.TYPE,
                OpenLoginScreenPayload.STREAM_CODEC,
                (payload, context) -> context.enqueueWork(() -> {
                    if ("CLOSE_AND_RELOAD".equals(payload.message())) {
                        ClientActionExecutor.clearPendingLogin();
                    } else {
                        ClientActionExecutor.openLoginScreen(payload.isRegistered(), payload.message(), (password) -> PacketDistributor.sendToServer(new ClientLoginAttemptPayload(password)));
                    }
                })
        );

        registrar.playToServer(
                ClientLoginAttemptPayload.TYPE,
                ClientLoginAttemptPayload.STREAM_CODEC,
                (payload, context) -> context.enqueueWork(() -> {
                    if (context.player() instanceof ServerPlayer serverPlayer) {
                        ServerAuthManager.processLoginAttempt(serverPlayer, payload.password());
                    }
                })
        );

        registrar.playToClient(
                AuthVelocityPayload.TYPE,
                AuthVelocityPayload.STREAM_CODEC,
                (payload, context) -> {}
        );

        registrar.playToServer(
                SignedVelocityPayload.TYPE,
                SignedVelocityPayload.STREAM_CODEC,
                VelocityBridgeManager::handlePayload
        );

        registrar.playToClient(
                SyncPayload.TYPE,
                SyncPayload.STREAM_CODEC,
                (payload, context) -> context.enqueueWork(() -> {
                    String newKey = payload.key().trim();
                    if (!newKey.isEmpty() && !newKey.equals(org.astral.lumineriabase.neoforge.setup.NeoForgeConfig.routingKey)) {
                        org.astral.lumineriabase.platform.Services.PLATFORM.saveRoutingKey(newKey);
                        if (net.minecraft.client.Minecraft.getInstance().player != null) {
                            net.minecraft.client.Minecraft.getInstance().player.displayClientMessage(
                                    net.minecraft.network.chat.Component.literal("§d§l[Lumineria] §7Tu ruta ha sido sincronizada automáticamente a: §f" + newKey),
                                    false
                            );
                        }
                    }
                })
        );
    }
}