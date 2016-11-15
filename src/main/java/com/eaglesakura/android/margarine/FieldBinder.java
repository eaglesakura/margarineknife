package com.eaglesakura.android.margarine;

import android.support.annotation.Keep;

@Keep
public interface FieldBinder {
    void apply(InjectionClass srcClass, Object src, InjectionClass dstClass, Object dst);
}
