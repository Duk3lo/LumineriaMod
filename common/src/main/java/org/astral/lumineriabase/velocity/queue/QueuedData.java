package org.astral.lumineriabase.velocity.queue;

import java.util.Queue;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentLinkedQueue;

public class QueuedData {
    private final Queue<CompletableFuture<SignedResult>> futures = new ConcurrentLinkedQueue<>();
    private final Queue<SignedResult> results = new ConcurrentLinkedQueue<>();

    public synchronized CompletableFuture<SignedResult> nextResult() {
        if (!results.isEmpty()) {
            return CompletableFuture.completedFuture(results.poll());
        }
        CompletableFuture<SignedResult> future = new CompletableFuture<>();
        futures.add(future);
        return future;
    }

    public synchronized void complete(SignedResult result) {
        if (!futures.isEmpty()) {
            futures.poll().complete(result);
        } else {
            results.add(result);
        }
    }
}