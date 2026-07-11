package org.astral.lumineriabase.forge;

import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.loading.FMLEnvironment;
import org.astral.lumineriabase.Constants;
import org.astral.lumineriabase.forge.setup.ForgeConfig;
import org.astral.lumineriabase.forge.network.ForgeNetwork;
import org.astral.lumineriabase.forge.client.ForgeClientHelper;

@Mod(Constants.MODID)
public class LumineriabaseForge {
    @SuppressWarnings("removal")
    public LumineriabaseForge() {
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();
        ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, ForgeConfig.COMMON_SPEC, Constants.MODID + "/common.toml");
        ModLoadingContext.get().registerConfig(ModConfig.Type.CLIENT, ForgeConfig.CLIENT_SPEC, Constants.MODID + "/client.toml");
        modEventBus.addListener(ForgeNetwork::init);
        modEventBus.addListener(ForgeConfig::onLoad);

        if (FMLEnvironment.dist.isClient()) {
            ForgeClientHelper.registerConfigScreen();
        }
    }
}