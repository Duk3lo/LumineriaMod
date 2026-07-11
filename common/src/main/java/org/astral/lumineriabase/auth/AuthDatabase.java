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

            // Obtenemos la ruta dinámicamente según la plataforma
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
            }
            LOGGER.info("Base de datos inicializada en: {}", dbPath.toAbsolutePath());

        } catch (Exception e) {
            LOGGER.error("Error crítico al inicializar la base de datos", e);
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
        String sql = "INSERT OR REPLACE INTO players(uuid, password, last_ip, last_login_time) VALUES(?, ?, ?, ?)";
        try (Connection conn = getConnection(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, uuid.toString());
            pstmt.setString(2, hash(rawPassword));
            pstmt.setString(3, ip);
            pstmt.setLong(4, System.currentTimeMillis());
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