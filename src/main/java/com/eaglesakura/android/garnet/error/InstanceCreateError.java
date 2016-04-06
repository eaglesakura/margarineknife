package com.eaglesakura.android.garnet.error;

public class InstanceCreateError extends Error {
    public InstanceCreateError() {
    }

    public InstanceCreateError(String detailMessage) {
        super(detailMessage);
    }

    public InstanceCreateError(String detailMessage, Throwable throwable) {
        super(detailMessage, throwable);
    }

    public InstanceCreateError(Throwable throwable) {
        super(throwable);
    }
}
