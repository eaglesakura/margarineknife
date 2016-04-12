package com.eaglesakura.android.margarine;

public class MenuNotFoundError extends Error {
    public MenuNotFoundError() {
        super();
    }

    public MenuNotFoundError(String detailMessage) {
        super(detailMessage);
    }

    public MenuNotFoundError(String detailMessage, Throwable throwable) {
        super(detailMessage, throwable);
    }

    public MenuNotFoundError(Throwable throwable) {
        super(throwable);
    }
}
