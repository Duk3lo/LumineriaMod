package org.astral.lumineriabase.forge.platform;

import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import io.netty.buffer.Unpooled;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.game.ClientboundCustomPayloadPacket;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.fml.loading.FMLPaths;
import net.minecraftforge.network.PacketDistributor;
import org.astral.lumineriabase.platform.IPlatformHelper;
import org.astral.lumineriabase.forge.setup.ForgeConfig;
import org.astral.lumineriabase.forge.network.ForgeNetwork;
import org.astral.lumineriabase.forge.network.packets.OpenLoginScreenPacket;
import org.jetbrains.annotations.NotNull;

import java.nio.file.Path;


public class ForgePlatformHelper implements IPlatformHelper {

    @Override
    public Path getConfigDir() {
        return FMLPaths.CONFIGDIR.get();
    }

    @Override
    public int getLoginTimeoutSeconds() {
        return ForgeConfig.loginTimeoutSeconds;
    }

    @Override
    public int getSessionExpirationHours() {
        return ForgeConfig.sessionExpirationHours;
    }

    @Override
    public void sendOpenLogin(ServerPlayer player, boolean isRegistered, String message) {
        ForgeNetwork.CHANNEL.send(PacketDistributor.PLAYER.with(() -> player), new OpenLoginScreenPacket(isRegistered, message));
    }

    @Override
    @SuppressWarnings("UnstableApiUsage")
    public void sendAuthVelocity(@NotNull ServerPlayer player, String action, String playerName) {
        FriendlyByteBuf buf = new FriendlyByteBuf(Unpooled.buffer());
        ByteArrayDataOutput out = ByteStreams.newDataOutput();
        out.writeUTF(action);
        out.writeUTF(playerName);
        buf.writeBytes(out.toByteArray());
        @SuppressWarnings("removal")
        ResourceLocation channel = new ResourceLocation("authmevelocity", "main");
        ClientboundCustomPayloadPacket packet = new ClientboundCustomPayloadPacket(channel, buf);
        player.connection.send(packet);
    }


    @Override
    public String getCurrentRoutingKey() {
        return ForgeConfig.routingKey;
    }

    @Override
    public void saveRoutingKey(String key) {
        ForgeConfig.routingKey = key;
        ForgeConfig.ROUTING_KEY_VALUE.set(key);
        ForgeConfig.CLIENT_SPEC.save();
    }

    @Override
    public void renderBackground(Object graphics, int mouseX, int mouseY, float partialTick) {
        if (graphics instanceof net.minecraft.client.gui.GuiGraphics guiGraphics) {
            if (net.minecraft.client.Minecraft.getInstance().screen != null) {
                net.minecraft.client.Minecraft.getInstance().screen.renderBackground(guiGraphics);
            }
        }
    }
}