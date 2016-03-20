package com.eaglesakura.android.margarine;

public class MethodBindError extends Error {
    public MethodBindError(String detailMessage) {
        super(detailMessage);
    }

    public MethodBindError(String detailMessage, Throwable throwable) {
        super(detailMessage, throwable);
    }

    public MethodBindError(Throwable throwable) {
        super(throwable);
    }
}
