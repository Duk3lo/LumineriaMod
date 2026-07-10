package org.astral.lumineriabase.velocity.queue;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class SignedQueue {
    private final Map<UUID, QueuedData> dataMap = new ConcurrentHashMap<>();

    public QueuedData dataFrom(UUID uuid) {
        return dataMap.computeIfAbsent(uuid, k -> new QueuedData());
    }

    public void removeData(UUID uuid) {
        dataMap.remove(uuid);
    }
}