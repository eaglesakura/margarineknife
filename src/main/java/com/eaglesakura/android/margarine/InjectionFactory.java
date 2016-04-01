package com.eaglesakura.android.margarine;

import java.lang.reflect.Field;

public interface InjectionFactory<T> {
    /**
     * ファクトリメソッドを提供させる
     *
     * @param srcClass Injection先のClass解析
     * @param src      Injection元のインスタンス
     * @param srcClass Injection先のClass解析
     * @param dst      Injection先のインスタンス
     * @param field    書き込み先のフィールド
     */
    T newInstance(InjectionClass srcClass, Object src, InjectionClass dstClass, Object dst, Field field);
}
