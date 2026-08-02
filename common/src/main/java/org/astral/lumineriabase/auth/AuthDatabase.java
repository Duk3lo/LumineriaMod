package org.astral.lumineriabase.auth;

import com.mojang.logging.LogUtils;
import org.astral.lumineriabase.Constants;
import org.astral.lumineriabase.platform.Services;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.MessageDigest;
import java.sql.*;
import java.util.UUID;

public class AuthDatabase {
    private static final Logger LOGGER = LogUtils.getLogger();
    private static String dbUrl;

    public static class PlayerData {
        public String hashedPassword;
        public String lastIp;
        public long lastLoginTime;

        public PlayerData(String hashedPassword, String lastIp, long lastLoginTime) {
            this.hashedPassword = hashedPassword;
            this.lastIp = lastIp;
            this.lastLoginTime = lastLoginTime;
        }
    }

    public static void init() {
        try {
            Class.forName("org.sqlite.JDBC", true, AuthDatabase.class.getClassLoader());

            Path dbPath = Services.PLATFORM.getConfigDir().resolve(Constants.MODID).resolve(Constants.MODID + "_auth.db");
            Files.createDirectories(dbPath.getParent());

            dbUrl = "jdbc:sqlite:" + dbPath.toAbsolutePath();

            try (Connection conn = getConnection(); Statement stmt = conn.createStatement()) {
                String sql = "CREATE TABLE IF NOT EXISTS players (" +
                        "uuid VARCHAR(36) PRIMARY KEY, " +
                        "password VARCHAR(64) NOT NULL, " +
                        "last_ip VARCHAR(45) NOT NULL, " +
                        "last_login_time BIGINT NOT NULL DEFAULT 0" +
                        ");";
                stmt.execute(sql);

                stmt.execute("CREATE TABLE IF NOT EXISTS launcher_status (" +
                        "uuid VARCHAR(36) PRIMARY KEY, " +
                        "verified_premium INTEGER NOT NULL DEFAULT 0" +
                        ");");

                migrateAddUsernameColumn(stmt);
            }
            LOGGER.info("Base de datos inicializada en: {}", dbPath.toAbsolutePath());

        } catch (Exception e) {
            LOGGER.error("Error crítico al inicializar la base de datos", e);
        }
    }

    /**
     * Migración segura: añade la columna 'username' si la base de datos venía de una versión anterior.
     * Necesaria para poder detectar conflictos de nombre premium / no-premium.
     */
    private static void migrateAddUsernameColumn(Statement stmt) throws SQLException {
        boolean hasUsername = false;
        try (ResultSet rs = stmt.executeQuery("PRAGMA table_info(players)")) {
            while (rs.next()) {
                if ("username".equalsIgnoreCase(rs.getString("name"))) {
                    hasUsername = true;
                    break;
                }
            }
        }
        if (!hasUsername) {
            stmt.execute("ALTER TABLE players ADD COLUMN username VARCHAR(32)");
            LOGGER.info("[Lumineria] Columna 'username' añadida a la tabla players (migración automática).");
        }
    }

    private static Connection getConnection() throws Exception {
        return DriverManager.getConnection(dbUrl);
    }

