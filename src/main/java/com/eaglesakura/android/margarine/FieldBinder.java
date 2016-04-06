package com.eaglesakura.android.margarine;

public interface FieldBinder {
    void apply(InjectionClass srcClass, Object src, InjectionClass dstClass, Object dst);
}
