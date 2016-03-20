package com.eaglesakura.android.margarine;

public class ResourceBindError extends Error {
    public ResourceBindError(String detailMessage) {
        super(detailMessage);
    }

    public ResourceBindError(String detailMessage, Throwable throwable) {
        super(detailMessage, throwable);
    }

    public ResourceBindError(Throwable throwable) {
        super(throwable);
    }
}
