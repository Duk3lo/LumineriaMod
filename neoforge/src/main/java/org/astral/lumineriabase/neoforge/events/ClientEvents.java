package org.astral.lumineriabase.neoforge.events;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.toasts.SystemToast;
import net.minecraft.client.gui.components.toasts.Toast;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ClientPlayerNetworkEvent;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.neoforge.client.event.ToastAddEvent;
import net.neoforged.neoforge.network.PacketDistributor;
import org.astral.lumineriabase.Constants;
import org.astral.lumineriabase.client.ClientActionExecutor;
import org.astral.lumineriabase.neoforge.network.payloads.RouterHandshakePayload;
import org.astral.lumineriabase.neoforge.setup.NeoForgeConfig;
import org.jetbrains.annotations.NotNull;

@EventBusSubscriber(modid = Constants.MODID, value = Dist.CLIENT)
public class ClientEvents {

    @SubscribeEvent
    public static void onToastAdd(@NotNull ToastAddEvent event) {
        Toast toast = event.getToast();
        if (toast instanceof SystemToast systemToast) {
            if (systemToast.getToken() == SystemToast.SystemToastId.UNSECURE_SERVER_WARNING) {
                event.setCanceled(true);
            }
        }
    }

    @SubscribeEvent
    public static void onClientJoin(ClientPlayerNetworkEvent.LoggingIn event) {
        Minecraft.getInstance().getToasts().clear();
        PacketDistributor.sendToServer(new RouterHandshakePayload(NeoForgeConfig.routingKey));
    }

    @SubscribeEvent
    public static void onClientTick(ClientTickEvent.Post event) {
        ClientActionExecutor.tickPendingLogin();
    }
}