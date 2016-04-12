package com.eaglesakura.android.margarine;

public class ViewNotFoundError extends Error {
    public ViewNotFoundError() {
    }

    public ViewNotFoundError(String detailMessage) {
        super(detailMessage);
    }

    public ViewNotFoundError(String detailMessage, Throwable throwable) {
        super(detailMessage, throwable);
    }

    public ViewNotFoundError(Throwable throwable) {
        super(throwable);
    }
}
