package org.astral.lumineriabase.forge.setup;

import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.fml.event.config.ModConfigEvent;

public class ForgeConfig {
    private static final ForgeConfigSpec.Builder BUILDER = new ForgeConfigSpec.Builder();

    private static final ForgeConfigSpec.IntValue LOGIN_TIMEOUT = BUILDER
            .comment("Tiempo maximo (segundos) para loguearse.")
            .defineInRange("loginTimeoutSeconds", 30, 10, 600);

    private static final ForgeConfigSpec.IntValue SESSION_EXPIRATION = BUILDER
            .comment("Tiempo maximo (horas) de sesion por IP.")
            .defineInRange("sessionExpirationHours", 3, 1, 720);

    public static final ForgeConfigSpec SPEC = BUILDER.build();

    public static int loginTimeoutSeconds;
    public static int sessionExpirationHours;

    public static void onLoad(final ModConfigEvent event) {
        loginTimeoutSeconds = LOGIN_TIMEOUT.get();
        sessionExpirationHours = SESSION_EXPIRATION.get();
    }
}