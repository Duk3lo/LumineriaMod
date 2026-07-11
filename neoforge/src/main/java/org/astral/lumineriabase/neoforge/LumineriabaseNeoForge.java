package org.astral.lumineriabase.neoforge;

import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.loading.FMLEnvironment;
import org.astral.lumineriabase.Constants;
import org.astral.lumineriabase.neoforge.setup.NeoForgeConfig;
import org.astral.lumineriabase.neoforge.network.NeoForgeNetwork;
import org.astral.lumineriabase.neoforge.client.NeoForgeClientHelper; // Nuevo import
import org.jetbrains.annotations.NotNull;

@Mod(Constants.MODID)
public class LumineriabaseNeoForge {
    public LumineriabaseNeoForge(@NotNull IEventBus modEventBus, @NotNull ModContainer modContainer) {
        modContainer.registerConfig(ModConfig.Type.COMMON, NeoForgeConfig.COMMON_SPEC, Constants.MODID + "/common.toml");
        modContainer.registerConfig(ModConfig.Type.CLIENT, NeoForgeConfig.CLIENT_SPEC, Constants.MODID + "/client.toml");
        modEventBus.addListener(NeoForgeNetwork::register);
        modEventBus.addListener(NeoForgeConfig::onLoad);

        if (FMLEnvironment.dist.isClient()) {
            NeoForgeClientHelper.registerConfigScreen(modContainer);
        }
    }
}