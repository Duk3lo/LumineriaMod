package org.astral.lumineriabase.forge.client;

import net.minecraftforge.client.ConfigScreenHandler;
import net.minecraftforge.fml.ModLoadingContext;
import org.astral.lumineriabase.client.RoutingConfigScreen;

public class ForgeClientHelper {
    @SuppressWarnings("removal")
    public static void registerConfigScreen() {
        ModLoadingContext.get().registerExtensionPoint(ConfigScreenHandler.ConfigScreenFactory.class,
                () -> new ConfigScreenHandler.ConfigScreenFactory((mc, parent) -> new RoutingConfigScreen(parent))
        );
    }
}