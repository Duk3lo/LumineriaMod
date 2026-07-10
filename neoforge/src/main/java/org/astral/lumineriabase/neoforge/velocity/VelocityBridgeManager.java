package org.astral.lumineriabase.neoforge.velocity;

import net.minecraft.server.level.ServerPlayer;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.ServerChatEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import org.astral.lumineriabase.Constants;
import org.astral.lumineriabase.neoforge.network.payloads.SignedVelocityPayload;
import org.astral.lumineriabase.velocity.queue.SignedQueue;
import org.astral.lumineriabase.velocity.queue.SignedResult;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.TimeUnit;

@EventBusSubscriber(modid = Constants.MODID)
public class VelocityBridgeManager {
    public static final SignedQueue chatQueue = new SignedQueue();
    public static final SignedQueue commandQueue = new SignedQueue();

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void onChat(ServerChatEvent event) {
        if (isLocal()) return;
        if (event.isCanceled()) return;
        event.setCanceled(true);

        ServerPlayer player = event.getPlayer();
        chatQueue.dataFrom(player.getUUID()).nextResult()
                .orTimeout(3, TimeUnit.SECONDS)
                .thenAccept(result -> {

                });
    }

    @SubscribeEvent
    public static void onQuit(PlayerEvent.@NotNull PlayerLoggedOutEvent event) {
        chatQueue.removeData(event.getEntity().getUUID());
        commandQueue.removeData(event.getEntity().getUUID());
    }
    public static void handlePayload(final @NotNull SignedVelocityPayload payload, @SuppressWarnings("unused") final IPayloadContext context) {
        SignedQueue queue;

        if ("COMMAND".equalsIgnoreCase(payload.source())) {
            queue = commandQueue;
        } else if ("CHAT".equalsIgnoreCase(payload.source())) {
            queue = chatQueue;
        } else {
            return;
        }

        SignedResult signedResult;
        if ("CANCEL".equalsIgnoreCase(payload.result())) {
            signedResult = SignedResult.cancel();
        } else if ("MODIFY".equalsIgnoreCase(payload.result())) {
            signedResult = SignedResult.modify(payload.modified());
        } else {
            signedResult = SignedResult.allowed();
        }

        queue.dataFrom(payload.playerId()).complete(signedResult);
    }

    private static boolean isLocal() {
        for (StackTraceElement element : Thread.currentThread().getStackTrace()) {
            String methodName = element.getMethodName();
            String className = element.getClassName();
            if (methodName.contains("handleChat") ||
                    className.contains("PlayerConnection") ||
                    className.contains("ServerGamePacketListenerImpl")) {
                return false;
            }
        }
        return true;
    }
}