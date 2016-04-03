package com.eaglesakura.android.margarine;

public interface FieldBinder {
    void apply(InjectionClass srcClass, Object src, InjectionClass dstClass, Object dst);

    /**
     * 遅延注入用オブジェクトを取得する
     */
    Object lazy(InjectionClass srcClass, Object src, InjectionClass dstClass, Object dst);
}
