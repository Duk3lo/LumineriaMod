package org.astral.lumineriabase.auth;

import net.minecraft.network.chat.Component;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.GameType;
import net.minecraft.world.phys.Vec3;
import org.astral.lumineriabase.platform.Services;
import org.jetbrains.annotations.NotNull;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class ServerAuthManager {
    public static final Map<UUID, PendingAuthData> pendingLogins = new ConcurrentHashMap<>();
    public record PendingAuthData(int joinTick, Vec3 pos, GameType previousGameMode) {}

    public static void onPlayerJoin(ServerPlayer player) {
        String ip = getPlayerIp(player);
        AuthDatabase.PlayerData data = AuthDatabase.getPlayer(player.getUUID());

        if (data != null && data.lastIp.equals(ip)) {
            long timePassed = System.currentTimeMillis() - data.lastLoginTime;
            if (timePassed < Services.PLATFORM.getSessionExpirationHours() * 3600000L) {
                AuthDatabase.updateSession(player.getUUID(), ip);
                player.sendSystemMessage(Component.literal("§aAuto-logueo exitoso."));
                Services.PLATFORM.sendOpenLogin(player, true, "CLOSE_AND_RELOAD");
                notifyVelocity(player, "LOGIN");
                return;
            }
        }
        GameType oldMode = player.gameMode.getGameModeForPlayer();
        pendingLogins.put(player.getUUID(), new PendingAuthData(player.server.getTickCount(), player.position(), oldMode));
        player.setGameMode(GameType.SPECTATOR);
        Services.PLATFORM.sendOpenLogin(player, data != null, "");
    }

    public static void onPlayerQuit(@NotNull ServerPlayer player) {
        pendingLogins.remove(player.getUUID());
        notifyVelocity(player, "LOGOUT");
    }

    public static void onServerTick(MinecraftServer server) {
        pendingLogins.forEach((uuid, data) -> {
            ServerPlayer player = server.getPlayerList().getPlayer(uuid);
            if (player != null && (server.getTickCount() - data.joinTick > Services.PLATFORM.getLoginTimeoutSeconds() * 20)) {
                player.connection.disconnect(Component.literal("§cTiempo agotado para loguearse."));
                pendingLogins.remove(uuid);
            }
        });
    }

    public static boolean isPending(UUID uuid) { return pendingLogins.containsKey(uuid); }

    public static void processLoginAttempt(@NotNull ServerPlayer player, String password) {
        if (!isPending(player.getUUID())) return;
        AuthDatabase.PlayerData data = AuthDatabase.getPlayer(player.getUUID());
        String ip = getPlayerIp(player);

        if (data == null) {
            AuthDatabase.savePlayer(player.getUUID(), password, ip);
            player.sendSystemMessage(Component.literal("§a¡Registrado con éxito!"));
            onSuccessfulLogin(player);
        } else {
            if (AuthDatabase.checkPassword(player.getUUID(), password)) {
                AuthDatabase.updateSession(player.getUUID(), ip);
                player.sendSystemMessage(Component.literal("§a¡Logueado con éxito!"));
                onSuccessfulLogin(player);
            } else {
                Services.PLATFORM.sendOpenLogin(player, true, "Contraseña incorrecta.");
            }
        }
    }

    private static void onSuccessfulLogin(@NotNull ServerPlayer player) {
        PendingAuthData data = pendingLogins.remove(player.getUUID());
        if (data != null) {
            player.teleportTo(data.pos().x, data.pos().y, data.pos().z);
            GameType targetMode = data.previousGameMode() == GameType.SPECTATOR || data.previousGameMode() == null ? GameType.SURVIVAL : data.previousGameMode();
            player.setGameMode(targetMode);
        }
        Services.PLATFORM.sendOpenLogin(player, true, "CLOSE_AND_RELOAD");
        notifyVelocity(player, "LOGIN");
    }

    private static String getPlayerIp(@NotNull ServerPlayer player) { return player.connection.getRemoteAddress().toString().substring(1).split(":")[0]; }
    private static void notifyVelocity(ServerPlayer player, String action) {
        Services.PLATFORM.sendAuthVelocity(player, action, player.getGameProfile().getName());
    }
}