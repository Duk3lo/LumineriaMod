package org.astral.lumineriabase.client;

import net.minecraft.client.Minecraft;
import java.util.function.Consumer;

public class ClientActionExecutor {
    private static boolean hasPendingLogin = false;
    private static boolean pendingIsReg = false;
    private static String pendingMessage = "";
    private static int renderDistanceTimer = -1;

    private static Consumer<String> pendingLoginAction = null;

    public static void openLoginScreen(boolean isReg, String message, Consumer<String> onLoginAttempt) {
        hasPendingLogin = true;
        pendingIsReg = isReg;
        pendingMessage = message;
        pendingLoginAction = onLoginAttempt;
    }

    public static void clearPendingLogin() {
        hasPendingLogin = false;
        pendingLoginAction = null;
        Minecraft mc = Minecraft.getInstance();

        mc.execute(() -> {
            if (mc.screen instanceof LoginScreen) {
                mc.setScreen(null);
            }
            renderDistanceTimer = 40;
        });
    }

    public static void tickPendingLogin() {
        Minecraft mc = Minecraft.getInstance();

        if (hasPendingLogin) {
            if (mc.level != null && mc.screen == null && pendingLoginAction != null) {
                mc.setScreen(new LoginScreen(pendingIsReg, pendingMessage, pendingLoginAction));
            }
        }

        if (renderDistanceTimer > 0) {
            renderDistanceTimer--;
            if (renderDistanceTimer == 0 && mc.level != null) {
                mc.options.renderDistance().set(12);
                mc.options.save();
                mc.levelRenderer.allChanged();
            }
        }
    }
}