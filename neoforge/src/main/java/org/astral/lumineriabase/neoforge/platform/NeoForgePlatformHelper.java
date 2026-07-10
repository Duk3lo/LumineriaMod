package org.astral.lumineriabase.neoforge.platform;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.fml.loading.FMLPaths;
import net.neoforged.neoforge.network.PacketDistributor;
import org.astral.lumineriabase.platform.IPlatformHelper;
import org.astral.lumineriabase.neoforge.setup.NeoForgeConfig;
import org.astral.lumineriabase.neoforge.network.payloads.OpenLoginScreenPayload;
import org.astral.lumineriabase.neoforge.network.payloads.AuthVelocityPayload;
import org.jetbrains.annotations.NotNull;

import java.nio.file.Path;

@SuppressWarnings("unused")
public class NeoForgePlatformHelper implements IPlatformHelper {

    @Override
    public Path getConfigDir() {
        return FMLPaths.CONFIGDIR.get();
    }

    @Override
    public int getLoginTimeoutSeconds() {
        return NeoForgeConfig.loginTimeoutSeconds;
    }

    @Override
    public int getSessionExpirationHours() {
        return NeoForgeConfig.sessionExpirationHours;
    }

    @Override
    public void sendOpenLogin(ServerPlayer player, boolean isRegistered, String message) {
        PacketDistributor.sendToPlayer(player, new OpenLoginScreenPayload(isRegistered, message));
    }

    @Override
    public void sendAuthVelocity(ServerPlayer player, String action, String playerName) {
        PacketDistributor.sendToPlayer(player, new AuthVelocityPayload(action, playerName));
    }

    @Override
    public void renderBackground(@NotNull Screen screen, GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        screen.renderBackground(graphics, mouseX, mouseY, partialTick);
    }
}