    public static @Nullable PlayerData getPlayer(UUID uuid) {
        String sql = "SELECT password, last_ip, last_login_time FROM players WHERE uuid = ?";
        try (Connection conn = getConnection(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, uuid.toString());
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return new PlayerData(rs.getString("password"), rs.getString("last_ip"), rs.getLong("last_login_time"));
            }
        } catch (Exception e) {
            LOGGER.error("Error al obtener los datos del jugador", e);
        }
        return null;
    }

    public static void savePlayer(UUID uuid, String rawPassword, String ip) {
        String sql = "INSERT OR REPLACE INTO players(uuid, password, last_ip, last_login_time, username) " +
                "VALUES(?, ?, ?, ?, COALESCE((SELECT username FROM players WHERE uuid = ?), NULL))";
        try (Connection conn = getConnection(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, uuid.toString());
            pstmt.setString(2, hash(rawPassword));
            pstmt.setString(3, ip);
            pstmt.setLong(4, System.currentTimeMillis());
            pstmt.setString(5, uuid.toString());
            pstmt.executeUpdate();
        } catch (Exception e) {
            LOGGER.error("Error al guardar el jugador", e);
        }
    }

    public static void updateSession(UUID uuid, String ip) {
        String sql = "UPDATE players SET last_ip = ?, last_login_time = ? WHERE uuid = ?";
        try (Connection conn = getConnection(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, ip);
            pstmt.setLong(2, System.currentTimeMillis());
            pstmt.setString(3, uuid.toString());
            pstmt.executeUpdate();
        } catch (Exception e) {
            LOGGER.error("Error al actualizar la sesión", e);
        }
    }

    public static boolean checkPassword(UUID uuid, String rawPassword) {
        PlayerData data = getPlayer(uuid);
        if (data == null) return false;
        return data.hashedPassword.equals(hash(rawPassword));
    }

    public static void setLauncherVerifiedPremium(UUID uuid, boolean verified) {
        String sql = "INSERT INTO launcher_status(uuid, verified_premium) VALUES(?, ?) " +
                "ON CONFLICT(uuid) DO UPDATE SET verified_premium = excluded.verified_premium";
        try (Connection conn = getConnection(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, uuid.toString());
            pstmt.setInt(2, verified ? 1 : 0);
            pstmt.executeUpdate();
        } catch (Exception e) {
            LOGGER.error("Error al guardar el estado premium del launcher", e);
        }
    }

    /**
     * Mantiene actualizado el nombre asociado a un UUID cada vez que ese jugador entra.
     * Si el UUID todavía no tiene fila en 'players' (aún no se registró), no hace nada:
     * la columna se completará en el primer savePlayer().
     */
    public static void touchUsername(UUID uuid, String username) {
        String sql = "UPDATE players SET username = ? WHERE uuid = ?";
        try (Connection conn = getConnection(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, username);
            pstmt.setString(2, uuid.toString());
            pstmt.executeUpdate();
        } catch (Exception e) {
            LOGGER.error("Error actualizando el username en caché", e);
        }
    }

    /**
     * Busca si existe una cuenta PREMIUM (verified_premium = 1) registrada con ese nombre,
     * distinta del uuid que se está conectando ahora.
     */
    public static @Nullable UUID findPremiumOwnerOfName(String username, UUID excludeUuid) {
        String sql = "SELECT p.uuid FROM players p " +
                "JOIN launcher_status l ON p.uuid = l.uuid " +
                "WHERE p.username = ? COLLATE NOCASE AND l.verified_premium = 1 AND p.uuid != ?";
        try (Connection conn = getConnection(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, username);
            pstmt.setString(2, excludeUuid.toString());
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) return UUID.fromString(rs.getString("uuid"));
        } catch (Exception e) {
            LOGGER.error("Error buscando dueño premium del nombre", e);
        }
        return null;
    }

    /**
     * Busca si existe una cuenta NO premium registrada con ese nombre,
     * distinta del uuid que se está conectando ahora (usado cuando un premium recién verificado entra).
     */
    public static @Nullable UUID findNonPremiumOwnerOfName(String username, UUID excludeUuid) {
        String sql = "SELECT p.uuid FROM players p " +
                "LEFT JOIN launcher_status l ON p.uuid = l.uuid " +
                "WHERE p.username = ? COLLATE NOCASE AND p.uuid != ? " +
                "AND (l.verified_premium IS NULL OR l.verified_premium = 0)";
        try (Connection conn = getConnection(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, username);
            pstmt.setString(2, excludeUuid.toString());
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) return UUID.fromString(rs.getString("uuid"));
        } catch (Exception e) {
            LOGGER.error("Error buscando dueño no-premium del nombre", e);
        }
        return null;
    }

    /** Elimina por completo el registro de una cuenta (usado al fusionar datos hacia otra cuenta). */
    public static void deleteAccount(UUID uuid) {
        try (Connection conn = getConnection()) {
            try (PreparedStatement p1 = conn.prepareStatement("DELETE FROM players WHERE uuid = ?")) {
                p1.setString(1, uuid.toString());
                p1.executeUpdate();
            }
            try (PreparedStatement p2 = conn.prepareStatement("DELETE FROM launcher_status WHERE uuid = ?")) {
                p2.setString(1, uuid.toString());
                p2.executeUpdate();
            }
        } catch (Exception e) {
            LOGGER.error("Error eliminando cuenta", e);
        }
    }

    /** Cambia el uuid/username de una cuenta existente (usado al mudar los datos a otro nick). */
    public static void renameAccount(UUID oldUuid, UUID newUuid, String newUsername) {
        try (Connection conn = getConnection()) {
            conn.setAutoCommit(false);
            try (PreparedStatement p1 = conn.prepareStatement(
                    "UPDATE players SET uuid = ?, username = ? WHERE uuid = ?")) {
                p1.setString(1, newUuid.toString());
                p1.setString(2, newUsername);
                p1.setString(3, oldUuid.toString());
                p1.executeUpdate();
            }
            try (PreparedStatement p2 = conn.prepareStatement(
                    "UPDATE launcher_status SET uuid = ? WHERE uuid = ?")) {
                p2.setString(1, newUuid.toString());
                p2.setString(2, oldUuid.toString());
                p2.executeUpdate();
            }
            conn.commit();
        } catch (Exception e) {
            LOGGER.error("Error renombrando cuenta", e);
        }
    }

    public static @NotNull String hash(String password) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] encodedhash = digest.digest(password.getBytes(StandardCharsets.UTF_8));
            StringBuilder hexString = new StringBuilder();
            for (byte b : encodedhash) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) hexString.append('0');
                hexString.append(hex);
            }
            return hexString.toString();
        } catch (Exception e) {
            throw new RuntimeException("Error encriptando contraseña", e);
        }
    }
}
