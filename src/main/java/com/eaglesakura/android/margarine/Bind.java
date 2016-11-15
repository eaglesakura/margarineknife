package com.eaglesakura.android.margarine;

import android.support.annotation.IdRes;
import android.support.annotation.Keep;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * View Injection
 */
@Keep
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD})
public @interface Bind {
    /**
     * R.id.**
     */
    @Keep
    @IdRes int value() default 0;

    /**
     * R.id."resName"
     */
    @Keep
    String resName() default "";

    /**
     * BinderClass
     */
    @Keep
    Class binder() default FieldResourceBinder.FieldBinderView.class;

    /**
     * Null許容する場合true
     */
    @Keep
    boolean nullable() default false;
}
