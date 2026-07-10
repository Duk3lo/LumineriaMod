package org.astral.lumineriabase.neoforge.setup;

import net.neoforged.fml.event.config.ModConfigEvent;
import net.neoforged.neoforge.common.ModConfigSpec;

// ELIMINADO: @EventBusSubscriber
public class NeoForgeConfig {
    private static final ModConfigSpec.Builder BUILDER = new ModConfigSpec.Builder();

    private static final ModConfigSpec.IntValue LOGIN_TIMEOUT = BUILDER
            .comment("Tiempo maximo (segundos) para loguearse.")
            .defineInRange("loginTimeoutSeconds", 30, 10, 600);

    private static final ModConfigSpec.IntValue SESSION_EXPIRATION = BUILDER
            .comment("Tiempo maximo (horas) de sesion por IP.")
            .defineInRange("sessionExpirationHours", 3, 1, 720);

    public static final ModConfigSpec SPEC = BUILDER.build();

    public static int loginTimeoutSeconds;
    public static int sessionExpirationHours;

    public static void onLoad(final ModConfigEvent event) {
        loginTimeoutSeconds = LOGIN_TIMEOUT.get();
        sessionExpirationHours = SESSION_EXPIRATION.get();
    }
}