package org.astral.lumineriabase.client;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.minecraft.MinecraftSessionService;
import net.minecraft.client.Minecraft;
import net.minecraft.client.User;
import org.slf4j.Logger;
import com.mojang.logging.LogUtils;

import java.lang.reflect.Method;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Consumer;

public class ClientPremiumVerifier {
    private static final Logger LOGGER = LogUtils.getLogger();
    private static final ExecutorService IO_POOL = Executors.newSingleThreadExecutor(r -> {
        Thread t = new Thread(r, "lumineria-premium-join");
        t.setDaemon(true);
        return t;
    });

    public static boolean isMsaSession() {
        try {
            User user = Minecraft.getInstance().getUser();
            String token = user.getAccessToken();

            boolean isPremium = token.length() > 50;
            LOGGER.info("[Lumineria] ¿Es sesión premium en cliente?: {} (Longitud: {})",
                    isPremium ? "SÍ" : "NO", token.length());
            return isPremium;
        } catch (Throwable t) {
            LOGGER.error("[Lumineria] Error al evaluar la sesión en el cliente", t);
            return false;
        }
    }

    public static void attemptJoinAsync(String serverId, Consumer<Boolean> onDone) {
        Minecraft mc = Minecraft.getInstance();
        User user = mc.getUser();

        CompletableFuture.supplyAsync(() -> {
                    try {
                        LOGGER.info("[Lumineria] Iniciando validación de sesión para serverId: {}", serverId);

                        UUID uuid = user.getProfileId();
                        String name = user.getName();
                        String token = user.getAccessToken();

                        if (uuid == null) {
                            LOGGER.warn("[Lumineria] Datos de usuario inválidos (nulos).");
                            return false;
                        }

                        MinecraftSessionService sessionService = mc.getMinecraftSessionService();

                        LOGGER.info("[Lumineria] Buscando método joinServer compatible por reflexión...");

                        Method targetMethod = null;
                        Object firstParam = null;

                        for (Method m : sessionService.getClass().getMethods()) {
                            if (m.getName().equals("joinServer") && m.getParameterCount() == 3) {
                                Class<?>[] params = m.getParameterTypes();
                                if (params[1].equals(String.class) && params[2].equals(String.class)) {
                                    if (params[0].equals(UUID.class)) {
                                        targetMethod = m;
                                        firstParam = uuid;
                                        LOGGER.info("[Lumineria] Detectada firma moderna de joinServer(UUID, String, String)");
                                        break;
                                    } else if (params[0].equals(GameProfile.class)) {
                                        targetMethod = m;
                                        firstParam = new GameProfile(uuid, name);
                                        LOGGER.info("[Lumineria] Detectada firma clásica de joinServer(GameProfile, String, String)");
                                        break;
                                    }
                                }
                            }
                        }

                        if (targetMethod == null) {
                            throw new NoSuchMethodException("No se encontró ninguna firma compatible para joinServer en MinecraftSessionService.");
                        }

                        LOGGER.info("[Lumineria] Contactando a Mojang de forma dinámica...");
                        targetMethod.invoke(sessionService, firstParam, token, serverId);

                        LOGGER.info("[Lumineria] ¡Mojang ha validado el token correctamente!");
                        return true;
                    } catch (Throwable t) {
                        Throwable cause = t instanceof java.lang.reflect.InvocationTargetException ? t.getCause() : t;
                        LOGGER.error("[Lumineria] La validación con Mojang falló o fue rechazada: " + cause.getMessage(), cause);
                        return false;
                    }
                }, IO_POOL)
                .handle((result, error) -> {
                    if (error != null) {
                        LOGGER.error("[Lumineria] Error inesperado en el proceso asíncrono", error);
                        return false;
                    }
                    return result != null && result;
                })
                .thenAccept(onDone);
    }
}