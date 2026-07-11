package org.astral.lumineriabase.platform;

import net.minecraft.server.level.ServerPlayer;
import java.nio.file.Path;

public interface IPlatformHelper {
    Path getConfigDir();
    int getLoginTimeoutSeconds();
    int getSessionExpirationHours();
    String getCurrentRoutingKey();
    void saveRoutingKey(String key);
    void sendOpenLogin(ServerPlayer player, boolean isRegistered, String message);
    void sendAuthVelocity(ServerPlayer player, String action, String playerName);
    void renderBackground(Object graphics, int mouseX, int mouseY, float partialTick);
}