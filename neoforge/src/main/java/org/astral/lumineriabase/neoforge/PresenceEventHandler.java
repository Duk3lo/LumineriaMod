package org.astral.lumineriabase.neoforge;

import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ClientPlayerNetworkEvent;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import org.astral.lumineriabase.Constants;
import org.astral.lumineriabase.presence.LauncherBridge;
import org.astral.lumineriabase.presence.PresenceConfig;

@EventBusSubscriber(modid = Constants.MODID, value = Dist.CLIENT)
public class PresenceEventHandler {

    private static int tickCounter = 0;
    private static final int UPDATE_INTERVAL_TICKS = 20 * 15;

    @SubscribeEvent
    public static void onLoggingIn(ClientPlayerNetworkEvent.LoggingIn event) {
        if (Minecraft.getInstance().hasSingleplayerServer()) return;
        LauncherBridge.start();
        tickCounter = UPDATE_INTERVAL_TICKS;
    }

    @SubscribeEvent
    public static void onLoggingOut(ClientPlayerNetworkEvent.LoggingOut event) {
        LauncherBridge.sendStatus(0, 0, null, null);
    }

    @SubscribeEvent
    public static void onClientTick(ClientTickEvent.Post event) {
        if (Minecraft.getInstance().level == null) return;

        tickCounter++;
        if (tickCounter >= UPDATE_INTERVAL_TICKS) {
            tickCounter = 0;
            sendCurrentStatus();
        }
    }

    private static void sendCurrentStatus() {
        ClientPacketListener connection = Minecraft.getInstance().getConnection();
        if (connection == null) return;

        int online = connection.getOnlinePlayers().size();
        LauncherBridge.sendStatus(online, PresenceConfig.SERVER_MAX_PLAYERS,
                PresenceConfig.SERVER_NAME, PresenceConfig.SERVER_ICON_URL);
    }
}