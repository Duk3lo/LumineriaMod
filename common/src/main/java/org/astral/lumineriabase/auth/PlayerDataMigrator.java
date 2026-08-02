package org.astral.lumineriabase.auth;

import com.mojang.logging.LogUtils;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.level.storage.LevelResource;
import org.slf4j.Logger;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

/**
 * Mueve/copia los archivos de un jugador (playerdata, stats, advancements) entre dos UUIDs
 * dentro de la carpeta del mundo. Requiere que el jugador origen NO esté conectado,
 * porque mientras está online Minecraft mantiene sus datos en memoria y los vuelve a
 * escribir en disco al salir, pisando cualquier cambio hecho "por debajo".
 *
 * Por eso: NO se llama a esto mientras el jugador está en línea. Se ejecuta con el
 * jugador desconectado, y luego, si necesita reconectarse, sus datos ya estarán en el
 * lugar correcto. No hace falta reiniciar el servidor.
 */
public final class PlayerDataMigrator {
    private static final Logger LOGGER = LogUtils.getLogger();

    private PlayerDataMigrator() {}

    /** Fusiona los archivos del jugador no-premium (rivalUuid) hacia la cuenta premium (premiumUuid). */
    public static void mergeIntoPremium(MinecraftServer server, UUID rivalUuid, UUID premiumUuid, String username) {
        if (server.getPlayerList().getPlayer(rivalUuid) != null) {
            LOGGER.warn("[Lumineria] Se intentó fusionar datos de {} mientras seguía conectado, se abortó.", rivalUuid);
            return;
        }

        Path worldDir = getWorldRoot(server);
        boolean ok = copyPlayerFiles(worldDir, rivalUuid, premiumUuid);

        if (ok) {
            AuthDatabase.deleteAccount(rivalUuid);
            AuthDatabase.touchUsername(premiumUuid, username);
            LOGGER.info("[Lumineria] Datos de {} fusionados en la cuenta premium {}", rivalUuid, premiumUuid);
        }
    }

    /**
     * Renombra la cuenta rival (no-premium) generando un nuevo UUID offline a partir del nuevo nick,
     * y libera el nombre original para que quede disponible para la cuenta premium.
     */
    public static void renameRivalAccount(MinecraftServer server, UUID rivalUuid, String newNick) {
        if (server.getPlayerList().getPlayer(rivalUuid) != null) {
            LOGGER.warn("[Lumineria] Se intentó renombrar a {} mientras seguía conectado, se abortó.", rivalUuid);
            return;
        }

        UUID newOfflineUuid = offlineUuidFor(newNick);
        Path worldDir = getWorldRoot(server);
        boolean ok = copyPlayerFiles(worldDir, rivalUuid, newOfflineUuid);

        if (ok) {
            AuthDatabase.renameAccount(rivalUuid, newOfflineUuid, newNick);
            LOGGER.info("[Lumineria] Cuenta {} renombrada a {} ({})", rivalUuid, newNick, newOfflineUuid);
        }
    }

    /** El mismo algoritmo que usa un servidor en modo offline para generar el UUID a partir de un nombre. */
    public static UUID offlineUuidFor(String username) {
        return UUID.nameUUIDFromBytes(("OfflinePlayer:" + username).getBytes(StandardCharsets.UTF_8));
    }

    private static Path getWorldRoot(MinecraftServer server) {
        // PLAYER_DATA_DIR apunta a "<mundo>/playerdata"; su padre es la carpeta raíz del mundo.
        return server.getWorldPath(LevelResource.PLAYER_DATA_DIR).getParent();
    }

    private static boolean copyPlayerFiles(Path worldDir, UUID from, UUID to) {
        try {
            copyIfExists(worldDir.resolve("playerdata").resolve(from + ".dat"), worldDir.resolve("playerdata").resolve(to + ".dat"));
            copyIfExists(worldDir.resolve("playerdata").resolve(from + ".dat_old"), worldDir.resolve("playerdata").resolve(to + ".dat_old"));
            copyIfExists(worldDir.resolve("stats").resolve(from + ".json"), worldDir.resolve("stats").resolve(to + ".json"));
            copyIfExists(worldDir.resolve("advancements").resolve(from + ".json"), worldDir.resolve("advancements").resolve(to + ".json"));
            return true;
        } catch (IOException e) {
            LOGGER.error("[Lumineria] Error copiando archivos de jugador de {} a {}", from, to, e);
            return false;
        }
    }

    private static void copyIfExists(Path source, Path target) throws IOException {
        if (Files.exists(source)) {
            Files.createDirectories(target.getParent());
            Files.copy(source, target, StandardCopyOption.REPLACE_EXISTING);
        }
    }
}
