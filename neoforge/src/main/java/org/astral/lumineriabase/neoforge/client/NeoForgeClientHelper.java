package org.astral.lumineriabase.neoforge.client;

import net.neoforged.fml.ModContainer;
import net.neoforged.neoforge.client.gui.IConfigScreenFactory;
import org.astral.lumineriabase.client.RoutingConfigScreen;
import org.jetbrains.annotations.NotNull;

public class NeoForgeClientHelper {
    public static void registerConfigScreen(@NotNull ModContainer modContainer) {
        modContainer.registerExtensionPoint(IConfigScreenFactory.class,
                (mc, parent) -> new RoutingConfigScreen(parent)
        );
    }
}