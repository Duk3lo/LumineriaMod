package org.astral.lumineriabase.velocity.queue;

public enum SignedResult {
    CANCEL,
    MODIFY,
    ALLOWED;

    public static SignedResult cancel() {
        return CANCEL;
    }

    public static SignedResult modify() {
        return MODIFY;
    }

    public static SignedResult allowed() {
        return ALLOWED;
    }
}