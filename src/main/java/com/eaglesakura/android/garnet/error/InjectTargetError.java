package com.eaglesakura.android.garnet.error;

public class InjectTargetError extends Error {
    public InjectTargetError() {
    }

    public InjectTargetError(String detailMessage) {
        super(detailMessage);
    }

    public InjectTargetError(String detailMessage, Throwable throwable) {
        super(detailMessage, throwable);
    }

    public InjectTargetError(Throwable throwable) {
        super(throwable);
    }
}
