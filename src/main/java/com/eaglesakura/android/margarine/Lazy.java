package com.eaglesakura.android.margarine;

/**
 * 値を遅延取得する
 *
 * @param <T> データ型
 */
public interface Lazy<T> {
    /**
     * 値を取得する
     */
    T get();

    /**
     * 値を削除する
     */
    void clear();
}
