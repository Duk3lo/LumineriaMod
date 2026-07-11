package org.astral.lumineriabase.forge.setup;

import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.fml.event.config.ModConfigEvent;
import org.jetbrains.annotations.NotNull;

public class ForgeConfig {

    private static final ForgeConfigSpec.Builder COMMON_BUILDER = new ForgeConfigSpec.Builder();

    private static final ForgeConfigSpec.IntValue LOGIN_TIMEOUT = COMMON_BUILDER
            .comment("Tiempo maximo (segundos) para loguearse.")
            .defineInRange("loginTimeoutSeconds", 30, 10, 600);

    private static final ForgeConfigSpec.IntValue SESSION_EXPIRATION = COMMON_BUILDER
            .comment("Tiempo maximo (horas) de sesion por IP.")
            .defineInRange("sessionExpirationHours", 3, 1, 720);

    public static final ForgeConfigSpec COMMON_SPEC = COMMON_BUILDER.build();

    private static final ForgeConfigSpec.Builder CLIENT_BUILDER = new ForgeConfigSpec.Builder();

    public static final ForgeConfigSpec.ConfigValue<String> ROUTING_KEY_VALUE = CLIENT_BUILDER
            .comment("La llave de enrutamiento que se enviará al proxy (ej: 'terror', 'tecnico', 'lobby')")
            .define("routingKey", "default_forge");

    public static final ForgeConfigSpec CLIENT_SPEC = CLIENT_BUILDER.build();

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