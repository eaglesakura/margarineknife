package com.eaglesakura.android.margarine;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Instance Injection
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD})
public @interface BindInstance {
    /**
     * シンプルにnewを行う
     */
    Class value() default Object.class;

    /**
     * ファクトリを提供させる
     */
    Class<? extends InjectionFactory> factory() default InjectionFactory.class;

    /**
     * BinderClass
     */
    Class<? extends FieldBinder> binder() default FieldInstanceBinder.class;
}
