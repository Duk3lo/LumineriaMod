package org.astral.lumineriabase.client;

import org.astral.lumineriabase.presence.PresenceConfig;


public final class ClientPresenceState {
    private static volatile int serverMaxPlayers = PresenceConfig.SERVER_MAX_PLAYERS;

    private ClientPresenceState() {}

    public static void setServerMaxPlayers(int maxPlayers) {
        serverMaxPlayers = maxPlayers;
    }

    public static int getServerMaxPlayers() {
        return serverMaxPlayers;
    }

    public static void reset() {
        serverMaxPlayers = PresenceConfig.SERVER_MAX_PLAYERS;
    }
}
