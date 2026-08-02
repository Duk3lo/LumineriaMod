package org.astral.lumineriabase.auth;

import net.minecraft.network.chat.ClickEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.HoverEvent;
import net.minecraft.network.chat.Style;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Resuelve los conflictos de nombre entre cuentas premium y no-premium.
 *
 * Caso 1 (bloqueo): un no-premium intenta loguearse con un nombre que ya
 * pertenece a una cuenta premium verificada -> se rechaza la conexión.
 *
 * Caso 2 (migración asistida): una cuenta premium se verifica con un nombre
 * que ya tiene datos guardados de una cuenta no-premium -> se le pregunta al
 * jugador premium si quiere quedarse con esos datos o mudarlos a otro nick,
 * liberando el nombre.
 */
public final class NameConflictManager {

    private record PendingConflict(UUID premiumUuid, String username, UUID rivalUuid) {}
    private static final Map<UUID, PendingConflict> pendingDecisions = new ConcurrentHashMap<>();

    private NameConflictManager() {}

    /**
     * Llamar justo ANTES de aceptar un login/registro NO premium
     * (al comienzo del flujo donde hoy decides mostrar el LoginScreen).
     *
     * @return true si la conexión fue rechazada y no se debe continuar el login normal.
     */
    public static boolean blockIfNamePremiumTaken(ServerPlayer player) {
        String username = player.getGameProfile().getName();
        UUID uuid = player.getUUID();

        UUID premiumOwner = AuthDatabase.findPremiumOwnerOfName(username, uuid);
        if (premiumOwner != null) {
            player.connection.disconnect(Component.literal(
                    "§c§lLumineria §7» §fYa existe una cuenta §bpremium§f registrada con el nombre §e" + username + "§f.\n" +
                            "§7Conéctate con otro nombre de usuario."
            ));
            return true;
        }
        return false;
    }

    /**
     * Llamar justo DESPUÉS de confirmar que la sesión premium fue validada por Mojang
     * (en el punto donde procesas el resultado positivo del PremiumJoinResult).
     */
    public static void checkForRivalNonPremiumData(ServerPlayer player) {
        String username = player.getGameProfile().getName();
        UUID premiumUuid = player.getUUID();

        UUID rivalUuid = AuthDatabase.findNonPremiumOwnerOfName(username, premiumUuid);
        if (rivalUuid == null) return;

        pendingDecisions.put(premiumUuid, new PendingConflict(premiumUuid, username, rivalUuid));
        sendChoicePrompt(player, username);
    }

    private static void sendChoicePrompt(ServerPlayer player, String username) {
        player.sendSystemMessage(Component.literal(
                "§d§l[Lumineria] §7Se detectó una cuenta §fno-premium§7 con datos guardados bajo el nombre §e" + username + "§7."));
        player.sendSystemMessage(Component.literal("§7¿Qué querés hacer con esos datos?"));

        Component keep = Component.literal("§a » [Conservar esos datos en mi cuenta premium]")
                .setStyle(Style.EMPTY
                        .withClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/lumineria migrar conservar"))
                        .withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT,
                                Component.literal("Fusiona el progreso guardado a tu cuenta premium"))));

        Component move = Component.literal("§e » [Mudar esos datos a otro nick]")
                .setStyle(Style.EMPTY
                        .withClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, "/lumineria migrar mudar "))
                        .withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT,
                                Component.literal("Click y escribe el nuevo nick para esos datos"))));

        player.sendSystemMessage(keep);
        player.sendSystemMessage(move);
    }

    public static boolean hasPendingDecision(UUID premiumUuid) {
        return pendingDecisions.containsKey(premiumUuid);
    }

    public static void resolveKeepInPremium(ServerPlayer premiumPlayer) {
        PendingConflict conflict = pendingDecisions.remove(premiumPlayer.getUUID());
        if (conflict == null) {
            premiumPlayer.sendSystemMessage(Component.literal("§cNo hay ninguna migración pendiente."));
            return;
        }
        MinecraftServer server = premiumPlayer.getServer();
        if (server == null) return;

        server.execute(() -> {
            PlayerDataMigrator.mergeIntoPremium(server, conflict.rivalUuid(), conflict.premiumUuid(), conflict.username());
            premiumPlayer.sendSystemMessage(Component.literal("§a[Lumineria] Los datos fueron fusionados a tu cuenta premium."));
        });
    }

    public static void resolveMoveToNewNick(ServerPlayer premiumPlayer, String newNick) {
        PendingConflict conflict = pendingDecisions.remove(premiumPlayer.getUUID());
        if (conflict == null) {
            premiumPlayer.sendSystemMessage(Component.literal("§cNo hay ninguna migración pendiente."));
            return;
        }
        if (newNick == null || newNick.isBlank() || newNick.equalsIgnoreCase(conflict.username())) {
            premiumPlayer.sendSystemMessage(Component.literal("§cNombre inválido para la migración."));
            pendingDecisions.put(premiumPlayer.getUUID(), conflict);
            return;
        }
        MinecraftServer server = premiumPlayer.getServer();
        if (server == null) return;

        server.execute(() -> {
            PlayerDataMigrator.renameRivalAccount(server, conflict.rivalUuid(), newNick);
            premiumPlayer.sendSystemMessage(Component.literal(
                    "§a[Lumineria] Los datos antiguos ahora viven bajo el nick §e" + newNick + "§a. Tu nombre quedó libre."));
        });
    }
}
