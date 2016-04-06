package com.eaglesakura.android.garnet;

import java.lang.reflect.Field;

public interface ComponentFactory<Instance, Target> {
    /**
     * 対象のインスタンスを生成する
     */
    Instance newInstance(Field field, Target target);

    /**
     * 対象の初期化を行う
     */
    void initialize(Field field, Target target, Instance obj);
}
