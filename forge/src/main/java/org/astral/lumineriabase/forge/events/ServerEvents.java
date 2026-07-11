package org.astral.lumineriabase.forge.events;

import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.event.ServerChatEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.item.ItemTossEvent;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.event.level.BlockEvent;
import net.minecraftforge.event.server.ServerStartingEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.astral.lumineriabase.Constants;
import org.astral.lumineriabase.auth.AuthDatabase;
import org.astral.lumineriabase.auth.ServerAuthManager;
import org.jetbrains.annotations.NotNull;

@Mod.EventBusSubscriber(modid = Constants.MODID)
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
    public static void onServerTick(TickEvent.@NotNull ServerTickEvent event) {
        if (event.phase == TickEvent.Phase.END) {
            ServerAuthManager.onServerTick(event.getServer());
        }
    }

    @SubscribeEvent
    public static void onChat(@NotNull ServerChatEvent event) {
        if (ServerAuthManager.isPending(event.getPlayer().getUUID())) {
            event.setCanceled(true);
            event.getPlayer().sendSystemMessage(Component.literal("§cLogueate primero."));
        }
    }

    @SubscribeEvent
    public static void onDamage(@NotNull LivingAttackEvent event) {
        if (event.getEntity() instanceof ServerPlayer p && ServerAuthManager.isPending(p.getUUID())) {
            event.setCanceled(true);
        }
    }

    @SubscribeEvent
    public static void onItemDrop(@NotNull ItemTossEvent event) {
        if (ServerAuthManager.isPending(event.getPlayer().getUUID())) event.setCanceled(true);
    }

    @SubscribeEvent
    public static void onBlockBreak(BlockEvent.@NotNull BreakEvent event) {
        if (ServerAuthManager.isPending(event.getPlayer().getUUID())) event.setCanceled(true);
    }

    @SubscribeEvent
    public static void onInteract(@NotNull PlayerInteractEvent event) {
        if (ServerAuthManager.isPending(event.getEntity().getUUID())) event.setCanceled(true);
    }
}