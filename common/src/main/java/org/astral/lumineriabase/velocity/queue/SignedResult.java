package org.astral.lumineriabase.velocity.queue;

import org.jetbrains.annotations.NotNull;

public class SignedResult {
    private final boolean cancelled;
    private final String toModify;

    private SignedResult(boolean cancelled, String toModify) {
        this.cancelled = cancelled;
        this.toModify = toModify;
    }

    public static @NotNull SignedResult cancel() {
        return new SignedResult(true, null);
    }

    public static @NotNull SignedResult modify(String message) {
        return new SignedResult(false, message);
    }

    public static @NotNull SignedResult allowed() {
        return new SignedResult(false, null);
    }

    public boolean cancelled() {
        return cancelled;
    }

    public String toModify() {
        return toModify;
    }
}