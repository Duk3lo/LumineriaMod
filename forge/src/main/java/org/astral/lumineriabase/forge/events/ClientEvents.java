package org.astral.lumineriabase.forge.events;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import io.netty.buffer.Unpooled;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.toasts.SystemToast;
import net.minecraft.client.gui.components.toasts.Toast;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.ServerboundCustomPayloadPacket;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.ClientPlayerNetworkEvent;
import net.minecraftforge.client.event.RegisterClientCommandsEvent;
import net.minecraftforge.client.event.ToastAddEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.astral.lumineriabase.Constants;
import org.astral.lumineriabase.client.ClientActionExecutor;
import org.astral.lumineriabase.client.RoutingConfigScreen;
import org.astral.lumineriabase.forge.setup.ForgeConfig;
import org.astral.lumineriabase.platform.Services;
import org.jetbrains.annotations.NotNull;

import java.nio.charset.StandardCharsets;

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
        FriendlyByteBuf buf = new FriendlyByteBuf(Unpooled.buffer());
        buf.writeBytes(ForgeConfig.routingKey.getBytes(StandardCharsets.UTF_8));
        ResourceLocation channel = ResourceLocation.fromNamespaceAndPath("lumineriabase", "router");
        if (Minecraft.getInstance().getConnection() != null) {
            Minecraft.getInstance().getConnection().send(new ServerboundCustomPayloadPacket(channel, buf));
        }
    }

    @SubscribeEvent
    public static void onClientCommandRegister(@NotNull RegisterClientCommandsEvent event) {
        event.getDispatcher().register(LiteralArgumentBuilder.<CommandSourceStack>literal("routerconfig")
                .executes(c -> {
                    Minecraft.getInstance().execute(() -> Minecraft.getInstance().setScreen(new RoutingConfigScreen(null)));
                    return 1;
                }));
    }

    @SubscribeEvent
    public static void onClientTick(TickEvent.@NotNull ClientTickEvent event) {
        if (event.phase == TickEvent.Phase.END) {
            ClientActionExecutor.tickPendingLogin();
        }
    }

    public static void handleSyncPacket(byte[] data) {
        String newKey = new String(data, StandardCharsets.UTF_8).trim();
        if (!newKey.isEmpty() && !newKey.equals(ForgeConfig.routingKey)) {
            Services.PLATFORM.saveRoutingKey(newKey);
            Minecraft mc = Minecraft.getInstance();
            mc.execute(() -> {
                if (mc.player != null) {
                    mc.player.displayClientMessage(
                            Component.literal("§d§l[Lumineria] §7Tu ruta ha sido sincronizada a: §f" + newKey),
                            false
                    );
                }
            });
        }
    }
}