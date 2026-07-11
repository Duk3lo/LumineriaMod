package org.astral.lumineriabase.forge.platform;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.fml.loading.FMLPaths;
import net.minecraftforge.network.PacketDistributor;
import org.astral.lumineriabase.platform.IPlatformHelper;
import org.astral.lumineriabase.forge.setup.ForgeConfig;
import org.astral.lumineriabase.forge.network.ForgeNetwork;
import org.astral.lumineriabase.forge.network.packets.OpenLoginScreenPacket;
import org.astral.lumineriabase.forge.network.packets.AuthVelocityPacket;
import org.jetbrains.annotations.NotNull;

import java.nio.file.Path;

@SuppressWarnings("unused")
public class ForgePlatformHelper implements IPlatformHelper {

    @Override
    public Path getConfigDir() {
        return FMLPaths.CONFIGDIR.get();
    }

    @Override
    public int getLoginTimeoutSeconds() {
        return ForgeConfig.loginTimeoutSeconds;
    }

    @Override
    public int getSessionExpirationHours() {
        return ForgeConfig.sessionExpirationHours;
    }

    @Override
    public void sendOpenLogin(ServerPlayer player, boolean isRegistered, String message) {
        ForgeNetwork.CHANNEL.send(PacketDistributor.PLAYER.with(() -> player), new OpenLoginScreenPacket(isRegistered, message));
    }

    @Override
    public void sendAuthVelocity(ServerPlayer player, String action, String playerName) {
        ForgeNetwork.CHANNEL.send(PacketDistributor.PLAYER.with(() -> player), new AuthVelocityPacket(action, playerName));
    }


    @Override
    public String getCurrentRoutingKey() {
        return ForgeConfig.routingKey;
    }

    @Override
    public void saveRoutingKey(String key) {
        ForgeConfig.routingKey = key;
        ForgeConfig.ROUTING_KEY_VALUE.set(key);
        ForgeConfig.CLIENT_SPEC.save();
    }

    @Override
    public void renderBackground(Object graphics, int mouseX, int mouseY, float partialTick) {
        if (graphics instanceof net.minecraft.client.gui.GuiGraphics guiGraphics) {
            if (net.minecraft.client.Minecraft.getInstance().screen != null) {
                net.minecraft.client.Minecraft.getInstance().screen.renderBackground(guiGraphics);
            }
        }
    }
}