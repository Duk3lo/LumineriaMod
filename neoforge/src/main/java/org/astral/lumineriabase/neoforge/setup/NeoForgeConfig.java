package org.astral.lumineriabase.neoforge.setup;

import net.neoforged.fml.event.config.ModConfigEvent;
import net.neoforged.neoforge.common.ModConfigSpec;
import org.jetbrains.annotations.NotNull;

public class NeoForgeConfig {

    // ---- COMMON: se carga en servidor y cliente ----
    private static final ModConfigSpec.Builder COMMON_BUILDER = new ModConfigSpec.Builder();

    private static final ModConfigSpec.IntValue LOGIN_TIMEOUT = COMMON_BUILDER
            .comment("Tiempo maximo (segundos) para loguearse.")
            .defineInRange("loginTimeoutSeconds", 30, 10, 600);

    private static final ModConfigSpec.IntValue SESSION_EXPIRATION = COMMON_BUILDER
            .comment("Tiempo maximo (horas) de sesion por IP.")
            .defineInRange("sessionExpirationHours", 3, 1, 720);

    public static final ModConfigSpec COMMON_SPEC = COMMON_BUILDER.build();

    // ---- CLIENT: SOLO existe/carga en el cliente físico ----
    private static final ModConfigSpec.Builder CLIENT_BUILDER = new ModConfigSpec.Builder();

    public static final ModConfigSpec.ConfigValue<String> ROUTING_KEY_VALUE = CLIENT_BUILDER
            .comment("La llave de enrutamiento que se enviará al proxy")
            .define("routingKey", "default_neoforge");

    public static final ModConfigSpec CLIENT_SPEC = CLIENT_BUILDER.build();

    public static String routingKey;
    public static int loginTimeoutSeconds;
    public static int sessionExpirationHours;

    @SuppressWarnings("unused")
    public static void onLoad(final @NotNull ModConfigEvent event) {
        var spec = event.getConfig().getSpec();
        if (spec == COMMON_SPEC) {
            loginTimeoutSeconds = LOGIN_TIMEOUT.get();
            sessionExpirationHours = SESSION_EXPIRATION.get();
        } else if (spec == CLIENT_SPEC) {
            routingKey = ROUTING_KEY_VALUE.get();
        }
    }
}