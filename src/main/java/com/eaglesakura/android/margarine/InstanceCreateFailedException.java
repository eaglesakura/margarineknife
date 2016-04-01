package com.eaglesakura.android.margarine;

public class InstanceCreateFailedException extends RuntimeException {
    public InstanceCreateFailedException() {
    }

    public InstanceCreateFailedException(String detailMessage) {
        super(detailMessage);
    }

    public InstanceCreateFailedException(String detailMessage, Throwable throwable) {
        super(detailMessage, throwable);
    }

    public InstanceCreateFailedException(Throwable throwable) {
        super(throwable);
    }
}
