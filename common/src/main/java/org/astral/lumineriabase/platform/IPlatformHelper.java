package org.astral.lumineriabase.platform;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.server.level.ServerPlayer;
import java.nio.file.Path;

public interface IPlatformHelper {
    Path getConfigDir();
    int getLoginTimeoutSeconds();
    int getSessionExpirationHours();

    void sendOpenLogin(ServerPlayer player, boolean isRegistered, String message);
    void sendAuthVelocity(ServerPlayer player, String action, String playerName);
    void renderBackground(Screen screen, GuiGraphics graphics, int mouseX, int mouseY, float partialTick);
}