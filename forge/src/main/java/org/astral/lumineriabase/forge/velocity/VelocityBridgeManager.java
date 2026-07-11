package org.astral.lumineriabase.forge.velocity;

import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.event.ServerChatEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.astral.lumineriabase.Constants;
import org.astral.lumineriabase.velocity.queue.SignedQueue;
import org.astral.lumineriabase.velocity.queue.SignedResult;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Mod.EventBusSubscriber(modid = Constants.MODID)
public class VelocityBridgeManager {
    public static final SignedQueue chatQueue = new SignedQueue();
    public static final SignedQueue commandQueue = new SignedQueue();

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void onChat(@NotNull ServerChatEvent event) {
        if (event.isCanceled()) return;
        event.setCanceled(true);

        ServerPlayer player = event.getPlayer();
        chatQueue.dataFrom(player.getUUID()).nextResult()
                .orTimeout(3, TimeUnit.SECONDS)
                .thenAccept(result -> {
                    // Bloque vacío igual que en NeoForge
                });
    }

    @SubscribeEvent
    public static void onQuit(PlayerEvent.@NotNull PlayerLoggedOutEvent event) {
        chatQueue.removeData(event.getEntity().getUUID());
        commandQueue.removeData(event.getEntity().getUUID());
    }


    public static void handlePayload(UUID playerId, String source, String resultString, String modified) {
        SignedQueue queue;

        if ("COMMAND".equalsIgnoreCase(source)) {
            queue = commandQueue;
        } else if ("CHAT".equalsIgnoreCase(source)) {
            queue = chatQueue;
        } else {
            return;
        }

        SignedResult signedResult;
        if ("CANCEL".equalsIgnoreCase(resultString)) {
            signedResult = SignedResult.cancel();
        } else if ("MODIFY".equalsIgnoreCase(resultString)) {
            signedResult = SignedResult.modify(modified);
        } else {
            signedResult = SignedResult.allowed();
        }

        queue.dataFrom(playerId).complete(signedResult);
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