package org.astral.lumineriabase.presence;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Properties;

public final class PresenceConfig {

    public static final String SERVER_NAME;
    public static final String SERVER_ICON_URL;
    public static final int SERVER_MAX_PLAYERS;

    static {
        Properties props = new Properties();
        Path configPath = Path.of("config", "lumineriabase-presence.properties");

        try {
            if (!Files.exists(configPath)) {
                props.setProperty("server_name", "Lumineria SMP");
                props.setProperty("server_icon_url", "");
                props.setProperty("server_max_players", "50");
                Files.createDirectories(configPath.getParent());
                try (FileOutputStream out = new FileOutputStream(configPath.toFile())) {
                    props.store(out, "Config de Rich Presence para Lumineria Launcher");
                }
            } else {
                try (FileInputStream in = new FileInputStream(configPath.toFile())) {
                    props.load(in);
                }
            }
        } catch (IOException ignored) { }

        SERVER_NAME = props.getProperty("server_name", "Lumineria SMP");
        String icon = props.getProperty("server_icon_url", "");
        SERVER_ICON_URL = icon.isEmpty() ? null : icon;
        SERVER_MAX_PLAYERS = parseIntOrDefault(props.getProperty("server_max_players"), 50);
    }

    private static int parseIntOrDefault(String value, int fallback) {
        try {
            return Integer.parseInt(value);
        } catch (Exception e) {
            return fallback;
        }
    }

    private PresenceConfig() { }
}