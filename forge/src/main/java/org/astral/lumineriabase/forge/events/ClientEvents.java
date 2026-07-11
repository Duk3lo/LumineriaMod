package org.astral.lumineriabase.forge.events;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.toasts.SystemToast;
import net.minecraft.client.gui.components.toasts.Toast;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.ClientPlayerNetworkEvent;
import net.minecraftforge.client.event.ToastAddEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.astral.lumineriabase.Constants;
import org.astral.lumineriabase.client.ClientActionExecutor;
import org.jetbrains.annotations.NotNull;

@Mod.EventBusSubscriber(modid = Constants.MODID, value = Dist.CLIENT)
public class ClientEvents {

    @SubscribeEvent
    public static void onToastAdd(@NotNull ToastAddEvent event) {
        Toast toast = event.getToast();
        if (toast instanceof SystemToast systemToast) {
            if (systemToast.getToken() == SystemToast.SystemToastIds.UNSECURE_SERVER_WARNING) {
                event.setCanceled(true);
            }
        }
    }

    @SubscribeEvent
    public static void onClientJoin(ClientPlayerNetworkEvent.LoggingIn event) {
        Minecraft.getInstance().getToasts().clear();
    }

    @SubscribeEvent
    public static void onClientTick(TickEvent.ClientTickEvent event) {
        if (event.phase == TickEvent.Phase.END) {
            ClientActionExecutor.tickPendingLogin();
        }
    }
}