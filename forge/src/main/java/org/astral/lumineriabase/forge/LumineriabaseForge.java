package org.astral.lumineriabase.forge;

import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.astral.lumineriabase.Constants;
import org.astral.lumineriabase.forge.setup.ForgeConfig;
import org.astral.lumineriabase.forge.network.ForgeNetwork;

@Mod(Constants.MODID)
public class LumineriabaseForge {
    @SuppressWarnings("removal")
    public LumineriabaseForge() {
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();
        ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, ForgeConfig.SPEC, Constants.MODID + "/config.toml");
        modEventBus.addListener(ForgeNetwork::init);
        modEventBus.addListener(ForgeConfig::onLoad);
    }
}