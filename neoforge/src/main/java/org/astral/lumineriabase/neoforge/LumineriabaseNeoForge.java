package org.astral.lumineriabase.neoforge;

import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import org.astral.lumineriabase.Constants;
import org.astral.lumineriabase.neoforge.setup.NeoForgeConfig;
import org.astral.lumineriabase.neoforge.network.NeoForgeNetwork;
import org.jetbrains.annotations.NotNull;

@Mod(Constants.MODID)
public class LumineriabaseNeoForge {

    public LumineriabaseNeoForge(@NotNull IEventBus modEventBus, @NotNull ModContainer modContainer) {
        modContainer.registerConfig(ModConfig.Type.COMMON, NeoForgeConfig.SPEC, Constants.MODID + "/config.toml");

        modEventBus.addListener(NeoForgeNetwork::register);
        modEventBus.addListener(NeoForgeConfig::onLoad);
    }
}