package com.eaglesakura.android.garnet.error;

public class DefaultConstructorNotFoundError extends Error {
    public DefaultConstructorNotFoundError() {
    }

    public DefaultConstructorNotFoundError(String detailMessage) {
        super(detailMessage);
    }

    public DefaultConstructorNotFoundError(String detailMessage, Throwable throwable) {
        super(detailMessage, throwable);
    }

    public DefaultConstructorNotFoundError(Throwable throwable) {
        super(throwable);
    }
}
