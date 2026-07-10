package org.astral.lumineriabase.neoforge.events;

import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.ServerChatEvent;
import net.neoforged.neoforge.event.entity.item.ItemTossEvent;
import net.neoforged.neoforge.event.entity.living.LivingIncomingDamageEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;
import net.neoforged.neoforge.event.level.BlockEvent;
import net.neoforged.neoforge.event.server.ServerStartingEvent;
import net.neoforged.neoforge.event.tick.ServerTickEvent;
import org.astral.lumineriabase.Constants;
import org.astral.lumineriabase.auth.AuthDatabase;
import org.astral.lumineriabase.auth.ServerAuthManager;
import org.jetbrains.annotations.NotNull;

@EventBusSubscriber(modid = Constants.MODID)
public class ServerEvents {

    @SubscribeEvent
    public static void onServerStart(ServerStartingEvent event) {
        AuthDatabase.init();
    }

    @SubscribeEvent
    public static void onPlayerJoin(PlayerEvent.@NotNull PlayerLoggedInEvent event) {
        if (event.getEntity() instanceof ServerPlayer player) {
            ServerAuthManager.onPlayerJoin(player);
        }
    }

    @SubscribeEvent
    public static void onPlayerQuit(PlayerEvent.@NotNull PlayerLoggedOutEvent event) {
        if (event.getEntity() instanceof ServerPlayer player) {
            ServerAuthManager.onPlayerQuit(player);
        }
    }

    @SubscribeEvent
    public static void onServerTick(ServerTickEvent.@NotNull Post event) {
        ServerAuthManager.onServerTick(event.getServer());
    }

    @SubscribeEvent
    public static void onChat(@NotNull ServerChatEvent e) {
        if (ServerAuthManager.isPending(e.getPlayer().getUUID())) {
            e.setCanceled(true);
            e.getPlayer().sendSystemMessage(Component.literal("§cLogueate primero."));
        }
    }

    @SubscribeEvent
    public static void onDamage(@NotNull LivingIncomingDamageEvent e) {
        if (e.getEntity() instanceof ServerPlayer p && ServerAuthManager.isPending(p.getUUID())) e.setCanceled(true);
    }

    @SubscribeEvent
    public static void onItemDrop(@NotNull ItemTossEvent e) {
        if (ServerAuthManager.isPending(e.getPlayer().getUUID())) e.setCanceled(true);
    }

    @SubscribeEvent
    public static void onBlockBreak(BlockEvent.@NotNull BreakEvent e) {
        if (ServerAuthManager.isPending(e.getPlayer().getUUID())) e.setCanceled(true);
    }

    @SubscribeEvent
    public static void onRCBlock(PlayerInteractEvent.@NotNull RightClickBlock e) {
        if (ServerAuthManager.isPending(e.getEntity().getUUID())) e.setCanceled(true);
    }

    @SubscribeEvent
    public static void onRCItem(PlayerInteractEvent.@NotNull RightClickItem e) {
        if (ServerAuthManager.isPending(e.getEntity().getUUID())) e.setCanceled(true);
    }

    @SubscribeEvent
    public static void onEntInt(PlayerInteractEvent.@NotNull EntityInteract e) {
        if (ServerAuthManager.isPending(e.getEntity().getUUID())) e.setCanceled(true);
    }

    @SubscribeEvent
    public static void onLCBlock(PlayerInteractEvent.@NotNull LeftClickBlock e) {
        if (ServerAuthManager.isPending(e.getEntity().getUUID())) e.setCanceled(true);
    }
}