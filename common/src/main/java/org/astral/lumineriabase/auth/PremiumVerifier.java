package org.astral.lumineriabase.auth;

import org.jetbrains.annotations.NotNull;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;

public class PremiumVerifier {
    private static final long CHALLENGE_TTL_MS = 6_000;
    private record PendingChallenge(String serverId, long issuedAt) {}
    private static final Map<UUID, PendingChallenge> pending = new ConcurrentHashMap<>();

    public static @NotNull String beginChallenge(UUID uuid) {
        String serverId = Long.toHexString(java.util.concurrent.ThreadLocalRandom.current().nextLong());
        pending.put(uuid, new PendingChallenge(serverId, System.currentTimeMillis()));
        return serverId;
    }

    public static void cancelChallenge(UUID uuid) {
        pending.remove(uuid);
    }

    public static void checkTimeouts(Consumer<UUID> onTimeout) {
        long now = System.currentTimeMillis();
        pending.forEach((uuid, challenge) -> {
            if (now - challenge.issuedAt() > CHALLENGE_TTL_MS) {
                pending.remove(uuid);
                onTimeout.accept(uuid);
            }
        });
    }
}