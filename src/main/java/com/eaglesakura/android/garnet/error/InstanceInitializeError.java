package com.eaglesakura.android.garnet.error;

public class InstanceInitializeError extends Error {
    public InstanceInitializeError() {
    }

    public InstanceInitializeError(String detailMessage) {
        super(detailMessage);
    }

    public InstanceInitializeError(String detailMessage, Throwable throwable) {
        super(detailMessage, throwable);
    }

    public InstanceInitializeError(Throwable throwable) {
        super(throwable);
    }
}
